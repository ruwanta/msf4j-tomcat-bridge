package com.wso2.carbon.identity.msf4j.bridge.servlet;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMessage;
import org.wso2.transport.http.netty.message.HTTPCarbonMessage;

import javax.servlet.http.HttpServletRequest;

/**
 * Reimplement the Carbon Message which bridges tomcat and msf4j.
 */
public class TomcatCarbonMessage extends HTTPCarbonMessage {

    private HttpServletRequest httpServletRequest;

    public TomcatCarbonMessage(HttpMessage httpMessage, HttpServletRequest httpServletRequest) {
        super(httpMessage);
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public HttpContent getHttpContent() {
        return new ServletWrappedHttpContent(httpServletRequest);
    }
}
