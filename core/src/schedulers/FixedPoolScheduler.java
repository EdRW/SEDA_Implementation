package schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import schedulers.general.utils.WorkerThread;

public class FixedPoolScheduler implements Executor {
	
	private BlockingQueue<Runnable> tasks;
	private List<WorkerThread> threads;
	private AtomicBoolean active;
	private static AtomicInteger threadPoolCount = new AtomicInteger(0);

	public FixedPoolScheduler(int poolSize) {
		threadPoolCount.incrementAndGet();
		this.tasks = new LinkedBlockingQueue<Runnable>();
		this.threads = new ArrayList<WorkerThread>();
		this.active = new AtomicBoolean(true);
		
		for (int i = 0; i < poolSize; i++) {
			WorkerThread wthread = new WorkerThread("FixedPoolScheduler " + threadPoolCount.get() + " WorkerThread " + i, tasks, active);
			wthread.start();
			threads.add(wthread);
		}
	}

	@Override
	public void execute(Runnable task) {
		if (this.active.get()) {
			tasks.add(task);
		}

	}
	
	public void shutdown() {
		active.set(false);
		for (WorkerThread t : threads) {
			t.interrupt();
		}
	}
	
	public void immediateShutdown() {
		tasks.clear();
		shutdown();		
	}

}
