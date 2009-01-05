package org.jsoak;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import junit.framework.TestResult;

import org.jabsorb.JSONRPCBridge;
import org.jsoak.testerServlet.ServletData;
import org.jsoak.testerServlet.TesterPageServicer;
import org.jsoak.testerServlet.TesterServlet;

public class JavascriptUnitTestRunner
{
  public static void main(String args[])
  {
    String propertiesFile = (args.length > 0) ? args[0] : null;
    try
    {
      JavascriptUnitTestRunner jsoakRunner = new JavascriptUnitTestRunner(
          JsoakProperties.loadProperties(propertiesFile));
      jsoakRunner.runTests();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private final TestManager testManager;

  private JsoakProperties properties;

  private final TestFileManager testFileManager;

  public JavascriptUnitTestRunner() throws IOException
  {
    this(JsoakProperties.loadProperties());
  }

  public JavascriptUnitTestRunner(final JsoakProperties properties)
      throws IOException
  {
    this.properties = properties;
    this.testFileManager = new TestFileManager(properties);
    this.testManager = new TestManager(this.testFileManager);
  }

  public List<TestResult> runTests() throws Exception
  {
    final JsoakServer server = getServer();
    List<TestResult> results = testBrowsers();
    server.stop();
    return results;
  }

  JsoakServer getServer() throws Exception
  {
    final String[] testFiles = this.properties.getAllNecessaryIncludes();
    final JsoakServer server = createServer(testFiles);
    return server;
  }

  private JsoakServer createServer(final String[] files) throws Exception
  {
    return new JsoakServer(8011, createTesterServlet(files));
  }

  private Servlet createTesterServlet(final String[] files)
  {
    return new TesterServlet() {
      private static final long serialVersionUID = 1L;

      @Override
      protected TesterPageServicer createTesterPageServicer(
          ServletData servletData) throws java.io.IOException, ServletException
      {
        return new TesterPageServicer(servletData, files, testManager
            .getTestAggregatorIdGenerator()) {
          @Override
          protected JSONRPCBridge createNewBridge()
          {
            JSONRPCBridge bridge = new JSONRPCBridge();
            TestAggregator testAggregator = testManager.getTestAggregator(this
                .getRequestId());
            bridge.registerObject("counter", testAggregator);
            return bridge;
          }
        };
      }
    };
  }

  private List<TestResult> testBrowsers()
  {
    final List<TestResult> results = new ArrayList<TestResult>();
    for (BrowserRunner browser : getBrowserRunners())
    {
      TestResult testResult = new TestResult();
      browser.run(testResult);
      results.add(testResult);
    }
    return results;
  }

  private Collection<BrowserRunner> getBrowserRunners()
  {
    final String[] browserList = this.properties.getBrowsers();
    final Collection<BrowserRunner> browserRunners = createBrowserRunners(browserList);
    return browserRunners;
  }

  private Collection<BrowserRunner> createBrowserRunners(
      final String[] browserList)
  {
    final Collection<BrowserRunner> browserRunners = new ArrayList<BrowserRunner>();
    for (String browser : browserList)
    {
      browserRunners.add(new BrowserRunner(this.properties.getBrowserExecutable(browser),
          "http://localhost:8011/TESTER_SERVLET?id=" + browser,
          this.testManager.getTestAggregator(browser + "0"),
          //              5));
          this.testFileManager));
    }
    return browserRunners;
  }

  JsoakProperties getProperties()
  {
    return this.properties;
  }

  TestManager getTestManager()
  {
    return testManager;
  }
}
