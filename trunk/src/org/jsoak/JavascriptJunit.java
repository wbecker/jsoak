package org.jsoak;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestResult;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class JavascriptJunit
{
  private final RunTests testResult;

  public JavascriptJunit(RunTests testResult) throws Exception
  {
    this.testResult = testResult;
  }

  @Test
  public void runJavascriptTest()
  {
    Assert.assertTrue(testResult.getName() + " failed. Reason: "
        + testResult.getMessage() + " Environment: "
        + testResult.getEnvironment(), testResult.isPassed());
  }

  @Parameters
  public static Collection<Object[]> data()
  {
    Collection<Object[]> data = new ArrayList<Object[]>();
    try
    {
      TestResult all = new TestResult();
      JavascriptUnitTestRunner runner = new JavascriptUnitTestRunner();
      Collection<Collection<RunTests>> results = runner.runTests();
      for (Collection<RunTests> r : results)
      {
        for (RunTests t : r)
        {
          data.add(new Object[] { t });
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return data;
  }

}
