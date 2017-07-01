package oro.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import oro.util.number.NumberUtil;
import oro.util.thread.ThreadUtil;

public class HttpClient {
	
	private static int delay = 30;//ms
	private final static Log logger = LogFactory.getLog(HttpClient.class);
	private static HttpClient instance;
	private long sendtime;
	private long lastReqUrl;
	
	
	public static HttpClient getDefault() {
		if(instance != null) return instance;
		else{
			instance = new HttpClient();
			return instance;
		}
	}
	/**
	 * 限制同一系统请求过于频繁
	 * @param req
	 */
	private synchronized void delay(HttpRequest req){
		int _delay = delay;
		if(req.getMethod() == HttpRequest.REQ_POS){
			_delay = delay + 10;
		}
		long now = System.currentTimeMillis();
		long url = Long.valueOf(NumberUtil.fetchNumberFrom(req.getUrl(), '/'));
		if((now - sendtime) > _delay){
		}else{
			if(url == lastReqUrl){
				ThreadUtil.sleep(_delay);
			}
		}
		sendtime = System.currentTimeMillis();
		lastReqUrl = url;
	}
	
	public HttpResponse send(HttpRequest req)throws Exception{
		//delay(req);
		if(logger.isDebugEnabled())logger.debug(req.getFullUrl());
		HttpResponse httpResponser = new HttpResponse();
		HttpURLConnection 	urlConnection = null;
		HttpsURLConnection  urlConnections = null;
		try {
			String ecod = req.getEncoding();
			InputStream in = null;
			try {
				if(req instanceof JksHttpsRequest){
					urlConnections = openHttpsConnection((JksHttpsRequest)req);
					in = urlConnections.getInputStream();
				}else{
					urlConnection = openConnection(req);
					in = urlConnection.getInputStream();
				}
			} catch (IOException e) {			
				if(urlConnection != null){
					in = urlConnection.getErrorStream();
				}
				if (in == null)throw e;
			}
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, Charset.forName(ecod)));
			httpResponser.contentCollection = new Vector<String>();
			StringBuilder temp = new StringBuilder();
			String line = bufferedReader.readLine();
			while (line != null) {
				httpResponser.contentCollection.add(line);
				temp.append(line).append("\r\n");
				line = bufferedReader.readLine();
			}
			bufferedReader.close();


			httpResponser.urlString = req.getFullUrl();

			URL url = null;
			if(urlConnection != null){
				url =  urlConnection.getURL();
			}else{
				url =  urlConnections.getURL();
			}
			httpResponser.defaultPort = url.getDefaultPort();
			httpResponser.file = url.getFile();
			httpResponser.host = url.getHost();
			httpResponser.path = url.getPath();
			httpResponser.port = url.getPort();
			httpResponser.protocol = url.getProtocol();
			httpResponser.query = url.getQuery();
			httpResponser.ref = url.getRef();
			httpResponser.userInfo = url.getUserInfo();

			httpResponser.content = temp.toString();
			httpResponser.contentEncoding = ecod;
			
			if(urlConnection != null){
				httpResponser.code = urlConnection.getResponseCode();
				httpResponser.message = urlConnection.getResponseMessage();
				httpResponser.contentType = urlConnection.getContentType();
				httpResponser.method = urlConnection.getRequestMethod();
				httpResponser.connectTimeout = urlConnection.getConnectTimeout();
				httpResponser.readTimeout = urlConnection.getReadTimeout();
			}else{
				httpResponser.code = urlConnections.getResponseCode();
				httpResponser.message = urlConnections.getResponseMessage();
				httpResponser.contentType = urlConnections.getContentType();
				httpResponser.method = urlConnections.getRequestMethod();
				httpResponser.connectTimeout = urlConnections.getConnectTimeout();
				httpResponser.readTimeout = urlConnections.getReadTimeout();
			}
			return httpResponser;
		} catch (IOException e) {
			throw e;
		} finally {
			if (urlConnection != null)urlConnection.disconnect();
			if (urlConnections != null)urlConnections.disconnect();
		}
	}
	
	private HttpURLConnection openConnection(HttpRequest req) throws IOException {
		String urlreq = req.getFullUrl();
		URL url = new URL(urlreq);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(req.getConnectTimeout());
		urlConnection.setReadTimeout(req.getReadTimeout());
		urlConnection.setRequestMethod(req.getMothodStr());
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setUseCaches(false);
		Map<String,String> headerMap = req.getRequestHeaders();
		for(String key:headerMap.keySet()){
			urlConnection.addRequestProperty(key, headerMap.get(key));
		}
		if (req.isPost()) {
			urlConnection.getOutputStream().write(req.getPostParam().getBytes(req.getEncoding()));
			urlConnection.getOutputStream().flush();
			urlConnection.getOutputStream().close();
		}
		return urlConnection;
	}
	
	private HttpsURLConnection openHttpsConnection(JksHttpsRequest req) throws Exception {
		String urlreq = req.getFullUrl();
		URL url = new URL(urlreq);
		HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(req.getConnectTimeout());
		urlConnection.setReadTimeout(req.getReadTimeout());
		urlConnection.setRequestMethod(req.getMothodStr());
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setUseCaches(false);
		Map<String,String> headerMap = req.getRequestHeaders();
		for(String key:headerMap.keySet()){
			urlConnection.addRequestProperty(key, headerMap.get(key));
		}
		if (req.isPost()) {
			urlConnection.getOutputStream().write(req.getPostParam().getBytes(req.getEncoding()));
			urlConnection.getOutputStream().flush();
			urlConnection.getOutputStream().close();
		}
		urlConnection.setSSLSocketFactory(req.getSSLContext().getSocketFactory());
		urlConnection.setHostnameVerifier(JksHttpsRequest.ALLOWALL);
		return urlConnection;
	}
	
}
