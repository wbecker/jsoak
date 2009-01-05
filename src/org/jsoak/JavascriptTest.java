package org.jsoak;

import junit.framework.Test;
import junit.framework.TestResult;

public class JavascriptTest implements Test
{
  private final String name;

  public JavascriptTest(String name)
  {
    this.name = name;
  }

  @Override
  public int countTestCases()
  {
    return 1;
  }

  @Override
  public void run(TestResult arg0)
  {
  }
}
