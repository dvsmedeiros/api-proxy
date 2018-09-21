package com.muralis.apiproxy.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muralis.apiproxy.domain.RequestVO;
import com.muralis.apiproxy.domain.ResponseVO;

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
	
	@GetMapping(value = "**", produces = { "application/json", "application/xml" }
	)
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
			
			logger.info("GET              :" + req.getRequestURL());
			logger.info("REDIRECT         :" + httpget.getURI().getAuthority() + httpget.getURI().getPath());
			logger.info("QUERY PARAM      :" + httpget.getURI().getQuery());
			logger.info("QUERY PARAM JSON :\n" + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(requestVO.getParams()));
			HttpResponse response = httpclient.execute(httpget);
			logger.info("STATUS  RESPONSE :" + response.getStatusLine().getStatusCode());
			Header contentType = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
			String contentTypeValue = contentType != null ? response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue() : "";
			logger.info("CONTENT-TYPE     :" + contentTypeValue);
			boolean isXml = contentType != null && contentTypeValue.contains("xml") ? true : false;
			
			if(response.getStatusLine().getStatusCode() < 400 && !isXml ) {
				JsonNode json = parseHttpEntityToJson(response.getEntity());
				logger.info("CONTENT RESPONSE :\n" + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json));
				return ResponseEntity.ok(json);
			}
			
			if (response.getStatusLine().getStatusCode() < 400 && isXml) {

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				try {
					DocumentBuilder docBuilder = factory.newDocumentBuilder();					
					Document doc;
					doc = docBuilder.parse(response.getEntity().getContent());
					Element element = doc.getDocumentElement();
					NodeList nodes = element.getChildNodes();
					logger.info("CONTENT RESPONSE :\n" + nodes);
					return ResponseEntity.ok(nodes);
				} catch (UnsupportedOperationException | SAXException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
			}
			
			ResponseVO resp = new ResponseVO(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
			resp.setQueryParam(httpget.getURI().getQuery());
			resp.setJsonParam(requestVO.getParams());
			resp.setGet(req.getRequestURL().toString());
			resp.setRedirect(httpget.getURI().getAuthority() + httpget.getURI().getPath());			
			
			return ResponseEntity.ok(resp);
			
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
