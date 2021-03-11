package xyz.dsvshx.ioc.io;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
public class UrlResource implements Resource {
    private URL url;

    public UrlResource(URL url) {
        this.url = url;
    }

    public InputStream getInputStream() throws Exception {
        URLConnection urlConnection = url.openConnection();
        urlConnection.connect();
        return urlConnection.getInputStream();
    }
}
