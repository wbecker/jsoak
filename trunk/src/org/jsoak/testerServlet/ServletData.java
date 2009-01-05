package org.jsoak.testerServlet;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletData
{
  public final Servlet servlet;

  public final HttpServletRequest request;

  public final HttpServletResponse response;

  public ServletData(final Servlet servlet, final HttpServletRequest request,
      final HttpServletResponse response)
  {
    this.servlet = servlet;
    this.request = request;
    this.response = response;
  }

  public String getId()
  {
    return this.request.getParameter("id");
  }
}
