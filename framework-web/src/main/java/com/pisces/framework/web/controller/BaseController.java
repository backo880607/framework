package com.pisces.framework.web.controller;

import com.pisces.framework.core.exception.BaseException;
import com.pisces.framework.core.locale.LanguageService;
import com.pisces.framework.core.locale.LocaleManager;
import com.pisces.framework.core.locale.Message;
import com.pisces.framework.web.config.WebMessage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 基本控制器
 *
 * @author jason
 * @date 2022/12/08
 */
public abstract class BaseController {
    protected Message LOG = new Message(this.getClass());
    @Autowired
    protected LanguageService lang;

    private ResponseData result(boolean success, Object data, Enum<?> message, Object... arguments) {
        ResponseData responseResult = new ResponseData();
        responseResult.setSuccess(success);
        if (message != null) {
            responseResult.setStatus(message.ordinal());
            responseResult.setName(message.name());
            responseResult.setMessage(LocaleManager.getLanguage(message, arguments));
        }
        responseResult.setData(data);
        return responseResult;
    }

    protected ResponseData exception(Exception ex, Enum<?> message, Object... arguments) {
        if (message == null) {
            message = WebMessage.System;
        }
        ResponseData responseResult = new ResponseData();
        responseResult.setSuccess(false);
        responseResult.setStatus(message.ordinal());
        responseResult.setName(message.name());
        responseResult.setMessage(LocaleManager.getLanguage(message, arguments));
        responseResult.setException(ex.getMessage());
        responseResult.setData(null);
        return responseResult;
    }

    protected ResponseData success() {
        return success(null);
    }

    protected ResponseData success(Object data) {
        return success(data, WebMessage.Query);
    }

    protected ResponseData success(Enum<?> status, Object... arguments) {
        return success(null, status, arguments);
    }

    protected ResponseData success(Object data, Enum<?> status, Object... arguments) {
        return result(true, data, status, arguments);
    }

    protected ResponseData fail() {
        return fail(null);
    }

    protected ResponseData fail(Object data) {
        return fail(data, null);
    }

    protected ResponseData fail(Enum<?> status, Object... arguments) {
        return fail(null, status, arguments);
    }

    protected ResponseData failException(BaseException ex) {
        return fail(null, ex.getKey(), ex.getArgs());
    }

    protected ResponseData fail(Object data, Enum<?> status, Object... arguments) {
        return result(false, data, status, arguments);
    }
}
