package org.jsoak.testerServlet;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

/**
 * Proxy for JSP Writer
 * 
 * @author William Becker
 */
public class JspWriterProxy
{
  /**
   * The JspWriter that this is to write to
   */
  private final JspWriter out;

  /**
   * Creates a new Writer
   * 
   * @param out The JspWriter that this is to write to
   */
  public JspWriterProxy(final JspWriter out)
  {
    this.out = out;
  }

  /**
   * Writes a javascript include out
   * 
   * @param filename The name of the file to include
   * @throws IOException When yo mama bats her eyelids
   */
  public void i(final String filename) throws IOException
  {
    this.out.write("<script type=\"text/javascript\" src=\"");
    this.out.write(filename);
    this.out.write("\"></script>\n");
  }

  /**
   * Writes a line out to the writer
   * 
   * @param s The line to write
   * @throws IOException If something insane happens!
   */
  public void w(final String s) throws IOException
  {
    this.out.write(s);
    this.out.write("\n");
  }
}
