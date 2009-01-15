package org.jsoak;

import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestResult;

public class BrowserRunner extends Thread implements Test
{
  public interface BrowserRunnerTerminatedCallback {
    void terminated();
  }
  
  private final TestFileManager testFileManager;

  @Override
  public int countTestCases()
  {
    return testFileManager.getNumberOfTests();
  }

  private static final long TIMEOUT = 150 * 1000;

  private final String browserExecutable;

  private final String url;

  private final TestAggregator testAggregator;

  private final boolean killBrowser;

  public BrowserRunner(String browserExecutable, String url,
      TestAggregator testAggregator, TestFileManager testFileManager,
      boolean killBrowser)
  {
    this.browserExecutable = browserExecutable;
    this.url = url;
    this.testAggregator = testAggregator;
    this.testFileManager = testFileManager;
    this.killBrowser = killBrowser;
  }

  private BrowserRunnerTerminatedCallback callback;

  public void run(TestResult testResult, BrowserRunnerTerminatedCallback callback)
  {
    this.testAggregator.setTestResult(testResult);
    this.callback = callback;
    this.start();
  }
  public void run(TestResult testResult)
  {
    this.run(testResult, null);
  }
  
  public void run() 
  {
    try
    {
      Process p = Runtime.getRuntime().exec(
          new String[] { this.browserExecutable, this.url });
      waitForProcessToFinish();
      if (killBrowser)
      {
        p.destroy();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    if(callback != null) {
      callback.terminated();
    }
  }

  private boolean waitForProcessToFinish() throws InterruptedException
  {
    long timeOfLastResult = System.currentTimeMillis();
    long lastNumberOfResults = 0;
    boolean finishedProperly = true;
    while (testAggregator.getTestsRun() < testFileManager.getNumberOfTests())
    {
      if (testAggregator.getTestsRun() > lastNumberOfResults)
      {
        lastNumberOfResults = testAggregator.getTestsRun();
        timeOfLastResult = System.currentTimeMillis();
      }
      else
      {
        if (System.currentTimeMillis() - timeOfLastResult > TIMEOUT)
        {
          finishedProperly = false;
          break;
        }
      }
      Thread.sleep(1000);
    }
    return finishedProperly;
  }

  public Collection<RunTests> getRunTests()
  {
    return this.testAggregator.getRunTests();
  }

  public String getBrowserId()
  {
    return this.browserExecutable;
  }
}
