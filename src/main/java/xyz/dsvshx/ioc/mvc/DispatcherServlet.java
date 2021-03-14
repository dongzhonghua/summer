package xyz.dsvshx.ioc.mvc;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author dongzhonghua
 * Created on 2021-03-12
 */
public interface DispatcherServlet {
    FullHttpResponse handle(FullHttpRequest fullHttpRequest);
}
