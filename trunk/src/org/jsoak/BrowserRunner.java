package org.jsoak;

import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestResult;

public class BrowserRunner implements Test
{
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

  public BrowserRunner(String browserExecutable, String url,
      TestAggregator testAggregator, TestFileManager testFileManager)
  {
    this.browserExecutable = browserExecutable;
    this.url = url;
    this.testAggregator = testAggregator;
    this.testFileManager = testFileManager;
  }

  @Override
  public void run(TestResult testResult)
  {
    this.testAggregator.setTestResult(testResult);
    try
    {
      Process p = Runtime.getRuntime().exec(
          new String[] { this.browserExecutable, this.url });
      waitForProcessToFinish();
      p.destroy();
    }
    catch (Exception e)
    {
      e.printStackTrace();
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
}

