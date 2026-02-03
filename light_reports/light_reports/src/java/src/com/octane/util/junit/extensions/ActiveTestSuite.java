package com.octane.util.junit.extensions;

import com.octane.util.junit.framework.Test;
import com.octane.util.junit.framework.TestResult;
import com.octane.util.junit.framework.TestSuite;

/**
 * A TestSuite for active Tests. It runs each test in a separate thread and waits until all threads
 * have terminated. -- Aarhus Radisson Scandinavian Center 11th floor
 */
public class ActiveTestSuite extends TestSuite {
  private volatile int fActiveTestDeathCount;

  public ActiveTestSuite() {}

  public ActiveTestSuite(Class theClass) {
    super(theClass);
  }

  public ActiveTestSuite(String name) {
    super(name);
  }

  public ActiveTestSuite(Class theClass, String name) {
    super(theClass, name);
  }

  public void run(TestResult result) {
    fActiveTestDeathCount = 0;
    super.run(result);
    waitUntilFinished();
  }

  public void runTest(final Test test, final TestResult result) {
    Thread t =
        new Thread() {
          public void run() {
            try {
              // inlined due to limitation in VA/Java
              // ActiveTestSuite.super.runTest(test, result);
              test.run(result);
            } finally {
              ActiveTestSuite.this.runFinished();
            }
          }
        };
    t.start();
  }

  synchronized void waitUntilFinished() {
    while (fActiveTestDeathCount < testCount()) {
      try {
        wait();
      } catch (InterruptedException e) {
        return; // ignore
      }
    }
  }

  public synchronized void runFinished() {
    fActiveTestDeathCount++;
    notifyAll();
  }
}
