package com.muralis.apiproxy.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muralis.apiproxy.domain.HttpStatusVO;
import com.muralis.apiproxy.domain.RequestVO;

@RestController
public class ProxyController {

	private Logger logger = LoggerFactory.getLogger("API-PROXY");
	
	@Value("${server.servlet.context-path}")
	private String contextPath;
	@Value("${server.address}")
	private String hostRequested;
	@Value("${server.port}")
	private Integer portRequested;
	@Value("${server.redirect}")
	private String hostToRedirect;
	@Value("${server.port.redirect}")
	private Integer portToRedirect;
	
	@GetMapping("**")
	public @ResponseBody ResponseEntity<?> redirectTo(@Autowired HttpServletRequest req) {

		HttpGet httpget = null;
		try {
			logger.info("server.servlet.context-path: " + contextPath);
			logger.info("server.address             : " + hostRequested);
			logger.info("server.port                : " + portRequested);
			logger.info("server.redirect            : " + hostToRedirect);
			logger.info("server.port.redirect       : " + portToRedirect);
			
			RequestVO requestVO = new RequestVO(req);

			HttpClient httpclient = HttpClients.createDefault();

			URIBuilder builder = new URIBuilder().setPath(requestVO.getUri()).setScheme("http").setHost(hostToRedirect)
					.setPort(portToRedirect);

			requestVO.getParams().forEach((key, value) -> builder.addParameter(key, value));

			httpget = new HttpGet(builder.build());

			// HttpHost proxy = new HttpHost("200.220.138.12", 80, "http");
			// RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
			// httpget.setConfig(config);			
			logger.info("GET              :" + req.getRequestURL());
			logger.info("REDIRECT         :" + httpget.getURI().getAuthority() + httpget.getURI().getPath());
			logger.info("QUERY PARAM      :" + httpget.getURI().getQuery());
			logger.info("QUERY PARAM JSON :\n"
					+ new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(requestVO.getParams()));
			HttpResponse response = httpclient.execute(httpget);
			logger.info("STATUS  RESPONSE :" + response.getStatusLine().getStatusCode());
			if(response.getStatusLine().getStatusCode() < 400) {
				JsonNode json = parseHttpEntityToJson(response.getEntity());
				logger.info("CONTENT RESPONSE :\n"
						+ new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json));
				return ResponseEntity.ok(json);
			}
			HttpStatusVO status = new HttpStatusVO(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
			return ResponseEntity.ok(status);
		} catch (URISyntaxException | ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpget.releaseConnection();
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	private JsonNode parseHttpEntityToJson(HttpEntity entity) throws UnsupportedOperationException, IOException {
		String content = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8.name());
		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = mapper.readTree(content);
		return json;
	}
}
