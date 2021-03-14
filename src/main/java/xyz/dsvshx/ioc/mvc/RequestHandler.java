package xyz.dsvshx.ioc.mvc;

import static xyz.dsvshx.ioc.util.HttpResponseUtils.getSuccessResponse;
import static xyz.dsvshx.ioc.util.UrlUtil.formatUrl;
import static xyz.dsvshx.ioc.util.UrlUtil.getContentType;
import static xyz.dsvshx.ioc.util.UrlUtil.getQueryParams;
import static xyz.dsvshx.ioc.util.UrlUtil.resolveParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import xyz.dsvshx.ioc.annotation.mvc.GetMapping;
import xyz.dsvshx.ioc.annotation.mvc.PostMapping;
import xyz.dsvshx.ioc.annotation.mvc.RestController;
import xyz.dsvshx.ioc.entity.MethodDetail;
import xyz.dsvshx.ioc.util.BeanUtils;
import xyz.dsvshx.ioc.util.ClassUtils;
import xyz.dsvshx.ioc.util.UrlUtil;

/**
 * @author dongzhonghua
 * Created on 2021-03-13
 */
@Slf4j
public class RequestHandler {
    private static Map<HttpMethod, Map<String, Method>> handlerMapping;
    private static Map<HttpMethod, Map<String, String>> handlerUrlMapping;
    private static Map<String, Object> controllerMap;
    private static Set<Class<?>> webClassSet;

    static {
        handlerMapping = new HashMap<>();
        handlerMapping.put(HttpMethod.GET, new HashMap<>());
        handlerMapping.put(HttpMethod.POST, new HashMap<>());
        handlerUrlMapping = new HashMap<>();
        handlerUrlMapping.put(HttpMethod.GET, new HashMap<>());
        handlerUrlMapping.put(HttpMethod.POST, new HashMap<>());
        controllerMap = new HashMap<>();
    }

    public static Set<Class<?>> scanner(String basepackage) {
        webClassSet = ClassUtils.findAllClassesByAnnotation(basepackage, RestController.class);
        return webClassSet;
    }

    public static void initHandlerMapping(Map<String, Object> allBeans) {
        if (webClassSet.isEmpty()) {
            return;
        }
        try {
            for (Class<?> clazz : webClassSet) {
                String baseUrl = "";
                if (clazz.isAnnotationPresent(RestController.class)) {
                    baseUrl = clazz.getAnnotation(RestController.class).value();
                }
                Object controllerBean = allBeans.get(ClassUtils.getLowerCamelName(clazz));
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        String url = method.getAnnotation(GetMapping.class).value();
                        mapUrlToMethod(controllerBean, url, baseUrl, method, HttpMethod.GET);
                    } else if (method.isAnnotationPresent(PostMapping.class)) {
                        String url = method.getAnnotation(PostMapping.class).value();
                        mapUrlToMethod(controllerBean, url, baseUrl, method, HttpMethod.POST);
                    }
                }
            }
            log.info(String.valueOf(handlerMapping));
            log.info(String.valueOf(handlerUrlMapping));
            log.info(String.valueOf(controllerMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void mapUrlToMethod(Object controllerBean, String methodUrl, String baseUrl, Method method,
            HttpMethod httpMethod) {
        String url = (baseUrl + "/" + methodUrl).replaceAll("/+", "/");
        String formatUrl = formatUrl(url);
        handlerMapping.get(httpMethod).put(formatUrl, method);
        handlerUrlMapping.get(httpMethod).put(formatUrl, url);
        controllerMap.put(url, controllerBean);
    }

    public static MethodDetail getMethodDetail(String requestPath, HttpMethod httpMethod) {
        MethodDetail methodDetail = new MethodDetail();
        methodDetail.build(requestPath, handlerMapping.get(httpMethod), handlerUrlMapping.get(httpMethod));
        return methodDetail;
    }


    public static FullHttpResponse doHandler(WebApplicationContext webApplicationContext,
            FullHttpRequest fullHttpRequest) {
        HttpMethod method = fullHttpRequest.method();
        if (HttpMethod.GET.equals(method)) {
            return handleGet(webApplicationContext, fullHttpRequest);
        } else if (HttpMethod.POST.equals(method)) {
            return handlePost(webApplicationContext, fullHttpRequest);
        } else {
            log.error("不支持该方法");
            return null;
        }
    }

    public static FullHttpResponse handleGet(WebApplicationContext webApplicationContext,
            FullHttpRequest fullHttpRequest) {
        String requestUri = fullHttpRequest.uri();
        Map<String, String> queryParameterMappings = getQueryParams(requestUri);
        // get http request path，such as "/user"
        String requestPath = UrlUtil.getRequestPath(requestUri);
        MethodDetail methodDetail = getMethodDetail(requestPath, HttpMethod.GET);
        methodDetail.setQueryParameterMappings(queryParameterMappings);
        // get target method
        Method targetMethod = methodDetail.getMethod();
        if (targetMethod == null) {
            return null;
        }
        // get target object
        Object targetObject = null;
        try {
            targetObject = webApplicationContext.getBean(BeanUtils.getBeanName(targetMethod.getDeclaringClass()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("requestPath -> target object [{}], target method [{}]", targetObject, targetMethod.getName());

        Parameter[] targetMethodParameters = targetMethod.getParameters();
        // target method parameters.
        // notice! you should convert it to array when pass into the executeMethod method
        List<Object> targetMethodParams = new ArrayList<>();
        for (Parameter parameter : targetMethodParameters) {
            Object param = resolveParam(methodDetail, parameter);
            targetMethodParams.add(param);
        }
        return getSuccessResponse(targetMethod, targetMethodParams, targetObject);
    }

    public static FullHttpResponse handlePost(WebApplicationContext webApplicationContext,
            FullHttpRequest fullHttpRequest) {
        String requestUri = fullHttpRequest.uri();
        // get http request path，such as "/user"
        String requestPath = UrlUtil.getRequestPath(requestUri);
        // get target method
        MethodDetail methodDetail = getMethodDetail(requestPath, HttpMethod.POST);
        Method targetMethod = methodDetail.getMethod();
        if (targetMethod == null) {
            return null;
        }
        // get target object
        Object targetObject = null;
        try {
            targetObject = webApplicationContext.getBean(BeanUtils.getBeanName(targetMethod.getDeclaringClass()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("requestPath -> target object [{}], target method [{}]", targetObject, targetMethod.getName());

        String contentType = getContentType(fullHttpRequest.headers());
        // target method parameters.
        // notice! you should convert it to array when pass into the executeMethod()
        List<Object> targetMethodParams = new ArrayList<>();
        if (contentType.equals("application/json")) {
            String json = fullHttpRequest.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
            methodDetail.setJson(json);
            Parameter[] targetMethodParameters = targetMethod.getParameters();
            for (Parameter parameter : targetMethodParameters) {
                Object param = resolveParam(methodDetail, parameter);
                targetMethodParams.add(param);
            }
        } else {
            throw new IllegalArgumentException("only receive application/json type data");
        }
        return getSuccessResponse(targetMethod, targetMethodParams, targetObject);
    }
}
