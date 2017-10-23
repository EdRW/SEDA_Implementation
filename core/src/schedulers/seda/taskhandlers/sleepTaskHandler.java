package schedulers.seda.taskhandlers;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import schedulers.seda.utils.ISedaTaskHandler;

public class sleepTaskHandler implements ISedaTaskHandler {
	private int batchSize;
	private BlockingQueue<Runnable> batchQueue;
	
	public sleepTaskHandler(int batchSize) {
		this.batchSize = batchSize;
		this.batchQueue = new ArrayBlockingQueue<>(batchSize);
	}

	@Override
	public synchronized void handleBatchOfTask(BlockingQueue<Runnable> tasks) {
		if (batchQueue.isEmpty() && !tasks.isEmpty()) {			
			for (int i = 0; i < (batchSize - 1); i++) {
				tasks.remove();
			}
		}
	}

	@Override
	public synchronized BlockingQueue<Runnable> getBatchRef() {
		return batchQueue;
	}

	@Override
	public synchronized int getBatchSize() {
		return batchSize;
	}

}
