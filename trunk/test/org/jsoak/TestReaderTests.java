package org.jsoak;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestReaderTests
{
  private TestFileManager testReader;

  @Before
  public void setUp() throws Exception
  {
    JsoakProperties properties = JsoakProperties.loadProperties();
    testReader = new TestFileManager(properties);
  }

  @Test
  public void testReadTestCount()
  {
    System.out.println("Number of tests suites: "+testReader.getNumberOfTestSuites());
    System.out.println("Number of tests: "+testReader.getNumberOfTests());
    Assert.assertEquals(2, testReader.getNumberOfTestSuites());
    Assert.assertEquals(4, testReader.getNumberOfTests());
  }

  @After
  public void tearDown() throws Exception
  {
  }

}
