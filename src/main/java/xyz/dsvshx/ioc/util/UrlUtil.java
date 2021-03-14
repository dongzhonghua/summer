package xyz.dsvshx.ioc.util;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;
import xyz.dsvshx.ioc.annotation.mvc.PathVariable;
import xyz.dsvshx.ioc.annotation.mvc.RequestBody;
import xyz.dsvshx.ioc.annotation.mvc.RequestParam;
import xyz.dsvshx.ioc.entity.MethodDetail;

/**
 * @author dongzhonghua
 * Created on 2021-03-13
 */
public class UrlUtil {


    /**
     * 把url中的参数注入到方法的参数中去。
     */
    public static Object resolveParam(MethodDetail methodDetail, Parameter parameter) {
        if (parameter.isAnnotationPresent(RequestParam.class)) {
            RequestParam requestParam = parameter.getDeclaredAnnotation(RequestParam.class);
            String requestParameter = requestParam.value();
            String requestParameterValue = methodDetail.getQueryParameterMappings().get(requestParameter);
            if (requestParameterValue == null) {
                if (requestParam.require() && requestParam.defaultValue().isEmpty()) {
                    throw new IllegalArgumentException(
                            "The specified parameter " + requestParameter + " can not be null!");
                } else {
                    requestParameterValue = requestParam.defaultValue();
                }
            }
            // convert the parameter to the specified type
            return ClassUtils.convert(parameter.getType(), requestParameterValue);
        }
        if (parameter.isAnnotationPresent(PathVariable.class)) {
            PathVariable pathVariable = parameter.getDeclaredAnnotation(PathVariable.class);
            String requestParameter = pathVariable.value();
            Map<String, String> urlParameterMappings = methodDetail.getUrlParameterMappings();
            String requestParameterValue = urlParameterMappings.get(requestParameter);
            return ClassUtils.convert(parameter.getType(), requestParameterValue);
        }

        if (parameter.isAnnotationPresent(RequestBody.class)) {
            Object param = null;
            RequestBody requestBody = parameter.getDeclaredAnnotation(RequestBody.class);
            if (requestBody != null) {
                try {
                    param = new ObjectMapper().readValue(methodDetail.getJson(), parameter.getType());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            return param;

        }
        return null;
    }


    public static String getRequestPath(String uri) {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, Charsets.toCharset(CharEncoding.UTF_8));
        return queryDecoder.path();
    }

    public static String getContentType(HttpHeaders headers) {
        String typeStr = headers.get("Content-Type");
        String[] list = typeStr.split(";");
        return list[0];
    }

    /**
     * format the url
     * for example : "/user/{name}" -> "^/user/[\u4e00-\u9fa5_a-zA-Z0-9]+/?$"
     */
    public static String formatUrl(String url) {
        // replace {xxx} placeholders with regular expressions matching Chinese, English letters and numbers, and
        // underscores
        String originPattern = url.replaceAll("(\\{\\w+})", "[\\\\u4e00-\\\\u9fa5_a-zA-Z0-9]+");
        String pattern = "^" + originPattern + "/?$";
        return pattern.replaceAll("/+", "/");
    }

    public static Map<String, String> getQueryParams(String uri) {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, Charsets.toCharset(CharEncoding.UTF_8));
        Map<String, List<String>> parameters = queryDecoder.parameters();
        Map<String, String> queryParams = new HashMap<>();
        for (Map.Entry<String, List<String>> attr : parameters.entrySet()) {
            for (String attrVal : attr.getValue()) {
                queryParams.put(attr.getKey(), attrVal);
            }
        }
        return queryParams;
    }
}
