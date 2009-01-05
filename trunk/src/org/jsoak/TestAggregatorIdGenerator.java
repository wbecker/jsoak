package org.jsoak;

import java.util.HashMap;
import java.util.Map;

public class TestAggregatorIdGenerator
{

  private final Map<String, Integer> testAggregatorCounts;

  public TestAggregatorIdGenerator()
  {
    this.testAggregatorCounts = new HashMap<String, Integer>();
  }

  public synchronized String generateId(String id)
  {
    final int nextCount = getCurrentCount(id) + 1;
    this.testAggregatorCounts.put(id, nextCount);
    String generatedId = id + Integer.toString(nextCount);
    return generatedId;
  }

  private int getCurrentCount(String id)
  {
    final int count;
    if (testAggregatorCounts.containsKey(id))
    {
      count = testAggregatorCounts.get(id);
    }
    else
    {
      count = -1;
    }
    return count;
  }
}
