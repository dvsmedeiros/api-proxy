package com.muralis.apiproxy.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muralis.apiproxy.domain.RequestVO;

@RestController
public class ProxyController {
	
	@Value("${server.redirect}")
	private String hostToRedirect;
	@Value("${server.port.redirect}")
	private Integer portToRedirect;
	
	@GetMapping("**")
	public @ResponseBody ResponseEntity<?> redirectTo(@Autowired HttpServletRequest req) {

		HttpGet httpget = null;
		try {
			RequestVO requestVO = new RequestVO(req);

			HttpClient httpclient = HttpClients.createDefault();

			URIBuilder builder = new URIBuilder()
					.setPath(requestVO.getUri())
					.setScheme("http")
					.setHost(hostToRedirect)
					.setPort(portToRedirect);
//					.setHost("200.220.138.12")
//					.setPort(80);
			
			requestVO.getParams().forEach((key, value) -> builder.addParameter(key, value));
			
			httpget = new HttpGet(builder.build());

			//HttpHost proxy = new HttpHost("200.220.138.12", 80, "http");
			//RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
			//httpget.setConfig(config);
			
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			String content = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8.name());
			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = mapper.readTree(content);
			
			return ResponseEntity.ok(json);
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally {
			httpget.releaseConnection();
		}
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
}