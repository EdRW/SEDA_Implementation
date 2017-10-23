package schedulers.seda.taskhandlers;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import schedulers.seda.utils.ISedaTaskHandler;

public class BasicTaskHandler implements ISedaTaskHandler {
	private int batchSize;
	private BlockingQueue<Runnable> batchQueue;

	public BasicTaskHandler(int batchSize) {
		this.batchSize = batchSize;
		this.batchQueue = new ArrayBlockingQueue<>(batchSize);
	}

	@Override
	public synchronized void handleBatchOfTask(BlockingQueue<Runnable> tasks) {
//		System.out.println("Default handler called");
		if (batchQueue.isEmpty() && !tasks.isEmpty()) {			
			int numTasksToPull = (tasks.size() >= batchSize) ? batchSize : tasks.size();
//			System.out.println("attempting to drain " + numTasksToPull + 
//					" tasks from tasksQueue holding " + tasks.size() + " tasks, into batch queue of size " + batchQueue.size());
			tasks.drainTo(batchQueue, numTasksToPull);
		}
//		System.out.println("Leaving handler");
	}

	@Override
	public synchronized  BlockingQueue<Runnable> getBatchRef() {
		return batchQueue;
	}

	@Override
	public synchronized int getBatchSize() {
		return batchSize;
	}
	
}
