package org.jsoak;

import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestResult;

import org.junit.Test;

public class TestBrowserRunner extends JsoakServerTest
{
  @Test
  public void testBrowserRunnerInFirefox() throws IOException
  {
    this.runBrowserRunnerIn("firefox");
  }

  @Test
  public void testBrowserRunnerInKonqueror() throws IOException
  {
    this.runBrowserRunnerIn("konqueror");
  }
  
  private void runBrowserRunnerIn(String browserName)throws IOException
  {
    TestAggregator ta = getRunner().getTestManager().getTestAggregator(
        browserName+"0");
    BrowserRunner b = new BrowserRunner(browserName,
        "http://localhost:8011/TESTER_SERVLET?id="+browserName, ta,
        new TestFileManager(getRunner().getProperties()));
    TestResult testResult = new TestResult();
    b.run(testResult);
    Assert.assertEquals(4, ta.getTestsRun());
  }

  
}
