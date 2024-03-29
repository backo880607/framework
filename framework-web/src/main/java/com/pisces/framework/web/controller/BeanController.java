package com.pisces.framework.web.controller;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.query.BeanQuery;
import com.pisces.framework.core.service.BeanService;
import com.pisces.framework.core.validator.group.InsertGroup;
import com.pisces.framework.core.validator.group.UpdateGroup;
import com.pisces.framework.web.annotation.ExceptionMessage;
import com.pisces.framework.web.config.WebMessage;
import com.pisces.framework.web.controller.request.PagerRequest;
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
public abstract class BeanController<T extends BeanObject, S extends BeanService<T>> extends BaseController {
    @Autowired
    private S service;

    protected S getService() {
        return this.service;
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

    @PostMapping(value = "listByPage")
    @ExceptionMessage(clazz = WebMessage.class, name = "Query")
    public ResponseData listByPage(@RequestBody PagerRequest request) {
        BeanQuery query = request.convert();
        this.service.list(query);
        return success(query, WebMessage.Query);
    }

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
