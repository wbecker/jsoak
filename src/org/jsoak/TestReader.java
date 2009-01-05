package org.jsoak;

import java.io.FileReader;
import java.io.IOException;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Interpreter;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ScriptOrFnNode;

public class TestReader
{

  public TestReader(String filename) throws IOException
  {
    CompilerEnvirons e = new CompilerEnvirons();
    Parser p = new Parser(e);
    ScriptOrFnNode s = p.parse(new FileReader(filename), filename, 0);
    Interpreter i = new Interpreter();
    
  }

  public int getNumberOfTests()
  {
    // TODO Auto-generated method stub
    return 0;
  }

}
