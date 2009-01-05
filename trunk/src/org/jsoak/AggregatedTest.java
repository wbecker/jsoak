package org.jsoak;

import junit.framework.Test;
import junit.framework.TestResult;

public class AggregatedTest implements Test
{
  private final TestFileManager testFileManager;
  public AggregatedTest(TestFileManager testFileManager)
  {
    this.testFileManager=testFileManager;
  }
  @Override
  public int countTestCases()
  {
    return testFileManager.getNumberOfTests();
  }

  @Override
  public void run(TestResult arg0)
  {
    
  }

}
