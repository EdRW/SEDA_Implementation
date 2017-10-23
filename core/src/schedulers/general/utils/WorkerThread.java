package schedulers.general.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkerThread extends Thread {
	
	protected BlockingQueue<Runnable> tasks;
	protected AtomicBoolean active;

	public WorkerThread(String name, BlockingQueue<Runnable> tasks, AtomicBoolean active) {
		super(name);
		this.tasks = tasks;
		this.active = active;
	}
	
	@Override
	public void run() {
		//System.out.println("Thread: " + this.getName() + " has started");
		
		Runnable task;
		while (active.get() || !tasks.isEmpty()) {
			try {
				task = tasks.take();
				task.run();
			} catch (InterruptedException e) {
				active.set(false);
				//System.out.println("Thread: " + this.getName() + " has been interrupted");
			}
		}
		
		//System.out.println("Thread: " + this.getName() + " has stopped");
	}
	
}
