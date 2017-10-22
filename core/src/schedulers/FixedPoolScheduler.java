package schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public class FixedPoolScheduler implements Executor {
	
	private BlockingQueue<Runnable> tasks;
	private List<WorkerThread> threads;
	private AtomicBoolean active;
	

	public FixedPoolScheduler(int poolSize) {
		this.tasks = new ArrayBlockingQueue<Runnable>(poolSize);
		this.threads = new ArrayList<WorkerThread>();
		this.active = new AtomicBoolean(true);
		
		for (int i = 0; i < poolSize; i++) {
			WorkerThread wthread = new WorkerThread("WorkerThread " + i, tasks, active);
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
	}
	
	

}
