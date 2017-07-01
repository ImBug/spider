package oro.service;

import java.io.File;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

public class FileChangeEvent {
	
	private File file;
	private WatchEvent.Kind<?> kind;
	
	public FileChangeEvent(File file, WatchEvent.Kind<?> kind) {
		super();
		this.file = file;
		this.kind = kind;
	}

	public File getFile() {
		return file;
	}
	
	public boolean isCreate(){
		return StandardWatchEventKinds.ENTRY_CREATE == kind;
	}
	
	public boolean isDelete(){
		return StandardWatchEventKinds.ENTRY_DELETE == kind;
	}
	
	public boolean isChange(){
		return StandardWatchEventKinds.ENTRY_MODIFY == kind;
	}
	
	
}
