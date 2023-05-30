package com.pisces.framework.nosql.controller;

import com.pisces.framework.nosql.bean.TestNosql;
import com.pisces.framework.nosql.config.NoSqlConstant;
import com.pisces.framework.nosql.service.TestNosqlService;
import com.pisces.framework.web.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户语言控制器
 *
 * @author jason
 * @date 2022/12/07
 */
@RestController
@RequestMapping(NoSqlConstant.IDENTIFY + "/TestNosql")
public class TestNosqlController extends BaseController<TestNosql, TestNosqlService> {
}
