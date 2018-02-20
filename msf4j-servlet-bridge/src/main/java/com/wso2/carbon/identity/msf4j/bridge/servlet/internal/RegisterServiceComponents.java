package com.wso2.carbon.identity.msf4j.bridge.servlet.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;


@Component(name = "msf4j.bridge.component",
        immediate = true)
public class RegisterServiceComponents {

    private static final Log log = LogFactory.getLog(RegisterServiceComponents.class);

    @Activate
    protected void activate(BundleContext bundleContext) {

        log.info("MSF4J - Servlet bridge activated Successfully.");
    }
}
