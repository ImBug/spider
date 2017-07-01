package oro.service;

import java.io.File;
import java.nio.file.Path;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 1.加载适配器
 * 2.卸载同步任务
 * @author honghm
 *
 */
public abstract class FileListener implements FileChangeListener {
	
	protected Log logger = LogFactory.getLog(FileListener.class);
	
	/**
	 * !!!
	 */
	@Override
	public void handle(FileChangeEvent event) {
		File config = event.getFile();
		if(event.isCreate()){
			try {
				add(config);
			} catch (Exception e) {
				logger.error(e);
			}
		}else if(event.isDelete()){
			try {
				remove(config);
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	/**
	 * 路径匹配
	 * @param path
	 * @return
	 */
	protected abstract boolean match(Path path);
	
	protected abstract void add(File file);
	
	protected abstract void remove(File file);
	
	private void change(File file){
		try {
			remove(file);
			add(file);
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
