package org.jsoak;

import java.io.IOException;

public class TestFileManager
{
  private final int numberOfTests;

  private final int numberOfTestSuites;

  private final JSEvaluator evaluator;

  public TestFileManager(final JsoakProperties properties) throws IOException
  {
    this.evaluator = new JSEvaluator();
    this.numberOfTestSuites = properties.getAllTestFiles().length;
    this.numberOfTests = determineNumberOfTests(properties
        .getAllNecessaryAbsoluteIncludes());
  }

  private int determineNumberOfTests(final String[] files) throws IOException
  {
    for (String f : files)
    {
      this.evaluator.evaluateFile(f);
    }
    Object result = this.evaluator.evaluateString("Jsoak.getTestCount();");
    return convertJSResultToInt(result);
  }

  private int convertJSResultToInt(Object jsObject)
  {
    final int testCount;
    if (Number.class.isAssignableFrom(jsObject.getClass()))
    {
      testCount = ((Number) jsObject).intValue();
    }
    else
    {
      testCount = -1;
    }
    return testCount;
  }

  public int getNumberOfTests()
  {
    return this.numberOfTests;
  }

  public int getNumberOfTestSuites()
  {
    return this.numberOfTestSuites;
  }

}
