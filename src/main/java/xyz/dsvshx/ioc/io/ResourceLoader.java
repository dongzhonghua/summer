package xyz.dsvshx.ioc.io;

import java.net.URL;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
public class ResourceLoader {
    public Resource getResource(String location) {
        URL url = this.getClass().getClassLoader().getResource(location);
        return new UrlResource(url);
    }
}
