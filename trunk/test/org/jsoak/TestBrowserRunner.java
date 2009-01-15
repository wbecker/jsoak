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
    this.runBrowserRunnerIn("firefox", "C:/Program Files/Mozilla Firefox/firefox.exe");
  }

//  @Test
//  public void testBrowserRunnerInKonqueror() throws IOException
//  {
//    this.runBrowserRunnerIn("konqueror");
//  }
  
  @Test
  public void testBrowserRunnerInKonqueror() throws IOException
  {
    this.runBrowserRunnerIn("iexplorer", "C:/Program Files/Internet Explorer/iexplore.exe");
  }
  
  private void runBrowserRunnerIn(String browserName, String browserExecutable)
      throws IOException
  {
    TestAggregator ta = getRunner().getTestManager().getTestAggregator(
        browserName + "0");
    BrowserRunner b = new BrowserRunner(browserExecutable,
        "http://localhost:8011/TESTER_SERVLET?id=" + browserName, ta,
        new TestFileManager(getRunner().getProperties()),true);
    TestResult testResult = new TestResult();
    b.run(testResult);
    Assert.assertEquals(4, ta.getTestsRun());
  }

}
