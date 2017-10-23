package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import schedulers.FixedPoolScheduler;
import schedulers.SedaStageScheduler;
import schedulers.seda.taskhandlers.BasicTaskHandler;
import tasks.PrimeTasks;

public class Main {

	public static void main(String[] args) {		
		try {
			File file = new File("SchedulerStats.txt");
			PrintStream out = new PrintStream(file);
			out.println("-- Scheduler Performance Statistics --");
			out.println();
			out.println();
			
			final long maxIndex = 1000L;  // <-------This is where you can change the number of primes to calculate
			
			System.out.println("Default Threadpool Scheduler");
			Long startTime = System.currentTimeMillis();
			//defaultSchedulerTest(maxIndex);		
			Long duration = System.currentTimeMillis() - startTime;
			
			out.println("\nDefault Scheduler time to complete " + maxIndex + " prime tasks: " + duration + " ms");
			out.println("Average Throughput: " + (float)maxIndex/(duration/1000f) +  " tasks/sec");
			out.println("Average Latency: " + duration/maxIndex + " ms");
			out.println();
			
			out.println("Entering Tests for Custom Fixed Threadpool Scheduler\n");
			
			for (int threadPoolSize = 4; threadPoolSize <= 1024; threadPoolSize*=2) {
				
				System.out.println("\n\nCustom Fixed Threadpool Scheduler. pool size: " + threadPoolSize);
				startTime = System.currentTimeMillis();
				customFixedThreadpoolSchedulerTest(maxIndex, threadPoolSize);
				duration = System.currentTimeMillis() - startTime;
				
				out.println("\nCustom Fixed Threadpool Scheduler time to complete " + maxIndex + " primes tasks: " + duration + " ms");
				out.println("Average Throughput: " + (float)maxIndex/(duration/1000f) +  " tasks/sec");
				out.println("Average Latency: " + duration/maxIndex + " ms");
				out.println("Thread Pool Size: " + threadPoolSize);
				out.println();
			}
			
			out.println("Entering Tests for Custom SEDA Scheduler with changing threadpool size\n");
			
			for (int threadPoolSize = 4; threadPoolSize <= 1024; threadPoolSize*=2) {
				
				int batchSize = 1024;
				System.out.println("\n\nCustom SEDA Scheduler. pool size: " + threadPoolSize + " Batch size: " + batchSize);
				startTime = System.currentTimeMillis();						
				customSedaSchedulerTest(maxIndex, threadPoolSize, (int)maxIndex, batchSize);
				duration = System.currentTimeMillis() - startTime;
				
				out.println("\nCustom SEDA Scheduler time to complete " + maxIndex + " primes tasks: " + duration + " ms");
				out.println("Average Throughput: " + (float)maxIndex/(duration/1000f) +  " tasks/sec");
				out.println("Average Latency: " + duration/maxIndex + " ms");
				out.println("Thread Pool Size: " + threadPoolSize);
				out.println("Batch size: " + batchSize);
				out.println();
				
			}
			
			out.println("Entering Tests for Custom SEDA Scheduler with changing batch size\n");
			for (int batchSize = 4; batchSize <= 1024; batchSize*=2) {
				
				int threadPoolSize = 1024;
				System.out.println("\n\nCustom SEDA Scheduler. pool size: " + threadPoolSize + " Batch size: " + batchSize);
				startTime = System.currentTimeMillis();						
				customSedaSchedulerTest(maxIndex, threadPoolSize, (int)maxIndex, batchSize);
				duration = System.currentTimeMillis() - startTime;
				
				out.println("\nCustom SEDA Scheduler time to complete " + maxIndex + " primes tasks: " + duration + " ms");
				out.println("Average Throughput: " + (float)maxIndex/(duration/1000f) +  " tasks/sec");
				out.println("Average Latency: " + duration/maxIndex + " ms");
				out.println("Thread Pool Size: " + threadPoolSize);
				out.println("Batch size: " + batchSize);
				out.println();
				
			}
			
			
			out.close();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Uses the default scheduler ForkJoinPool.commonPool() to print the first n prime numbers.
	 * This allows for up to 7 concurrent processes at a time.
	 * This is most likely because my processor has 8 cores and the main thread is using one of the cores.
	 * The scheduler does not add additional threads to its thread pool when the 7 thread limit is reached.
	 * @param maxIndex
	 */
	public static void defaultSchedulerTest(final Long maxIndex) {
		List<CompletableFuture<Long>> futurePrimes = new ArrayList<CompletableFuture<Long>>();
		
		for (Long i = 1L; i <= maxIndex; ++i) {
		    final Long n = i;
		    futurePrimes.add(CompletableFuture.supplyAsync(()-> {
		    	Long prime = PrimeTasks.calculateNthPrime(n);
		    	PrimeTasks.sleepTask(prime);
		    	PrimeTasks.printTask(n, prime); 
		    	return prime;
		    	}, ForkJoinPool.commonPool()));
		}
		
		for (CompletableFuture<Long> prime : futurePrimes) {
		    // Ensures that the entire job is executed to completion
		    try {
				prime.get();
			} catch (InterruptedException| ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
		}
	}	
	
	
	/**
	 * Uses a custom fixed thread pool scheduler to print the first n prime numbers.
	 * This allows for up to Integer.MAX_VALUE threads at a time.
	 * @param maxIndex
	 * @param poolSize
	 */
	public static void customFixedThreadpoolSchedulerTest(final Long maxIndex, int poolSize) {
		FixedPoolScheduler fixedThreadPool = new FixedPoolScheduler(poolSize);
		
		List<CompletableFuture<Long>> futurePrimes = new ArrayList<CompletableFuture<Long>>();
		
		for (Long i = 1L; i <= maxIndex; ++i) {
		    final Long n = i;
		    futurePrimes.add(CompletableFuture.supplyAsync(()-> {
		    	Long prime = PrimeTasks.calculateNthPrime(n);
		    	PrimeTasks.sleepTask(prime);
		    	PrimeTasks.printTask(n, prime); 
		    	return prime;
		    	}, fixedThreadPool));
		}
		
		for (CompletableFuture<Long> prime : futurePrimes) {
		    // Ensures that the entire job is executed to completion
		    try {
				prime.get();
			} catch (InterruptedException| ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
		}
		
		fixedThreadPool.shutdown();
	}	
	
	/**
	 * Uses a custom SEDA scheduler. The following can be knobs are available:
	 * number of threads in threadpool, the size of the task queue, the batch size used by the handler
	 * 
	 * @param maxIndex
	 * @param poolSize
	 * @param taskQueueSize
	 * @param batchSize
	 */
	public static void customSedaSchedulerTest (final Long maxIndex, int poolSize, int taskQueueSize, int batchSize) {		
		SedaStageScheduler primeStage = new SedaStageScheduler(poolSize/2, taskQueueSize, new BasicTaskHandler(batchSize));
		SedaStageScheduler sleepStage = new SedaStageScheduler(poolSize/4, taskQueueSize, new BasicTaskHandler(batchSize));
		SedaStageScheduler printStage = new SedaStageScheduler(poolSize/4, taskQueueSize, new BasicTaskHandler(batchSize));
				
		List<CompletableFuture<Void>> futurePrimes = new ArrayList<CompletableFuture<Void>>();
		
		for (Long i = 1L; i <= maxIndex; ++i) {
		    final Long n = i;
		    futurePrimes.add(CompletableFuture.supplyAsync(()-> PrimeTasks.calculateNthPrime(n), primeStage)
		    		.thenApplyAsync((Long prime) -> PrimeTasks.sleepTask(prime), sleepStage)
		    		.thenAcceptAsync((Long prime) -> PrimeTasks.printTask(n, prime), printStage));
		}
		
		for (CompletableFuture<Void> prime : futurePrimes) {
		    try {
				prime.get();
			} catch (InterruptedException| ExecutionException e) {
				e.printStackTrace();
			
			}
		}
		
		primeStage.shutdown();
		sleepStage.shutdown();
		printStage.shutdown();
	}
}
