/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pisces.framework.core.query;

import com.pisces.framework.core.query.condition.QueryCondition;
import com.pisces.framework.core.query.condition.QueryConnector;

/**
 * 括号
 *
 * @author jason
 * @date 2023/06/25
 */
public class Brackets extends QueryCondition {
    private final QueryCondition childCondition;

    public Brackets(QueryCondition childCondition) {
        this.childCondition = childCondition;
    }

    @Override
    public QueryCondition and(QueryCondition nextCondition) {
        connectToChild(nextCondition, QueryConnector.AND);
        return this;
    }

    @Override
    public QueryCondition or(QueryCondition nextCondition) {
        connectToChild(nextCondition, QueryConnector.OR);
        return this;
    }

    protected void connectToChild(QueryCondition nextCondition, QueryConnector connector) {
        childCondition.connect(nextCondition, connector);
    }

//    @Override
//    public Object getValue() {
//        return checkEffective() ? WrapperUtil.getValues(childCondition) : null;
//    }

    @Override
    public boolean checkEffective() {
        boolean effective = super.checkEffective();
        if (!effective) {
            return false;
        }
        QueryCondition condition = this.childCondition;
        while (condition != null) {
            if (condition.checkEffective()) {
                return true;
            }
            condition = condition.getNext();
        }
        return false;
    }
}
