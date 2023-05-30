package com.pisces.framework.nosql.service.impl;

import com.pisces.framework.core.service.BaseServiceImpl;
import com.pisces.framework.nosql.bean.TestNosql;
import com.pisces.framework.nosql.dao.TestNosqlDao;
import com.pisces.framework.nosql.service.TestNosqlService;
import org.springframework.stereotype.Service;

@Service
public class TestNosqlServiceImpl extends BaseServiceImpl<TestNosql, TestNosqlDao> implements TestNosqlService {
}
