package org.jsoak;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;

import org.jabsorb.JSONRPCServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.ServletHolder;

public class JsoakServer
{
  public final String WEB_APP_BASE_DIR = "";

  private final String TEST_SERVLET_PATH = "/TESTER_SERVLET";
  
  private final String REDIRECT_SERVLET_PATH = "/REDIRECT_SERVLET";
  
  private final String webContentDirectory;

  private final String redirectLocation;
  
  private final Server server;

  private final int port;

  /**
   * Creates a new webserver and starts it
   * 
   * @param port
   *          The port the server runs on
   */
  public JsoakServer(int port, Servlet servlet, String webContentDirectory, String redirectLocation)
  {
    this.port = port;
    this.server = new Server(port);
    this.webContentDirectory = webContentDirectory;
    this.redirectLocation = redirectLocation;
    initialiseServer(servlet);
  }

  public void start() throws Exception
  {
    this.server.start();
  }

  /**
   * Puts the necessary servlets on the server
   */
  private void initialiseServer(Servlet servlet)
  {
    Context context = createContext();
    configureDefaultServlet(context);
    configureTestServlet(servlet, context);
    configureRedirectServlet(context);
    configureJabsorbServlet(context);
  }

  private Context createContext()
  {
    Context context = new Context(this.server, WEB_APP_BASE_DIR,
        Context.SESSIONS);
    context.setContextPath(WEB_APP_BASE_DIR);
    context.setResourceBase(webContentDirectory);
    context.setAttribute("copyWebDir", "true");
    Map<String,String> initParams=new HashMap<String, String>();
    initParams.put("cacheControl", "no-cache");
    context.setInitParams(initParams);
    return context;
  }

  private void configureDefaultServlet(Context context)
  {
    ServletHolder defaultServlet = new ServletHolder(new DefaultServlet());
    context.addServlet(defaultServlet, "/");
  }

  private void configureTestServlet(Servlet servlet, Context context)
  {
    ServletHolder _servlet = new ServletHolder(servlet);
    _servlet.setInitParameter("auto-session-bridge", "0");
    context.addServlet(_servlet, this.getTestServletPath());
  }
  private void configureRedirectServlet(Context context)
  {
    ServletHolder _servlet = new ServletHolder(new RedirectServlet(redirectLocation));
    _servlet.setInitParameter("auto-session-bridge", "0");
    context.addServlet(_servlet, REDIRECT_SERVLET_PATH);
  }
  private void configureJabsorbServlet(Context context)
  {
    ServletHolder _servlet = new ServletHolder(new JSONRPCServlet());
    _servlet.setInitParameter("auto-session-bridge", "0");
    context.addServlet(_servlet, "/JSON-RPC");
  }

  /**
   * Stops the server
   * 
   * @throws Exception
   *           if jetty has issues stopping
   */
  public void stop()
  {
    try
    {
      this.server.stop();
    }
    catch (Exception e)
    {
      // I don't think we care if it can't stop properly?
    }
  }

  public int getPort()
  {
    return this.port;
  }

  public String getTestServletPath()
  {
    return TEST_SERVLET_PATH;
  }

}
