package com.wso2.carbon.identity.msf4j.bridge.servlet;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.swagger.util.ReflectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.messaging.Constants;
import org.wso2.msf4j.Microservice;
import org.wso2.msf4j.internal.DataHolder;
import org.wso2.msf4j.internal.MSF4JConstants;
import org.wso2.msf4j.internal.MSF4JHttpConnectorListener;
import org.wso2.msf4j.internal.MicroservicesRegistryImpl;
import org.wso2.transport.http.netty.contract.HttpConnectorListener;
import org.wso2.transport.http.netty.message.HTTPCarbonMessage;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Enumeration;
import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is the Servlet used for bridging.
 */
@WebServlet(urlPatterns = {"/*"},asyncSupported = true)
public class Msf4jBridgeServlet extends HttpServlet {

    private Log log = LogFactory.getLog(Msf4jBridgeServlet.class);

    private MicroservicesRegistryImpl msRegistry = new MicroservicesRegistryImpl();
    private MSF4JHttpConnectorListener msf4JHttpConnectorListener;
    private TomcatBridgeListener tomcatBridgeListener;

    private static final String HTTP_ASYNC_CONTEXT = "HttpAsyncContextProperty";
    private static final String SERVICES_LIST = "ServiceClasses";

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        String serviceClasses = servletConfig.getInitParameter(SERVICES_LIST);
        if (serviceClasses == null) {
            //No services present. TODO: Log errors.
            return;
        }
        String[] serviceClassesList = serviceClasses.split(",");
        for (String serviceClass : serviceClassesList) {
            try {
                Class cls = Class.forName(serviceClass);
                boolean hasPathAnnotation = (ReflectionUtils.getAnnotation(cls, javax.ws.rs.Path.class) != null);
                if (hasPathAnnotation) {
                    Object serviceObject = cls.newInstance();
                    msRegistry.addService(serviceObject);
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                log.error("An error occurred while trying to add service to registry.");
            }

        }
        this.init();
    }

    public void init() throws ServletException {

        msRegistry.getSessionManager().init();
        msRegistry.initServices();
        DataHolder.getInstance().getMicroservicesRegistries().put("tomcat", msRegistry);

        //TODO: Need to get this listener from the container. We should not create thread pools.
        msf4JHttpConnectorListener = new MSF4JHttpConnectorListener();
        tomcatBridgeListener = new TomcatBridgeListener();
    }

    @Override
    public ServletConfig getServletConfig() {

        return null;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AsyncContext asyncContext = request.startAsync(request, response);
        HTTPCarbonMessage httpCarbonMessage = translateMessage(request, response);

        addToWaitingList(httpCarbonMessage, asyncContext);
    }

    private HTTPCarbonMessage translateMessage(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

        HttpMessage httpMessage = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
                new HttpMethod(servletRequest.getMethod()), servletRequest.getRequestURI());
        HTTPCarbonMessage carbonMessage = new TomcatCarbonMessage(httpMessage, servletRequest);
        carbonMessage.setProperty(Constants.PROTOCOL, servletRequest.getProtocol());
        carbonMessage.setProperty(Constants.TO, servletRequest.getRequestURI()
                .substring(servletRequest.getRequestURI().indexOf("/", 1), servletRequest.getRequestURI().length()));
        carbonMessage
                .setProperty(org.wso2.transport.http.netty.common.Constants.HTTP_METHOD, servletRequest.getMethod());
        copyHeaders(carbonMessage, servletRequest);
        carbonMessage.getHttpResponseFuture().setHttpConnectorListener(tomcatBridgeListener);
        return carbonMessage;
    }

    private void copyHeaders(HTTPCarbonMessage carbonMessage, HttpServletRequest servletRequest) {

        Enumeration<String> headerNames = servletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            carbonMessage.setHeader(name, servletRequest.getHeader(name));
        }
    }

    @Override
    public String getServletInfo() {

        return null;
    }

    @Override
    public void destroy() {

    }

    void addToWaitingList(HTTPCarbonMessage httpCarbonMessage, AsyncContext asyncContext) {

        httpCarbonMessage.setProperty(MSF4JConstants.CHANNEL_ID, "tomcat");
        httpCarbonMessage.setProperty(HTTP_ASYNC_CONTEXT, asyncContext);
        msf4JHttpConnectorListener.onMessage(httpCarbonMessage);
    }

    /**
     * Adds the micro-service to the Servlet.
     *
     * @param service
     */
    public void addMicroServiceToRegistry(Microservice service) {

        msRegistry.addService(service);
    }

    /**
     * Removes the micro-service from the Servlet.
     *
     * @param service
     */
    public void removeMicroServiceFromRegistry(Microservice service){

        msRegistry.removeService(service);
    }

    private class TomcatBridgeListener implements HttpConnectorListener {

        private Log log = LogFactory.getLog(TomcatBridgeListener.class);

        @Override
        public void onMessage(HTTPCarbonMessage httpCarbonMessage) {

            AsyncContext asyncContex = (AsyncContext) httpCarbonMessage.getProperty(HTTP_ASYNC_CONTEXT);
            if (asyncContex != null) {
                try {
                    WritableByteChannel channel = Channels.newChannel(asyncContex.getResponse().getOutputStream());
                    httpCarbonMessage.getFullMessageBody().stream().forEach(bb -> {
                        try {
                            channel.write(bb);
                        } catch (IOException e) {
                            log.error("An I/O error occurred while trying to write to Channel.");
                        }
                    });
                    asyncContex.complete();

                } catch (IOException e) {
                    log.error("An I/O error occurred.");
                }
            }
        }

        @Override
        public void onError(Throwable throwable) {

        }
    }

}
