package com.pisces.framework.language.service.impl;

import com.pisces.framework.core.service.BaseServiceImpl;
import com.pisces.framework.language.bean.UserLanguage;
import com.pisces.framework.language.dao.UserLanguageDao;
import com.pisces.framework.language.service.UserLanguageService;
import org.springframework.stereotype.Service;

/**
 * 用户语言服务impl
 *
 * @author jason
 * @date 2022/12/07
 */
@Service
public class UserLanguageServiceImpl extends BaseServiceImpl<UserLanguage, UserLanguageDao> implements UserLanguageService {
}
