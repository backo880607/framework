package com.pisces.framework.core.service;

import com.pisces.framework.core.locale.Message;

/**
 * 基础服务impl
 *
 * @author jason
 * @date 2022/12/07
 */
public abstract class BaseServiceImpl implements BaseService {
    protected final Message LOG = new Message(this.getClass());
}
