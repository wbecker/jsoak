package org.jsoak.testerServlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
      final String[] testFileNames,
      final String[] cssFiles,
      final TestAggregatorIdGenerator testAggregatorIdGenerator) throws IOException,
      ServletException
  {
    this.servletData = servletData;
    this.pageContext = this.getPageContext();
    this.outputWriter = this.pageContext.getOut();
    this.requestId = testAggregatorIdGenerator.generateId(this.servletData
        .getId());
    this.bridge = getBridge();

    createResponse(testFileNames, cssFiles);
  }

  public String getRequestId()
  {
    return requestId;
  }

  private void createResponse(final String[] testFileNames, final String[] cssFiles) throws IOException,
      ServletException
  {
    try
    {
      setResponseContentType();
      writeTesterPage(testFileNames, cssFiles);
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
      // try
      // {
      // _bridge = this.getBridgeFromContext();
      // }
      // catch (BridgeNotFound e)
      // {
      _bridge = createNewBridge();
      pageContext.setAttribute("JSONRPCBridge", _bridge, SCOPE);
      // }
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

  private void writeTesterPage(final String[] testFileNames, final String[] cssFiles) throws IOException
  {
    final JspWriterProxy w = new JspWriterProxy(this.outputWriter);

    writeHtmlHeader(w, cssFiles);
    writeJavascriptIncludes(testFileNames, w);
    writeHtmlBody(w);
  }

  private void writeHtmlHeader(final JspWriterProxy w, final String[] cssFiles) throws IOException
  {
    w.w("<html>");
    w.w("<head>");
    w.w("<title>jsoak</title>");
    writeCssIncludes(w,cssFiles);
    w.w("</head>");
    w.w("<body>");
    w.w("<script type='text/javascript' src='http://getfirebug.com/releases/lite/1.2/firebug-lite-compressed.js'></script>");
	w.w("<script type='text/javascript'>\n");
	
    w.w("var ddl_isIE = false;");
    w.w("</script>\n");
    w.w("<!--[if IE]>\n");
    w.w("<script type='text/javascript'>\n");
    w.w("ddl_isIE = true;\n");
    w.w("</script>\n");
    w.w("<![endif]-->\n");
    w.w("<div id='logging' style='display:none;height:400px;position:absolute;overflow:auto;width:800px;'>");
    w.w("</div>");
  }

  private void writeCssIncludes(final JspWriterProxy w, final String[] cssFiles) throws IOException
  {
    for (String cssFile: cssFiles) 
    {
      w.w("<link rel='stylesheet' href='"+cssFile+"' type='text/css' media='screen' charset='utf-8' />");
    }
  }

  private void writeJavascriptIncludes(final String[] testFileNames,
      final JspWriterProxy w) throws IOException
  {
    List<String> lastOnes = new ArrayList<String>();
    for (final String s : testFileNames)
    {
      if (s.contains("onloadHandler.js"))
      {
        lastOnes.add(s);
      }
      else
      {
        w.i(s);
      }
    }
    for (final String s : lastOnes)
    {
      w.i(s);
    }
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
