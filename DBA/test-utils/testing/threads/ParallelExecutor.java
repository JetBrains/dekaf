package testing.threads;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.max;
import static testing.threads.ThreadTestUtils.sleep;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class ParallelExecutor<T> {

  //// TASK WRAPPER \\\\

  private final class Task implements Runnable {

    private final Callable<T> myCallable;
    private final int index;

    Task(Callable<T> callable, int index) {
      myCallable = callable;
      this.index = index;
    }

    @Override
    public void run() {
      try {
        T result = myCallable.call();
        myResults[index] = result;
      }
      catch (Exception e) {
        myExceptions.add(e);
      }
      finally {
        myRunning.decrementAndGet();
      }
    }
  }


  //// STATE \\\\

  private final int n;
  private final Thread[] myThreads;
  private final T[] myResults;
  private final List<Exception> myExceptions;
  private final AtomicInteger myRunning;


  //// METHODS \\\\

  @SuppressWarnings("unchecked")
  public ParallelExecutor(@NotNull final Callable<T>[] callables) {
    n = callables.length;
    myThreads = new Thread[n];
    myResults = (T[]) new Object[n];
    myExceptions = new CopyOnWriteArrayList<Exception>();
    myRunning = new AtomicInteger();

    for (int i = 0; i < n; i++) {
      Task task = new Task(callables[i], i);
      myThreads[i] = new Thread(task);
    }
  }


  public T[] run(final int timeLimit) {
    myRunning.set(n);
    for (int i = 0; i < n; i++) myThreads[i].start();

    long timeout = System.currentTimeMillis() + timeLimit;
    int interval = max(timeLimit/1000, 11);

    do {
      sleep(interval);
    } while (myRunning.get() > 0 && System.currentTimeMillis() <= timeout);

    if (myRunning.get() > 0) {
      for (int i = 0; i < n; i++) {
        Thread thread = myThreads[i];
        if (thread.isAlive()) thread.interrupt();
      }

      timeout = System.currentTimeMillis() + 1000;
      do {
        sleep(interval);
      } while (myRunning.get() > 0 && System.currentTimeMillis() <= timeout);
    }

    for (int i = 0; i < n; i++) {
      Thread thread = myThreads[i];
      if (thread.isAlive()) {
        String msg = "Thread " + (i+1) + " hung!";
        myExceptions.add(new Exception(msg));
      }
    }

    return myResults;
  }


  public List<Exception> getExceptions() {
    return myExceptions;
  }


  public T[] getResults() {
    return myResults;
  }
}
