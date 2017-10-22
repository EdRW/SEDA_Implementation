package tasks;

public class PrimeTasks {
	
	public static Long calculateNthPrime(final Long n) {
		if (n == 0) return 0L;
		Long index = 0L;
		Long returnVal= 0L;
		for (Long i = 0L; i < Long.MAX_VALUE && index != n; i++) {			
			if (isPrime(i)) {
				returnVal = i;
				index++;
			}		
		}		
		return returnVal;
	}
	
	private static boolean isPrime(Long p) {
		if (p < 2) return false;
		else if (p == 2) return true;
		else if (p == 3) return true;
		for(int i = 2; i <= p/2; ++i) {
	        if (p % i == 0) {
	            return false;
	        }
	    }
	    return true;
	}
	
	public static Long sleepTask(Long p) {
		try {
			Thread.sleep(10);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		return p;
	}
	
	public static void printTask(final Long n, Long p) {
		System.out.print("[n" + n + ": " + p + "], ");
	}
	
}
