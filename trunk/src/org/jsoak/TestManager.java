package org.jsoak;

import java.util.HashMap;
import java.util.Map;

import junit.extensions.ActiveTestSuite;
import junit.framework.TestSuite;

public class TestManager
{
  private final Map<String, TestAggregator> testAggregators;

  private final TestAggregatorIdGenerator testAggregatorIdGenerator;

  private final TestSuite testSuite;

  private final TestFileManager testFileManager;

  public TestManager(TestFileManager testFileManager)
  {
    this.testAggregators = new HashMap<String, TestAggregator>();
    this.testAggregatorIdGenerator = new TestAggregatorIdGenerator();
    this.testSuite = new TestSuite();
    this.testFileManager = testFileManager;
  }

  protected TestAggregatorIdGenerator getTestAggregatorIdGenerator()
  {
    return testAggregatorIdGenerator;
  }

  protected TestAggregator getTestAggregator(String id)
  {
    //    testSuite.
    TestSuite s = new ActiveTestSuite();
    s.addTest(new AggregatedTest(testFileManager));
    final TestAggregator toReturn;
    if (testAggregators.containsKey(id))
    {
      toReturn = testAggregators.get(id);
    }
    else
    {
      toReturn = new TestAggregator();
      testAggregators.put(id, toReturn);
    }
    return toReturn;
  }

  protected boolean hasTestAggregator(String id)
  {
    return testAggregators.containsKey(id);
  }
}
