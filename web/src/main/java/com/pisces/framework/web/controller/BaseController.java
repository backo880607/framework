package com.pisces.framework.web.controller;

import com.pisces.framework.core.entity.BaseObject;
import com.pisces.framework.core.exception.BaseException;
import com.pisces.framework.core.locale.LanguageService;
import com.pisces.framework.core.locale.LocaleManager;
import com.pisces.framework.core.locale.Message;
import com.pisces.framework.core.service.BaseService;
import com.pisces.framework.core.validator.group.InsertGroup;
import com.pisces.framework.core.validator.group.UpdateGroup;
import com.pisces.framework.web.annotation.ExceptionMessage;
import com.pisces.framework.web.config.WebMessage;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 基本控制器
 *
 * @author jason
 * @date 2022/12/08
 */
public abstract class BaseController<T extends BaseObject, S extends BaseService<T>> {
    protected Message LOG = new Message(this.getClass());
    @Autowired
    protected LanguageService lang;
    @Autowired
    private S service;

    protected S getService() {
        return this.service;
    }

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

    @GetMapping(value = "create")
    @ExceptionMessage(clazz = WebMessage.class, name = "Create")
    public ResponseData create() {
        return success(this.service.create(), WebMessage.Create);
    }

    @GetMapping("get")
    @ExceptionMessage(clazz = WebMessage.class, name = "Query")
    public ResponseData get() {
        return success(this.service.get(), WebMessage.Query);
    }

    /**
     * ExceptionMessage注解是处理异常时，返回给前端的状态码。
     * fail函数可以自定义返回的状态码
     *
     * @return 返回请求的数据，成功需返回给前端状态码
     */
    @GetMapping("getById")
    @ExceptionMessage(clazz = WebMessage.class, name = "Query")
    public ResponseData getById(@RequestParam long id) {
        final T item = this.service.getById(id);
        return item != null ? success(item, WebMessage.Query) : fail(id, WebMessage.NOT_EXISTED);
    }

    @GetMapping("list")
    @ExceptionMessage(clazz = WebMessage.class, name = "Query")
    public ResponseData list() {
        return success(this.service.list(), WebMessage.Query);
    }

    @GetMapping("listByIds")
    @ExceptionMessage(clazz = WebMessage.class, name = "Query")
    public ResponseData listByIds(@RequestBody List<Long> ids) {
        return success(this.service.listByIds(ids), WebMessage.Query);
    }

//    @GetMapping(value = "getByPage")
//    @ExceptionMessage(clazz = WebMessage.class, name = "Query")
//    public ResponseData getByPage(@RequestParam int pageNum, @RequestParam int pageSize,
//                                  @RequestParam String orderBy, @RequestParam String filter) {
//        PageParam param = new PageParam();
//        param.setPageNum(pageNum);
//        param.setPageSize(pageSize);
//        param.setOrderBy(orderBy);
//        param.setFilter(filter);
//        return success(this.service.get(param), WebMessage.Query);
//    }

    @PostMapping("insert")
    @ExceptionMessage(clazz = WebMessage.class, name = "Insert")
    public ResponseData insert(@RequestBody @Validated({InsertGroup.class, Default.class}) T item) {
        this.service.insert(item);
        return success(item, WebMessage.Insert);
    }

    @PostMapping("insertBatch")
    @ExceptionMessage(clazz = WebMessage.class, name = "Insert")
    public ResponseData insertBatch(@RequestBody @Validated({InsertGroup.class, Default.class}) List<T> items) {
        this.service.insertBatch(items);
        return success(items, WebMessage.Insert);
    }

    @PutMapping("update")
    @ExceptionMessage(clazz = WebMessage.class, name = "Edit")
    public ResponseData update(@RequestBody @Validated({UpdateGroup.class, Default.class}) T item) {
        final int value = this.service.update(item);
        return value > 0 ? success(item.getId(), WebMessage.Edit) : fail(item.getId(), WebMessage.NOT_EXISTED);
    }

    @PutMapping("updateBatch")
    @ExceptionMessage(clazz = WebMessage.class, name = "Edit")
    public ResponseData updateBatch(@RequestBody @Validated({UpdateGroup.class, Default.class}) List<T> items) {
        return success(WebMessage.Edit, this.service.updateBatch(items));
    }

    @DeleteMapping("delete")
    @ExceptionMessage(clazz = WebMessage.class, name = "Delete")
    public ResponseData delete(@RequestBody T item) {
        final int value = this.service.delete(item);
        return value > 0 ? success(WebMessage.Delete) : fail(item.getId(), WebMessage.NOT_EXISTED);
    }

    @DeleteMapping("deleteBatch")
    @ExceptionMessage(clazz = WebMessage.class, name = "Delete")
    public ResponseData deleteBatch(@RequestBody List<T> items) {
        return success(this.service.deleteBatch(items), WebMessage.Delete);
    }

    @DeleteMapping("deleteById")
    @ExceptionMessage(clazz = WebMessage.class, name = "Delete")
    public ResponseData deleteById(@RequestBody Long id) {
        final int value = this.service.deleteById(id);
        return value > 0 ? success(id, WebMessage.Delete) : fail(id, WebMessage.NOT_EXISTED);
    }

    @DeleteMapping("deleteIdBatch")
    @ExceptionMessage(clazz = WebMessage.class, name = "Delete")
    public ResponseData deleteIdBatch(@RequestBody List<Long> ids) {
        return success(this.service.deleteIdBatch(ids), WebMessage.Delete);
    }
}
