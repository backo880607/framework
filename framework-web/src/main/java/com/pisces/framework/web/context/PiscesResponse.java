package com.pisces.framework.web.context;

public interface PiscesResponse {
    /**
     * 指定前端可以获取到哪些响应头时使用的参数名
     */
    String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    /**
     * 获取底层源对象
     *
     * @return see note
     */
    Object getSource();

    /**
     * 删除指定Cookie
     *
     * @param name Cookie名称
     */
    default void deleteCookie(String name) {
        addCookie(name, null, null, null, 0);
    }

    /**
     * 删除指定Cookie
     *
     * @param name   Cookie名称
     * @param path   Cookie 路径
     * @param domain Cookie 作用域
     */
    default void deleteCookie(String name, String path, String domain) {
        addCookie(name, null, path, domain, 0);
    }

    /**
     * 写入指定Cookie
     *
     * @param name    Cookie名称
     * @param value   Cookie值
     * @param path    Cookie路径
     * @param domain  Cookie的作用域
     * @param timeout 过期时间 （秒）
     */
    default void addCookie(String name, String value, String path, String domain, int timeout) {
        PiscesCookie cookie = new PiscesCookie(name, value);
        cookie.setPath(path);
        cookie.setDomain(domain);
        cookie.setMaxAge(timeout);
        this.addCookie(cookie);
    }

    /**
     * 写入指定Cookie
     *
     * @param cookie Cookie-Model
     */
    default void addCookie(PiscesCookie cookie) {
        this.addHeader(PiscesCookie.HEADER_NAME, cookie.toHeaderValue());
    }

    void setHeader(String name, String value);

    void addHeader(String name, String value);

    /**
     * 设置响应状态码
     *
     * @param sc 响应状态码
     */
    void setStatus(int sc);

    /**
     * 重定向
     *
     * @param url 重定向地址
     * @return 任意值
     */
    Object redirect(String url);
}
