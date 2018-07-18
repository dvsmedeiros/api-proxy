package com.muralis.apiproxy.domain;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class RequestVO {

	private String host;
	private int port;
	private String method;
	private String uri;
	private String url;
	private Map<String, String> params;
	private Map<String, String> headers;
	private String content;

	public RequestVO(HttpServletRequest req){
		this.method = req.getMethod();
		//this.headers = getHeadersInfo(req);
		this.params = getParametersInfo(req);
		this.url = req.getRequestURL().toString();
		this.uri = req.getRequestURI();
		this.host = req.getServerName();
		this.port = req.getServerPort();
		try {
			this.content = IOUtils.toString(req.getReader());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Map<String, String> getHeadersInfo(HttpServletRequest request) {

		Map<String, String> map = new HashMap<String, String>();

		Enumeration<?> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}
		return map;
	}

	private Map<String, String> getParametersInfo(HttpServletRequest request) {

		Map<String, String> map = new HashMap<String, String>();

		Enumeration<?> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String key = (String) params.nextElement();
			String value = request.getParameter(key);
			map.put(key, value);
		}

		return map;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
