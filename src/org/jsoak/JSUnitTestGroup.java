package org.jsoak;

import java.util.Collection;

public class JSUnitTestGroup implements JSUnitTestNode
{
  private final Collection<JSUnitTestNode> nodes;
  private final String environment;
  public JSUnitTestGroup(String environment, Collection<JSUnitTestNode> nodes)
  {
    this.environment = environment;
    this.nodes = nodes;
  }
}
