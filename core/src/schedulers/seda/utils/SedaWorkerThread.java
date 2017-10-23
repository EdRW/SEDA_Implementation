package schedulers.seda.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import schedulers.general.utils.WorkerThread;

public class SedaWorkerThread extends WorkerThread {
	private ISedaTaskCompleteListener taskCompleteListener;
	private Boolean readyForTask;
	
	public SedaWorkerThread(String name, BlockingQueue<Runnable> batchOfTasks, AtomicBoolean active, ISedaTaskCompleteListener taskCompleteListener) {
		super(name, batchOfTasks, active);
		this.taskCompleteListener = taskCompleteListener;
		readyForTask = true;
	}
	
	@Override
	public void run() {
		//System.out.println("Thread: " + this.getName() + " has started");
		Runnable task;
		while (active.get() || !tasks.isEmpty()) {
			try {
				task = tasks.take();
				readyForTask = false;
				task.run();
				readyForTask = true;
				//System.out.println("Thread: " + this.getName() + " has COMPLETED TASK!");
				taskCompleteListener.taskComplete();
			} catch (InterruptedException e) {
				active.set(false);
				//System.out.println("Thread: " + this.getName() + " has been interrupted");
			}
		}
		
		//System.out.println("Thread: " + this.getName() + " has stopped");
	}
	
	public Boolean readyForTask() {
		return readyForTask;
	}

}
