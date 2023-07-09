package com.pisces.framework.web.context;

public interface PiscesContext {
    /**
     * 获取当前请求的 [Request] 对象
     *
     * @return see note
     */
    PiscesRequest getRequest();

    /**
     * 获取当前请求的 [Response] 对象
     *
     * @return see note
     */
    PiscesResponse getResponse();
}
