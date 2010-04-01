package org.jsoak;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jabsorb.client.ClientError;

public class RedirectServlet extends HttpServlet {
	private final String location;
	public RedirectServlet(String location) {
		this.location = location; 
	}
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse resp)
			throws ServletException, IOException {
		String charset = request.getCharacterEncoding();
		if (charset == null) {
			charset = "UTF-8";
		}
		final int buf_size = 4096;
		BufferedReader in = new BufferedReader(new InputStreamReader(request
				.getInputStream(), charset));

		String message = null;
		CharArrayWriter data = new CharArrayWriter();
		char buf[] = new char[buf_size];
		int ret;
		while ((ret = in.read(buf, 0, buf_size)) != -1) {
			data.write(buf, 0, ret);
		}
		message = data.toString();

		String contentType = "application/x-www-form-urlencoded";
		PostMethod postMethod = new PostMethod(location);
		postMethod.setRequestHeader("Content-Type", contentType);
		RequestEntity requestEntity = new StringRequestEntity(message
				.toString(), contentType, null);
		postMethod.setRequestEntity(requestEntity);
		http().executeMethod(null, postMethod, state);
		int statusCode = postMethod.getStatusCode();
		if (statusCode != HttpStatus.SC_OK)
			throw new ClientError("HTTP Status - "
					+ HttpStatus.getStatusText(statusCode) + " (" + statusCode
					+ ")");

		BufferedOutputStream out = new BufferedOutputStream(resp
				.getOutputStream());
		out.write(postMethod.getResponseBodyAsString().getBytes());
		out.flush();
    out.close();
	}

	private HttpClient client;
	private HttpState state;

	private HttpClient http() {
		if (client == null) {
			client = new HttpClient();
			if (state == null) {
				state = new HttpState();
			}
			client.setState(state);
		}
		return client;
	}
}