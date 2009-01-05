package org.jsoak;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestAggregatorIdGeneratorTest extends TestCase
{
  private TestAggregatorIdGenerator testAggregatorIdGenerator;
  @Before
  public void setUp() throws Exception
  {
    testAggregatorIdGenerator=new TestAggregatorIdGenerator();
  }

  @Test
  public void testGenerator(){
    Assert.assertEquals("a0",testAggregatorIdGenerator.generateId("a"));
    Assert.assertEquals("b0",testAggregatorIdGenerator.generateId("b"));
    Assert.assertEquals("c0",testAggregatorIdGenerator.generateId("c"));
    Assert.assertEquals("a1",testAggregatorIdGenerator.generateId("a"));
    Assert.assertEquals("b1",testAggregatorIdGenerator.generateId("b"));
    Assert.assertEquals("c1",testAggregatorIdGenerator.generateId("c"));
    Assert.assertEquals("a2",testAggregatorIdGenerator.generateId("a"));
    Assert.assertEquals("a3",testAggregatorIdGenerator.generateId("a"));
  }
}
