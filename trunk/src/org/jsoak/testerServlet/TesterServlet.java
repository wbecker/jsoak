package org.jsoak.testerServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet which provides a basic Hiveshare view through a browser
 * 
 * @author William Becker
 */
public abstract class TesterServlet extends
    org.apache.jasper.runtime.HttpJspBase implements
    org.apache.jasper.runtime.JspSourceDependent
{
  /**
   * Generated Id
   */
  private static final long serialVersionUID = 1L;

  @Override
  public void _jspService(final HttpServletRequest request,
      final HttpServletResponse response) throws java.io.IOException,
      ServletException
  {
    createTesterPageServicer(new ServletData(this, request, response));
  }

  protected abstract TesterPageServicer createTesterPageServicer(ServletData servletData) throws java.io.IOException,
  ServletException;

  public Object getDependants()
  {
    return null;
  }

}
