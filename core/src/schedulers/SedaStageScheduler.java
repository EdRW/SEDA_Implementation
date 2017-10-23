package schedulers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import schedulers.seda.utils.ISedaBatchCompleteListener;
import schedulers.seda.utils.ISedaTaskHandler;
import schedulers.seda.utils.SedaThreadPool;

public class SedaStageScheduler implements Executor, ISedaBatchCompleteListener {
	private BlockingQueue<Runnable> tasks;
	private SedaThreadPool threadPool;
	private AtomicBoolean active;
	private ISedaTaskHandler taskHandler;
	
	public SedaStageScheduler(int poolSize, int taskQueueSize, ISedaTaskHandler taskHandler) {
		this.tasks = new LinkedBlockingQueue<>(taskQueueSize);
		this.taskHandler = taskHandler;
		//System.out.println("creating threadpool");
		this.active = new AtomicBoolean(true);
		this.threadPool = new SedaThreadPool(poolSize, taskHandler.getBatchRef(), this);
		
	}

	@Override
	public void execute(Runnable task) {
		if (this.active.get()) {
			tasks.add(task);
			if (threadPool.numTasksInQueue() == 0 )taskHandler.handleBatchOfTask(tasks);
		}
	}

	@Override
	public void batchComplete() {
		taskHandler.handleBatchOfTask(tasks);
	}
	
	public void shutdown() {
		active.set(false);
		threadPool.shutdown();
	}
	
	public void immediateShutdown() {
		tasks.clear();
		shutdown();
		threadPool.immediateShutdown();
	}
}
