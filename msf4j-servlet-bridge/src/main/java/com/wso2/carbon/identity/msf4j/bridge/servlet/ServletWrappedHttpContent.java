package com.wso2.carbon.identity.msf4j.bridge.servlet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpContent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * Maps netty Http to Servlet world. Used in msf4j Servlet Bridge.
 */
public class ServletWrappedHttpContent implements HttpContent {

    private Log log = LogFactory.getLog(Msf4jBridgeServlet.class);
    private HttpServletRequest httpServletRequest;
    private InputStream inputStream;

    public ServletWrappedHttpContent(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public ByteBuf content() {
        if (inputStream == null) {
            try {
                inputStream = httpServletRequest.getInputStream();
            } catch (IOException e) {
                log.error("An error occurred while trying to access input stream.");
            }
        }
        if (inputStream != null) {
            byte[] byteArray = new byte[128];
            try {
                int len = inputStream.read(byteArray);
                if (len > 0) {
                    return Unpooled.wrappedBuffer(byteArray, 0, len);
                }
            } catch (IOException e) {
                log.error("An error occurred while trying to read from stream.");
            }
        }
        return Unpooled.EMPTY_BUFFER;
    }

    @Override
    public HttpContent copy() {
        return null;
    }

    @Override
    public HttpContent duplicate() {
        return null;
    }

    @Override
    public HttpContent retainedDuplicate() {
        return null;
    }

    @Override
    public HttpContent replace(ByteBuf byteBuf) {
        return null;
    }

    @Override
    public int refCnt() {
        return 0;
    }

    @Override
    public HttpContent retain() {
        return null;
    }

    @Override
    public HttpContent retain(int i) {
        return null;
    }

    @Override
    public HttpContent touch() {
        return null;
    }

    @Override
    public HttpContent touch(Object o) {
        return null;
    }

    @Override
    public boolean release() {
        return false;
    }

    @Override
    public boolean release(int i) {
        return false;
    }

    @Override
    public DecoderResult getDecoderResult() {
        return null;
    }

    @Override
    public DecoderResult decoderResult() {
        return null;
    }

    @Override
    public void setDecoderResult(DecoderResult decoderResult) {

    }
}
