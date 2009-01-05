package org.jsoak;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

public abstract class JsoakRunnerTest extends TestCase
{
  private JavascriptUnitTestRunner runner;

  @Before
  public void setUp() throws Exception
  {
    this.runner = new JavascriptUnitTestRunner();
  }

  @After
  public void tearDown() throws Exception
  {
    
  }
  
  public JavascriptUnitTestRunner getRunner()
  {
    return runner;
  }
}
