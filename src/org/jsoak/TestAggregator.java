package org.jsoak;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.AssertionFailedError;
import junit.framework.TestResult;

public class TestAggregator
{
  // TODO: map an id for each test and keep track based on that.
  private int successes;

  private int failures;

  private TestResult testResult;
  private final Collection<RunTests> runTests;
  private final String environment;

  public TestAggregator(String environment)
  {
    this.successes = 0;
    this.failures = 0;
    this.environment = environment;
    this.runTests = new ArrayList<RunTests>();
  }

  public void addSuccess(String testName)
  {
    System.out.println(testName + ": Succeedded");
    this.runTests.add(new RunTests(testName, true, "Test Passed", environment));
    this.successes++;
  }

  public void addFailure(String testName, String reason)
  {
    System.out.println(testName + ": Failed (" + reason + ")");
    testResult.addFailure(new JavascriptTest(testName),
        new AssertionFailedError(reason));
    this.runTests.add(new RunTests(testName, false, reason, environment));
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

  public Collection<RunTests> getRunTests()
  {
    return this.runTests;
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
