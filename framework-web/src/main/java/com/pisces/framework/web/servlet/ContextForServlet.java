package com.pisces.framework.web.servlet;

import com.pisces.framework.web.context.PiscesContext;
import com.pisces.framework.web.context.PiscesRequest;
import com.pisces.framework.web.context.PiscesResponse;

/**
 * servlet上下文
 *
 * @author jason
 * @date 2023/07/09
 */
public class ContextForServlet implements PiscesContext {
    
    @Override
    public PiscesRequest getRequest() {
        return new RequestForServlet(SpringMVCUtils.getRequest());
    }

    @Override
    public PiscesResponse getResponse() {
        return new ResponseForServlet(SpringMVCUtils.getResponse());
    }
}
