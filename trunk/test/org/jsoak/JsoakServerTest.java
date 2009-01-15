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
    server = this.getRunner().getServer();
    this.getRunner().startServer();
  }

  @After
  public void tearDown() throws Exception
  {
    this.getRunner().stopServer();
    super.tearDown();
  }
  
  public JsoakServer getServer()
  {
    return server;
  }
}
