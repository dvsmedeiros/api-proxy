package com.muralis.apiproxy.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ProxyInterceptor extends HandlerInterceptorAdapter {

	private Logger logger = LoggerFactory.getLogger("PROXY-INTERCEPTOR");
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		long startTime = System.currentTimeMillis();
		logger.info("Request URL: " + request.getRequestURL());
		request.setAttribute("startTime", startTime);
		return true;

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		logger.info("Request URL: " + request.getRequestURL());
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		long startTime = (Long) request.getAttribute("startTime");
		long endTime = System.currentTimeMillis();
		logger.info("Request URL: " + request.getRequestURL());		

		logger.info("Time Taken: " + (endTime - startTime) + " (ms)");
	}
}
