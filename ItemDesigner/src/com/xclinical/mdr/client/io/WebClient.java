package com.xclinical.mdr.client.io;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.xclinical.mdr.client.util.LoginUtils;
import com.xclinical.mdr.repository.HasKey;

public class WebClient {

	private final StringBuilder url;
	
	private static final String SERVICE_URL = getServiceUrl();

	private Request pendingRequest;

	private static final String PATTERN = "(https?://[^/]*)/";
		
	public static String getHostUrl() {
		RegExp re = RegExp.compile(PATTERN);
		MatchResult res = re.exec(GWT.getModuleBaseURL());
		String baseUrl = res.getGroup(1);			
		return baseUrl;
	}
	
	public static String getServiceUrl() {
		return GWT.getModuleBaseURL() + "mdr/";
	}
	
	public static String makeServiceUrl(String service) {
		return SERVICE_URL + service;
	}
	
	private WebClient(String url) {
		this.url = new StringBuilder(makeServiceUrl(url));
	}

	
	public static WebClient create(HasKey context) {
		return new WebClient(context.getKey().toString());
	}

	public static WebClient create(String context) {
		return new WebClient(context);
	}
	
	public WebClient append(String name) {
		url.append('/');
		url.append(name);
		return this;
	}

	public WebClient append(HasKey element) {
		url.append('/');
		url.append(element.getKey().toString());
		return this;
	}

	private RequestBuilder newBuilder(RequestBuilder.Method method) {
		RequestBuilder builder = new RequestBuilder(method, url.toString());
		String session = LoginUtils.currentSession();
		if (session != null) {
			builder.setHeader("session", session);
		}
		return builder;
	}
	
	public <T extends JavaScriptObject> void sendRequest(Command cmd, final OnSuccessHandler<T> onResult, final OnErrorHandler onError) {
		RequestBuilder builder = newBuilder(RequestBuilder.POST);
		try {				
			String json = new JSONObject(cmd).toString();
			GWT.log("JSON Request[" + builder.getUrl() + "]:" + json);
			pendingRequest = builder.sendRequest(json, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					String json = response.getText();
					GWT.log("JSON Response:" + json);

					if (response.getStatusCode() == 200) {
						T t = JsonUtils.unsafeEval(json);
						onResult.onSuccess(t);						
					}
					else {
						ErrorResponse resp = JsonUtils.unsafeEval(json);
						onError.onError(resp);
					}
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					onError.onError(ErrorResponse.newClientError(exception.getLocalizedMessage(), exception.toString()));
				}
			});
		} catch (RequestException e) {
			onError.onError(ErrorResponse.newClientError(e.getLocalizedMessage(), e.toString()));
		}
	}
	
	public void cancel() {
		if (pendingRequest != null && pendingRequest.isPending()) {
			pendingRequest.cancel();
		}
	}
}
