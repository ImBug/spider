package oro.service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import oro.util.thread.ThreadUtil;

/**
 * 文件变化监听器
 * @author honghm
 *
 */
public class FileMonitor extends Thread{
	
	private final static Log logger = LogFactory.getLog(FileMonitor.class);
	
	private Path dir;
	private List<FileChangeListener> listeners;
	
	
	public FileMonitor(String dir) {
		super();
		this.dir = Paths.get(System.getProperty("user.dir") + File.separator + dir);
		listeners = new ArrayList<>(5);
	}
	
	public void run(){
		try {
			WatchService service = dir.getFileSystem().newWatchService();
			dir.register(service, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY,StandardWatchEventKinds.ENTRY_DELETE);
			for(;;){
				WatchKey key = service.take();
				if(key != null){
					List<WatchEvent<?>> events = key.pollEvents();
					for(WatchEvent<?> event:events){
						try{
							for(FileChangeListener listener:listeners){
								if(event.context() instanceof Path){
									ThreadUtil.sleep(400);//等待文件完整结束
									Path relativ = (Path)event.context();
									listener.handle(new FileChangeEvent(new File(dir.resolve(relativ).toAbsolutePath().toUri()), event.kind()));
								}
							}
						}catch (Exception e) {
							logger.error(event.context(), e);
						}
					}
					key.reset();
				}
			}
		} catch (Exception e) {
			logger.error("适配器监听失效",e);
		}
	}
	
	public void addListener(FileChangeListener listener){
		listeners.add(listener);
	}
	
}
