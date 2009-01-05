package org.jsoak.testerServlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.SkipPageException;

import org.jabsorb.JSONRPCBridge;
import org.jsoak.TestAggregatorIdGenerator;

public abstract class TesterPageServicer
{
  protected final ServletData servletData;

  private final JspWriter outputWriter;

  private final PageContext pageContext;

  private final String requestId;

  private final JSONRPCBridge bridge;
  
  public TesterPageServicer(final ServletData servletData,
      String[] testFileNames,
      TestAggregatorIdGenerator testAggregatorIdGenerator) throws IOException,
      ServletException
  {
    this.servletData = servletData;
    this.pageContext = this.getPageContext();
    this.outputWriter = this.pageContext.getOut();
    this.requestId = testAggregatorIdGenerator.generateId(this.servletData
        .getId());
    this.bridge = getBridge();

    createResponse(testFileNames);
  }

  public String getRequestId()
  {
    return requestId;
  }

  private void createResponse(final String[] testFileNames) throws IOException,
      ServletException
  {
    try
    {
      setResponseContentType();
      writeTesterPage(testFileNames);
    }
    catch (final Throwable t)
    {
      handleExceptions(t);
    }
    finally
    {
      releasePage();
    }
  }

  private void setResponseContentType()
  {
    servletData.response.setContentType("text/html; charset=UTF-8");
  }

  private PageContext getPageContext()
  {
    return JspFactory.getDefaultFactory().getPageContext(
        this.servletData.servlet, this.servletData.request,
        this.servletData.response, null, true, 8192, true);
  }

  private JSONRPCBridge getBridge()
  {
    JSONRPCBridge _bridge;
    final HttpSession session = pageContext.getSession();
    synchronized (session)
    {
//      try
//      {
//        _bridge = this.getBridgeFromContext();
//       }
//      catch (BridgeNotFound e)
//      {
      _bridge = createNewBridge();
      pageContext.setAttribute("JSONRPCBridge", _bridge,
          SCOPE);
//      }
    }
    return _bridge;
  }
  private final int SCOPE = PageContext.SESSION_SCOPE;

  private JSONRPCBridge getBridgeFromContext() throws BridgeNotFound
  {
    JSONRPCBridge _bridge = (JSONRPCBridge) this.pageContext.getAttribute(
        "JSONRPCBridge", SCOPE);
    if (_bridge == null)
    {
      throw new BridgeNotFound();
    }
    return _bridge;
  }

  protected abstract JSONRPCBridge createNewBridge();

  private class BridgeNotFound extends Exception
  {
    private static final long serialVersionUID = 1L;

    public BridgeNotFound()
    {
      super("Bridge does not exist on this session");
    }
  }

  private void writeTesterPage(final String[] testFileNames) throws IOException
  {
    final JspWriterProxy w = new JspWriterProxy(this.outputWriter);

    writeHtmlHeader(w);
    writeJavascriptIncludes(testFileNames, w);
    writeHtmlBody(w);
  }

  private void writeHtmlHeader(final JspWriterProxy w) throws IOException
  {
    w.w("<html>");
    w.w("<head>");
    w.w("<title>jsoak</title>");
    w.w("</head>");
    w.w("<body>");
    w.w("<div id='logging'>");
    w.w("</div>");
  }

  private void writeJavascriptIncludes(final String[] testFileNames,
      final JspWriterProxy w) throws IOException
  {
//    includeLibraries(w);
    includeTestFiles(w, testFileNames);
    includeOnLoadHandler(w);
  }

  private void includeLibraries(final JspWriterProxy w) throws IOException
  {
    w.i("/console.js");
    w.i("/jabsorb.js");
    w.i("/jsUnitCore.js");
    w.i("/Jsoak.js");
    w.i("/JsUnitUtil.js");
    w.i("/JsUnitTest.js");
  }

  private void includeTestFiles(final JspWriterProxy w,
      String[] testFileNames) throws IOException
  {
    for (final String s : testFileNames)
    {
      w.i(s);
    }
  }

  private void includeOnLoadHandler(final JspWriterProxy w) throws IOException
  {
    w.i("/onloadHandler.js");
  }

  private void writeHtmlBody(final JspWriterProxy w) throws IOException
  {
    w.w("</body>");
    w.w("</html>");
  }

  private void handleExceptions(final Throwable t) throws IOException,
      ServletException
  {
    if (!(t instanceof SkipPageException))
    {
      if ((outputWriter != null) && (outputWriter.getBufferSize() != 0))
      {
        outputWriter.clearBuffer();
      }
      if (pageContext != null)
      {
        pageContext.handlePageException(t);
      }
    }
  }

  private void releasePage()
  {
    JspFactory.getDefaultFactory().releasePageContext(pageContext);
  }
}
