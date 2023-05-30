package com.pisces.framework.language.controller;

import com.pisces.framework.language.bean.UserLanguage;
import com.pisces.framework.language.config.LanguageConstant;
import com.pisces.framework.language.service.UserLanguageService;
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
@RequestMapping(LanguageConstant.IDENTIFY + "/UserLanguage")
public class UserLanguageController extends BaseController<UserLanguage, UserLanguageService> {
}
