package oro.util.thread.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class HashQueue<T>{
	
	private LinkedHashMap<Integer, T> queue = new LinkedHashMap<Integer,T>();
	
	public void offer(T t) {
		queue.put(t.hashCode(), t);
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public T poll() {
		T t = null;
		for(Iterator<Entry<Integer, T>> iter=queue.entrySet().iterator();iter.hasNext();){
			t = iter.next().getValue();
			iter.remove();
			break;
		}
		return t;
	}

	public void addAll(Collection<T> list) {
		if(list != null){
			for(T t:list)offer(t);
		}
	}

	public int size() {
		return queue.size();
	}
	
	
}
