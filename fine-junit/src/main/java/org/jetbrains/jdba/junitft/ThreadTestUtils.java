package org.jetbrains.jdba.junitft;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.Callable;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class ThreadTestUtils {


  public static <T> T[] parallel(final Callable<T> task, final int numberOfThreads, final int timeLimit) throws Exception {
    final int n = numberOfThreads;
    @SuppressWarnings("unchecked")
    final Callable<T>[] tasks = (Callable<T>[]) new Callable[n];
    for (int i = 0; i < n; i++) tasks[i] = task;

    ParallelExecutor<T> executor = new ParallelExecutor<T>(tasks);
    executor.run(timeLimit);

    List<Exception> exceptions = executor.getExceptions();
    if (!exceptions.isEmpty()) {
      PrintStream stream = System.err;
      for (int i = 0, size = exceptions.size(); i < size; i++) {
        //noinspection ThrowableResultOfMethodCallIgnored
        Exception e = exceptions.get(i);
        stream.println("---- Exception " + (i+1) + " ----");
        e.printStackTrace(stream);
      }
      stream.println("----------------------------");
      throw new Exception("FAIL: " + exceptions.size()+ " exceptions occurred");
    }

    return executor.getResults();
  }


  public static void sleep(int milliseconds) {
    try {
      Thread.sleep(milliseconds);
    }
    catch (InterruptedException e) {
      // do nothing here
    }
  }



}
