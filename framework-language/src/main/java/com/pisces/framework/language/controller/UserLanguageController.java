package com.pisces.framework.language.controller;

import com.pisces.framework.language.bean.UserLanguage;
import com.pisces.framework.language.bean.table.QUserLanguage;
import com.pisces.framework.language.config.LanguageConstant;
import com.pisces.framework.language.service.UserLanguageService;
import com.pisces.framework.web.annotation.ExceptionMessage;
import com.pisces.framework.web.config.WebMessage;
import com.pisces.framework.web.controller.BeanController;
import com.pisces.framework.web.controller.ResponseData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户语言控制器
 *
 * @author jason
 * @date 2022/12/07
 */
@RestController
@RequestMapping(LanguageConstant.IDENTIFY + "/UserLanguage")
public class UserLanguageController extends BeanController<UserLanguage, UserLanguageService> {

    @GetMapping("listByDataset")
    @ExceptionMessage(clazz = WebMessage.class, name = "Query")
    public ResponseData listByDataset(Long datasetId) {
        UserLanguage language = getService().get(QUserLanguage.id.equal(1665763576129785856L));
        return success("", WebMessage.Query);
    }
}
