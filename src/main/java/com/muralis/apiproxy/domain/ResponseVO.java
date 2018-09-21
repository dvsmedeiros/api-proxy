package com.muralis.apiproxy.domain;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ResponseVO {

	private int code;
	private String description;
	private String get;
	private String redirect;
	private String queryParam;
	private Map<String, String> jsonParam;

	public ResponseVO() {
	};

	public ResponseVO(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGet() {
		return get;
	}

	public void setGet(String get) {
		this.get = get;
	}

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public String getQueryParam() {
		return queryParam;
	}

	public void setQueryParam(String queryParam) {
		this.queryParam = queryParam;
	}

	public Map<String, String> getJsonParam() {
		return jsonParam;
	}

	public void setJsonParam(Map<String, String> jsonParam) {
		this.jsonParam = jsonParam;
	}

}
