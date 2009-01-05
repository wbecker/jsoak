package org.jsoak;

import junit.framework.AssertionFailedError;
import junit.framework.TestResult;

public class TestAggregator
{
  //TODO: map an id for each test and keep track based on that.
  private int successes;

  private int failures;

  private TestResult testResult;

  public TestAggregator()
  {
    this.successes = 0;
    this.failures = 0;
  }

  public void addSuccess(String testName)
  {
    System.out.println(testName + ": Succeedded");
    this.successes++;
  }

  public void addFailure(String testName, String reason)
  {
    System.out.println(testName + ": Failed (" + reason + ")");
    testResult.addFailure(new JavascriptTest(testName),
        new AssertionFailedError(reason));
    this.failures++;
  }

  public int getSuccesses()
  {
    return successes;
  }

  public int getFailures()
  {
    return failures;
  }

  public void reset()
  {
    this.successes = 0;
    this.failures = 0;
  }

  public int getTestsRun()
  {
    return successes + failures;
  }

  public void setTestResult(TestResult testResult)
  {
    if (this.testResult != null)
    {
      throw new RuntimeException("test result already set");
    }
    this.testResult = testResult;
  }
}