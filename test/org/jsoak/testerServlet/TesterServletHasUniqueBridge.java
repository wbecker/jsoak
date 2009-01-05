/**
 * 
 */
package org.jsoak.testerServlet;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;

import org.jabsorb.JSONRPCBridge;
import org.jsoak.TestAggregatorIdGenerator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author pretzel
 */
public class TesterServletHasUniqueBridge
{

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception
  {
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception
  {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception
  {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception
  {
  }

  @Test
  public void testHasUniqueBridge()
  {
    final List<String> fileNames = Collections.EMPTY_LIST;
    try
    {
//      TesterPageServicer first = createTesterPageServicer(fileNames,
//          servletData);
//      TesterPageServicer second = createTesterPageServicer(fileNames,
//          servletData);
//      assertTrue(first.getBridgeFromContext() != second.getBridgeFromContext());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail("Exception thrown");
    }

  }

  private TesterServlet createTesterServlet(final String[] fileNames)
  {
    TesterServlet first = new TesterServlet() {
      @Override
      protected TesterPageServicer createTesterPageServicer(
          ServletData servletData) throws IOException, ServletException
      {
        return TesterServletHasUniqueBridge.this.createTesterPageServicer(
            fileNames, servletData);
      }

    };
    return first;
  }
TestAggregatorIdGenerator taig = new TestAggregatorIdGenerator();
  private TesterPageServicer createTesterPageServicer(
      final String[] fileNames, ServletData servletData)
      throws IOException, ServletException
  {
    return new TesterPageServicer(servletData, fileNames,taig) {
      @Override
      protected JSONRPCBridge createNewBridge()
      {
        return new JSONRPCBridge();
      }
    };
  }

}
