package org.jsoak;

import java.util.List;

import org.junit.Test;
import junit.framework.TestResult;


public class JavascriptJunit implements junit.framework.Test
{

    JavascriptUnitTestRunner runner;

  public JavascriptJunit() throws Exception
  {
    this.runner = new JavascriptUnitTestRunner();
  }

  @Override
  public int countTestCases()
  {

    return 0;
  }

  @Override
  public void run(TestResult testResult)
  {
    try
    {
      TestResult all = new TestResult();
      List<TestResult> results = this.runner.runTests();
      for (TestResult r : results)
      {
        for (Object o : new EnumerationIterator(r.failures()))
        {
          System.out.println(o.getClass());
          testResult.addFailure(null, null);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
