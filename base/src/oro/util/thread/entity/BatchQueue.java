package oro.util.thread.entity;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 批量
 * @author honghm
 * todu参数修正算法 得以选取最优的batchSize和batchMsss然数据量控制在一个合理的度
 * @param <E>
 */
public class BatchQueue<E>{
	
	private final transient ReentrantLock lock = new ReentrantLock();
  private final PriorityQueue<E> q;
  private final Condition available = lock.newCondition();
  private long fisrtWaitMs;//第一个元素等待时刻
  
  private int batchSize = 20;
  private int batchMsss = 500;//ms
  
  private E[] array;
  
  public BatchQueue(E[] array) {
		super();
		q = new PriorityQueue<E>(batchSize + 10);
		this.array = array;
	}

  public BatchQueue(E[] array,int batchSize, int batchMsss) {
  	super();
  	this.batchSize = batchSize;
  	this.batchMsss = batchMsss;
  	q = new PriorityQueue<E>(batchSize + 10);
  	this.array = array;
  }

	public boolean offer(E e) throws InterruptedException {
		final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        q.offer(e);
        if(fisrtWaitMs < 1)fisrtWaitMs = System.currentTimeMillis();
        available.signal();
        return true;
    } finally {
        lock.unlock();
    }
	}
	
	public E[] take() throws InterruptedException {
		 final ReentrantLock lock = this.lock;
     lock.lockInterruptibly();
     try {
         for (;;) {
             E first = q.peek();
             if (first == null)available.await();
             else {
                 long delay =  fisrtWaitMs + batchMsss - System.currentTimeMillis();//是否需要延迟
                 if (q.size() >= batchSize || delay <= 0){
                	 return flush();
                 }else{
                	 try {
                     available.await(delay,TimeUnit.MILLISECONDS);
                	 } finally {
                	 }
                 }
             }
         }
     } finally {
         lock.unlock();
     }
	}



	public Iterator<E> iterator() {
		return q.iterator();
	}

	public int size() {
		return q.size();
	}
	
	private E[] flush(){
		final ReentrantLock lock = this.lock;
    lock.lock();
    try {
    	E[] es = q.toArray(array);
      q.clear();
      fisrtWaitMs = 0;
      return es;
    } finally {
       lock.unlock();
    }
	}
	
}
