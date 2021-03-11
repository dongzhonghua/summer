package xyz.dsvshx.ioc.io;

import java.io.InputStream;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
public interface Resource {

    /**
     * 通过输入流的方式获取资源
     * @return 待获取资源的输入流
     * @throws Exception IO异常
     */
    InputStream getInputStream() throws Exception;

}
