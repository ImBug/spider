package oro.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import oro.json.CustomObjectMapper;

public class HttpRequest {
	
	public static final int CONNECT_TIMEOUT_DEFAULT = 5 * 1000;
	public static final int READ_TIMEOUT_DEFAINT = 8 * 60 * 1000;	
	public static final int REQ_PUT = 1;
	public static final int REQ_GET = 2;
	public static final int REQ_POS = 3;
	public static final int REQ_DEL = 4;

	private int connectTimeout;
	private int readTimeout;
	private String encoding = "UTF-8";	
	private String url;
	private int method;
	
	private Map<String,String> requestHeaders = new LinkedHashMap<String, String>(20);
	private Map<String,String> parameters = new LinkedHashMap<String, String>(20);
	private String fullUrl;
	private String postParam;
	
	public HttpRequest(String url, int method) {
		super();
		this.url = url;
		this.method = method;
		this.connectTimeout = CONNECT_TIMEOUT_DEFAULT;
		this.readTimeout = READ_TIMEOUT_DEFAINT;
		requestHeaders.put("Content-Type", "application/json;charset=" + encoding);
		requestHeaders.put("Accept-Language", "zh-CN");
	}
	
	private String encode(String str){
		try {
			return URLEncoder.encode(str, getEncoding());
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}
	
	public boolean isPost(){
		return method == REQ_POS;
	}
	
	/**
	 * 完整路径
	 * @return
	 */
	public String getFullUrl() {
		if (fullUrl == null) {
			this.fullUrl = url;
			if(!this.fullUrl.contains("?")){
				this.fullUrl += "?";
			}
			if(parameters.size() > 0){
				this.fullUrl += getParamString();
			}
		}
		return fullUrl;
	}
	
	private String getParamString(){
		if(parameters.size() > 0){
			StringBuffer p = new StringBuffer();
			for(String param:parameters.keySet()){
				p.append(String.format("&%s=%s",param,encode(parameters.get(param))));
			}
			if(!this.url.contains("&"))p.deleteCharAt(0);
			return p.toString();
		}
		return "";
	}

	public String getPostParam() {
		if(postParam == null){
			return getParamString();
		}
		return postParam;
	}

	public void setPostParam(String postParam) {
		this.postParam = postParam;
	}

	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}


	public void setHead(String key,String val){
		if(key != null)
			requestHeaders.put(key, val);
	}
	
	public void setParam(String key,String val){
		if(key != null)
			parameters.put(key, val);
	}
	
	public void setParamObj(String key,Object obj){
		if(key != null)
			parameters.put(key, CustomObjectMapper.encodeJson(obj));
	}
	
	
	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public String getMothodStr(){
		switch (getMethod()) {
			case REQ_PUT: return "PUT";
			case REQ_GET: return "GET";
			case REQ_POS: return "POST";
			case REQ_DEL: return "DELETE";
			default:
				return "GET";
			}
	}
	
	public int getConnectTimeout() {
		return connectTimeout;
	}
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	public int getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public int getMethod() {
		return method;
	}

	public void setMethod(int method) {
		this.method = method;
	}
	
	
}
