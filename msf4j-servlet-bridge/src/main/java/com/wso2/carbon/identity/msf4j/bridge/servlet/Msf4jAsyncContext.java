/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.wso2.carbon.identity.msf4j.bridge.servlet;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletResponse;

/**
 * Asynchronous context for Servlet and MSF4J system.
 * This is used as we do no use Servlet Async Context.
 * We can remove ths class when all the servlets, valves, and filters support Async.
 */
public class Msf4jAsyncContext {

    private HttpServletResponse httpServletResponse;
    private final CountDownLatch msf4jEngineLatch = new CountDownLatch(1);

    public Msf4jAsyncContext(HttpServletResponse httpServletResponse) {

        this.httpServletResponse = httpServletResponse;
    }

    public void awaitForMsf4j(long time, TimeUnit timeUnit) throws InterruptedException {

        msf4jEngineLatch.await();
    }

    public void complete() {

        msf4jEngineLatch.countDown();
    }

    public HttpServletResponse getResponse() {

        return httpServletResponse;
    }
}
