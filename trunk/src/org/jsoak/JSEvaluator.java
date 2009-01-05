package org.jsoak;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

public class JSEvaluator
{
  protected final Context jsContext;

  protected final Scriptable jsScope;

  private String getFileContents(String filename) throws FileNotFoundException,
      IOException
  {
    final BufferedReader reader = new BufferedReader(new FileReader(filename));
    final StringBuilder fileContents = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null)
    {
      fileContents.append(line).append("\n");
    }
    return fileContents.toString();
  }

  public JSEvaluator()
  {
    this.jsContext = Context.enter();
    this.jsScope = jsContext.initStandardObjects();
  }

  public void evaluateFile(String filename) throws IOException
  {
    String fileContents = getFileContents(filename);
    evaluateString(fileContents, filename);
  }

  public Object evaluateString(String fileContents)
  {
    return this.evaluateString(fileContents, "<cmd>");
  }

  public Object evaluateString(String fileContents, String filename)
  {
    Object result;
    try
    {
      result = jsContext.evaluateString(jsScope, fileContents, filename, 1,
          null);
    }
    catch (JavaScriptException e)
    {
      result = e;
    }
    return result;
  }
}