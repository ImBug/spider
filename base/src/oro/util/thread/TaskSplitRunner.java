package oro.util.thread;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import oro.util.thread.entity.HashQueue;


/**
 * 6.15 修改成线程池实现，防止线程过多
 * 任务拆分执行:任务数量明确
 * @author honghm 
 * Create By 2016年5月31日 上午9:27:04
 */
public abstract class TaskSplitRunner<T> implements Runnable {
	
	protected Log logger = LogFactory.getLog(TaskSplitRunner.class);
	
	protected List<T> taskList;
	protected int dealNumPerThread = 20;
	
	private CountDownLatch countDown;
	protected static int maxThread = 30;
	private final static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(maxThread*2/3, maxThread, 3, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
	
	protected abstract void excuteTask(T t);
	protected abstract void callBack();
	
	@Override
	public void run() {
		if(taskList != null){
			int threadNum = taskList.size() / dealNumPerThread + 1;
			logger.info(String.format("任务总数:[%s],启用线程[%s],单线程处理任务[%s]",taskList.size(),threadNum,dealNumPerThread));
			countDown = new CountDownLatch(threadNum);
			Queue<T> queue = new LinkedList();
			queue.addAll(taskList);
			try {
				for(int i=0; i<threadNum; i++){
					MyRunner runner = new MyRunner(queue, countDown);
					threadPool.execute(runner);
				}
				countDown.await();
				callBack();
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
	}
	
	class MyRunner extends QueueRunner<T>{
		
		
		public MyRunner(Queue<T> queue, CountDownLatch coundDown) {
			super();
			super.queue = queue;
			super.countDown = coundDown;
			super.logger = TaskSplitRunner.this.logger;
		}

		@Override
		public void init() {
			super.queue = queue;
			super.countDown = countDown;
		}

		@Override
		public void excute(T t) {
			if(logger.isDebugEnabled())logger.debug("当前执行" + t + ",剩余：" + queue.size());
			excuteTask(t);
		}
		
	}

}
