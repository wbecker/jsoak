package org.jsoak;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.junit.Assert;
import org.junit.Test;

public class TestAggregatorIsUniquePerPageRequest extends JsoakServerTest
{
  String TEST_TYPE = "TEST";

  @Test
  public void testCounterMatchesSession()
  {
    String uriString = "http://localhost:" + this.getServer().getPort()
        + this.getServer().getTestServletPath() + "?id=" + TEST_TYPE;
    int maxTests = 10;
    try
    {
      testCorrectCountersExist(this.getRunner(), uriString, maxTests);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      Assert.fail();
    }
  }

  private void testCorrectCountersExist(JavascriptUnitTestRunner runner,
      String uriString, int maxTests) throws IOException, HttpException
  {
    ServerRequester requester = new ServerRequester();
    for (int callCount = 0; callCount < maxTests; callCount++)
    {
      requester.requestPage(uriString);
      final TestAggregatorIdGenerator generator = new TestAggregatorIdGenerator();
      for (int testAggregatorCount = 0; testAggregatorCount < maxTests; testAggregatorCount++)
      {
        ensureCounterExistsIfNeeded(runner, testAggregatorCount, callCount,
            generator);
      }
    }
  }

  private void ensureCounterExistsIfNeeded(JavascriptUnitTestRunner runner,
      int testAggregatorCount, int callCount,
      TestAggregatorIdGenerator generator)
  {
    boolean result = runner.getTestManager().hasTestAggregator(
        generator.generateId(TEST_TYPE));
    Assert.assertTrue(result == (testAggregatorCount <= callCount));
  }
}
