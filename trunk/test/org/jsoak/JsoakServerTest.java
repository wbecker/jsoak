package org.jsoak;

import org.junit.After;
import org.junit.Before;

public abstract class JsoakServerTest extends JsoakRunnerTest
{
  private JsoakServer server;

  @Before
  public void setUp() throws Exception
  {
    super.setUp();
    this.server = this.getRunner().getServer();
  }

  @After
  public void tearDown() throws Exception
  {
    this.server.stop();
    super.tearDown();
  }
  
  public JsoakServer getServer()
  {
    return server;
  }
}
