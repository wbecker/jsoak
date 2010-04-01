package org.jsoak;

import java.util.ArrayList;
import java.util.Collection;

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
      Collection<Collection<RunTests>> results = jsoakRunner.runTests();
      int returnCode = generateReturnCode(results);
      System.exit(returnCode);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private static int generateReturnCode(Collection<Collection<RunTests>> results)
  {
    for(Collection<RunTests> rr: results) 
    {
      for(RunTests rt: rr) {
        if(!rt.isPassed()) {
          return 1;
        }
      }
    }
    return 0;
  }

  private final TestManager testManager;

  private final JsoakProperties properties;

  private final TestFileManager testFileManager;

  private final JsoakServer server;

  public JavascriptUnitTestRunner() throws Exception
  {
    this(JsoakProperties.loadProperties());
  }

  public JavascriptUnitTestRunner(final JsoakProperties properties)
      throws Exception
  {
    this.properties = properties;
    this.testFileManager = new TestFileManager(properties);
    this.testManager = new TestManager(this.testFileManager);
    this.server = this.getServer();
  }

  public Collection<Collection<RunTests>> runTests() throws Exception
  {
    this.startServer();
    Collection<Collection<RunTests>> results = testBrowsers();
    this.stopServer();
    return results;
  }

  public void startServer() throws Exception
  {
    this.server.start();
  }

  public void stopServer()
  {
    this.server.stop();
  }

  public JsoakServer getServer() throws Exception
  {
    final String[] testFiles = this.properties.getAllNecessaryIncludes();
    final String[] cssFiles = this.properties.getCssFiles();
    return createServer(testFiles,cssFiles);
  }

  private JsoakServer createServer(final String[] testFiles, final String[] cssFiles) throws Exception
  {
    return new JsoakServer(8011, createTesterServlet(testFiles, cssFiles), this.properties.getProperty(JsoakProperties.WEB_DIRECTORY),
    		this.properties.getProperty(JsoakProperties.REDIRECT_LOCATION));
  }

  private Servlet createTesterServlet(final String[] testFiles, final String[] cssFiles)
  {
    return new TesterServlet()
    {
      private static final long serialVersionUID = 1L;

      @Override
      protected TesterPageServicer createTesterPageServicer(
          ServletData servletData) throws java.io.IOException, ServletException
      {
        return new TesterPageServicer(servletData, testFiles, cssFiles, testManager
            .getTestAggregatorIdGenerator())
        {
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

  private Collection<Collection<RunTests>> testBrowsers()
  {
    final Collection<Collection<RunTests>> results = new ArrayList<Collection<RunTests>>();
    final Collection<BrowserRunner> runningBrowsers = new ArrayList<BrowserRunner>();
    for (final BrowserRunner browser : getBrowserRunners())
    {
      System.out.println("Testing: "+browser.getBrowserId());
      TestResult testResult = new TestResult();
      browser.run(testResult, new BrowserRunner.BrowserRunnerTerminatedCallback(){
        @Override
        public void terminated()
        {
          runningBrowsers.remove(browser); 
        }
      });
      results.add(browser.getRunTests());
      runningBrowsers.add(browser);
    }
    while(runningBrowsers.size() > 0) 
    {
      try{Thread.sleep(100);}catch(Exception e){};
    }
    System.out.println("Finished all tests");
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
      browserRunners.add(new BrowserRunner(this.properties
          .getBrowserExecutable(browser),
          "http://localhost:8011/TESTER_SERVLET?id=" + browser,
          this.testManager.getTestAggregator(browser + "0"),
          this.testFileManager, this.properties.killBrowser()));
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
