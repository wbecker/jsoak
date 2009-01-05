package org.jsoak;

public class RunTests
{
  private final String name;
  private final boolean passed;
  private final String message;
  private final String environment;

  public RunTests(String name, boolean passed, String message,
      String environment)
  {
    this.name = name;
    this.passed = passed;
    this.message = message;
    this.environment = environment;
  }

  public String getName()
  {
    return this.name;
  }

  public boolean isPassed()
  {
    return this.passed;
  }

  public String getMessage()
  {
    return this.message;
  }

  public String getEnvironment()
  {
    return this.environment;
  }

}
