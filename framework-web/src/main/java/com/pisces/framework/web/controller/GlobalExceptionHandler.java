package com.pisces.framework.web.controller;

import com.pisces.framework.core.locale.LanguageService;
import com.pisces.framework.core.locale.LocaleManager;
import com.pisces.framework.web.config.WebMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常处理程序
 * 在请求到达controller之前的参数错误，请求方式错误，数据格式不一致问题都可以拦截到
 *
 * @author jason
 * @date 2022/12/08
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private LanguageService language;

//    @Override
//    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
//                                                             HttpStatus status, WebRequest request) {
//        //这里将异常直接传给handlerException()方法进行处理，返回值为OK保证友好的返回，而不是出现500错误码。
//        return new ResponseEntity<>(ex.getMessage(), status);
//    }

    @ExceptionHandler({Exception.class})
    public ResponseData jsonHandler(HttpServletRequest request, Exception ex) throws Exception {
        ex.printStackTrace();
        ResponseData r = new ResponseData();
        Enum<?> message = WebMessage.System;

        r.setSuccess(false);
        r.setStatus(message.ordinal());
        r.setName(message.name());
        r.setMessage(LocaleManager.getLanguage(message));
        r.setException(ex instanceof NullPointerException ? LocaleManager.getLanguage(WebMessage.SystemError) : ex.getMessage());
        return r;
    }

//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
//                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
//        final Enum<?> message = WebMessage.INVALIDATION_ENTITY_PROPERTY;
//        ResponseData r = new ResponseData();
//        r.setSuccess(false);
//        r.setStatus(message.ordinal());
//        r.setName(message.name());
//        r.setMessage(LocaleManager.getLanguage(message));
//        r.setData(getErrorInfos(ex));
//        return new ResponseEntity<>(r, headers, status);
//    }

//    /**
//     * 获取验证错误提示消息
//     *
//     * @param ex 前女友
//     * @return {@link List}<{@link ErrorInfo}>
//     */
//    private List<ErrorInfo> getErrorInfos(MethodArgumentNotValidException ex) {
//        List<ErrorInfo> errorInfoList = new ArrayList<>();
//        BindingResult bindingResult = ex.getBindingResult();
//        for (ObjectError error : bindingResult.getAllErrors()) {
//            ErrorInfo info = new ErrorInfo();
//            info.setEntityClass(error.getObjectName());
//            if (error.getDefaultMessage() != null && error.getDefaultMessage().startsWith("{") &&
//                    error.getDefaultMessage().endsWith("}")) {
//                info.setMessage(language.get(error.getDefaultMessage().substring(1, error.getDefaultMessage().length() - 1)));
//            } else {
//                info.setMessage(error.getDefaultMessage());
//            }
//            if (error instanceof FieldError) {
//                FieldError fieldError = (FieldError) error;
//                info.setProperty(fieldError.getField());
//                if (fieldError.getRejectedValue() != null) {
//                    info.setValue(fieldError.getRejectedValue().toString());
//                }
//            }
//
//            Object source = getSource(error);
//            if (source instanceof ConstraintViolationImpl) {
//                ConstraintViolationImpl<?> impl = (ConstraintViolationImpl<?>) source;
//                Object rootValue = error instanceof FieldError ? impl.getRootBean() : impl.getInvalidValue();
//                if (rootValue != null) {
//                    info.setEntityClass(language.get(rootValue.getClass()));
//                    if (error instanceof FieldError) {
//                        info.setProperty(language.get(rootValue.getClass(), ((FieldError) error).getField()));
//                    }
//                    if (EntityObject.class.isAssignableFrom(impl.getRootBeanClass())) {
//                        info.setEntity((EntityObject) rootValue);
//                    } else if (info.getValue() == null) {
//                        info.setValue(rootValue.toString());
//                    }
//                }
//            }
//
//            errorInfoList.add(info);
//        }
//        return errorInfoList;
//    }

    private Object getSource(ObjectError error) {
        try {
            Field modifiersField = ObjectError.class.getDeclaredField("source");
            modifiersField.setAccessible(true);
            return modifiersField.get(error);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ignored) {
        }
        return null;
    }
}
