package schedulers.seda.utils;

import java.util.concurrent.BlockingQueue;

public interface ISedaTaskHandler {
	
	public void handleBatchOfTask(BlockingQueue<Runnable> tasks);
	
	public BlockingQueue<Runnable> getBatchRef();
	
	public int getBatchSize();
}
