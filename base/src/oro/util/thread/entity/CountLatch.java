package oro.util.thread.entity;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 
 * 拷貝自jdk实现加法
 * @author honghm 
 * Create By 2016年5月23日 上午10:02:39
 */
public class CountLatch {
	private final Sync sync;

	private static final class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = 4982264981922014374L;

		Sync(int paramInt) {
			setState(paramInt);
		}

		int getCount() {
			return getState();
		}

		public int tryAcquireShared(int paramInt) {
			return getState() == 0 ? 1 : -1;
		}

		public boolean tryReleaseShared(int paramInt) {
			for (;;) {
				int i = getState();
				int j = i - paramInt;
				if(j < 0) j = 0;
				if (compareAndSetState(i, j)) {
					return j == 0;
				}
			}
		}
	}

	public CountLatch(int paramInt) {
		if (paramInt < 0)
			throw new IllegalArgumentException("count < 0");
		sync = new Sync(paramInt);
	}

	public void await() throws InterruptedException {
		sync.acquireSharedInterruptibly(1);
	}

	public boolean await(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
		return sync.tryAcquireSharedNanos(1, paramTimeUnit.toNanos(paramLong));
	}

	public void countDown() {
		sync.releaseShared(1);
	}
	
	public void countUp() {
		sync.releaseShared(-1);
	}

	public long getCount() {
		return sync.getCount();
	}

	public String toString() {
		return super.toString() + "[Count = " + sync.getCount() + "]";
	}
}
