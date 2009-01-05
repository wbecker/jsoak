package org.jsoak;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.GetMethod;

public class ServerRequester
{
  private HttpClient client;

  private HttpState state;

  public ServerRequester()
  {
    client = new HttpClient();
    state = new HttpState();
    client.setState(state);
  }

  public void requestPage(String uriString) throws IOException, HttpException
  {
    GetMethod getMethod = new GetMethod(uriString);
    getMethod.setRequestHeader("Content-Type", "text/plain");
    client.executeMethod(null, getMethod, state);
  }

}
