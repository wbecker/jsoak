package org.jsoak;

import junit.framework.Assert;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.Test;

public class TestAll
{
  @Test
  public void testAll()
  {
    junit.framework.TestSuite s = new TestSuite("Jsoak tests");
    s.addTestSuite(TestAggregatorIsUniquePerPageRequest.class);
    s.addTestSuite(TestBrowserRunner.class);
    TestResult r = new TestResult();
    s.run(r);
    Assert.assertEquals(0, r.failureCount());
  }
}
