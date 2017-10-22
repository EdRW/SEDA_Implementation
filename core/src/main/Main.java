package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import tasks.PrimeTasks;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*
		 * Both of my custom schedulers should implement the Executor Interface
		 * The platform default scheduler is forkjoinpool.something something
		 * 
		 * 
		 * A concurrent blocking queue will be very useful.
		 * 
		 * Controller options. adjust queue size, threadpool size, batch size
		 * 
		 * 
		 * Java's task/future framework
		 * java.util.concurrent.CompletableFuture)
		 * CompletableFuture.thenRunAsync(Runnable r, Executor e))
		 * 
		 * 
		 * Batch of events -> event handler
		 * 
		 * 
		 */
		
		final Long maxIndex = 15L;
		
		Long startTime = System.currentTimeMillis();
		
		defaultSchedulerTest(maxIndex);		
		
		Long duration = System.currentTimeMillis() - startTime;
		
		System.out.println("\nDefault Scheduler time complete tasks for first " + maxIndex + " primes was: " + duration + " ms");
	}

	/**
	 * Uses the default scheduler to print the first n prime numbers.
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
		    	}));
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

	

}
