package io.skylab.logging_module.provider;

import net.logstash.logback.composite.AbstractFieldJsonProvider;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonGenerator;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpRequestResponseJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent> {
	public HttpRequestResponseJsonProvider() {
		super();
		setFieldName("request");
	}

	@Override
	public void writeTo(JsonGenerator generator, ILoggingEvent value) throws IOException {
		// Retrieve the HttpServletRequest and HttpServletResponse from the current
		// thread's context
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
		HttpServletResponse response = attributes != null ? attributes.getResponse() : null;

		if (request != null && response != null) {
			generator.writeStringField("host", request.getHeader("Host") != null ? request.getHeader("Host") : "");
			generator.writeStringField("http_method", request.getMethod());
			generator.writeStringField("protocol", request.getProtocol());
			generator.writeStringField("query_string",
					request.getQueryString() != null ? request.getQueryString() : "");
			generator.writeStringField("referer",
					request.getHeader("Referer") != null ? request.getHeader("Referer") : "");
			generator.writeStringField("remote_addr", request.getRemoteAddr());
			generator.writeStringField("remote_host", request.getRemoteHost());
			generator.writeStringField("request_timestamp", String.valueOf(System.currentTimeMillis()));
			generator.writeNumberField("status_code", response.getStatus());
			generator.writeStringField("uri", request.getRequestURI());
			generator.writeStringField("url", request.getRequestURL().toString());
			generator.writeStringField("user_agent",
					request.getHeader("User-Agent") != null ? request.getHeader("User-Agent") : "");
			generator.writeStringField("x_forwarded_for",
					request.getHeader("X-Forwarded-For") != null ? request.getHeader("X-Forwarded-For") : "");
		}
	}
}
