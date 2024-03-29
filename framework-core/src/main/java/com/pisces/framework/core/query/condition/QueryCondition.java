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
package com.pisces.framework.core.query.condition;

import com.pisces.framework.core.enums.CONDITION_TYPE;
import com.pisces.framework.core.query.column.QueryColumn;
import lombok.Getter;

import java.io.Serializable;

/**
 * 查询条件
 *
 * @author jason
 * @date 2023/06/25
 */
@Getter
public class QueryCondition implements Serializable {
    private QueryColumn column;
    private CONDITION_TYPE type;
    private Object value;
    private boolean effective = true;

    //当前条件的上个条件
    private QueryCondition before;
    //当前条件的下个条件
    private QueryCondition next;
    //两个条件直接的连接符
    private QueryConnector connector;

    public static QueryCondition createEmpty() {
        return new QueryCondition().when(false);
    }

    public static QueryCondition create(QueryColumn queryColumn, Object value) {
        return create(queryColumn, CONDITION_TYPE.EQUAL, value);
    }

    public static QueryCondition create(QueryColumn queryColumn, CONDITION_TYPE type, Object value) {
        QueryCondition condition = new QueryCondition();
        condition.setColumn(queryColumn);
        condition.setType(type);
        condition.setValue(value);
        return condition;
    }

    public QueryCondition() {
    }

    public void setColumn(QueryColumn column) {
        this.column = column;
    }

    public void setType(CONDITION_TYPE type) {
        this.type = type;
    }

    public Object getValue() {
        return checkEffective() ? value : null;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public QueryCondition when(boolean effective) {
        this.effective = effective;
        return this;
    }

    public boolean checkEffective() {
        return effective;
    }

    public QueryCondition and(QueryCondition nextCondition) {
        return new Brackets(this).and(nextCondition);
    }

    public QueryCondition or(QueryCondition nextCondition) {
        return new Brackets(this).or(nextCondition);
    }

    public void connect(QueryCondition nextCondition, QueryConnector connector) {
        if (this.next != null) {
            this.next.connect(nextCondition, connector);
        } else {
            this.next = nextCondition;
            this.connector = connector;
            nextCondition.before = this;
        }
    }

    public QueryCondition getEffectiveBefore() {
        if (before == null) {
            return null;
        }
        return before.checkEffective() ? before : before.getEffectiveBefore();
    }
}
