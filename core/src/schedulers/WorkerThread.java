package schedulers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkerThread extends Thread {
	
	private BlockingQueue<Runnable> tasks;
	private AtomicBoolean active;

	public WorkerThread(String name, BlockingQueue<Runnable> tasks, AtomicBoolean active) {
		super(name);
		this.tasks = tasks;
		this.active = active;
	}
	
	@Override
	public void run() {
		System.out.println("Thread: " + this.getName() + " has started");
		
		Runnable task;
		while (active.get()) {
			try {
				task = tasks.take();
				task.run();
			} catch (InterruptedException e) {
				active.set(false);
				System.out.println("Thread: " + this.getName() + " has been interrupted");
				e.printStackTrace();
			}
		}
		
		System.out.println("Thread: " + this.getName() + " has stopped");
	}
	
}
