package schedulers.seda.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SedaThreadPool implements ISedaTaskCompleteListener {
	private BlockingQueue<Runnable> batchOfTasks;
	private List<SedaWorkerThread> threads;
	ISedaBatchCompleteListener batchCompleteListener;
	
	private AtomicBoolean active;
	private static AtomicInteger threadPoolCount = new AtomicInteger(0);

	public SedaThreadPool(int poolSize, BlockingQueue<Runnable> batchOfTasks, ISedaBatchCompleteListener batchCompleteListener) {
		threadPoolCount.incrementAndGet();
		this.batchOfTasks = batchOfTasks;
		this.threads = new ArrayList<SedaWorkerThread>();
		this.active = new AtomicBoolean(true);
		this.batchCompleteListener = batchCompleteListener;
		//System.out.println("SedaThreadPool " + threadPoolCount.get() + " starting threads");
		for (int i = 0; i < poolSize; i++) {
			SedaWorkerThread wthread = new SedaWorkerThread("SedaThreadPool " + threadPoolCount.get() + " SedaWorkerThread " + i, batchOfTasks, active, this);
			wthread.start();
			threads.add(wthread);
		}
	}
	
	public void execute(Runnable task) {
		if (this.active.get()) {
			batchOfTasks.add(task);
		}

	}
	
	public void shutdown() {
		active.set(false);
		for (SedaWorkerThread t : threads) {
			t.interrupt();
		}
	}
	
	
	@Override
	public void taskComplete() {
//		for (SedaWorkerThread t : threads) {
//			if (t.readyForTask() == false) return;
//		}
		//System.out.println("Batch is complete!");
		batchCompleteListener.batchComplete();
	}

	public void immediateShutdown() {
		batchOfTasks.clear();
		shutdown();		
	}
	
	public int numTasksInQueue() {
		return batchOfTasks.size();
	}

}
