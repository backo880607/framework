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
package com.pisces.framework.core.query.column;

import com.pisces.framework.core.entity.Property;
import com.pisces.framework.core.enums.VALUE_SORT_TYPE;
import com.pisces.framework.core.query.QueryTable;
import com.pisces.framework.core.query.condition.QueryOrderBy;
import com.pisces.framework.core.utils.lang.ColumnUtils;
import com.pisces.framework.core.utils.lang.ObjectUtils;

import java.io.Serializable;

/**
 * 查询列，描述的是一张表的字段
 *
 * @author jason
 * @date 2023/06/25
 */
public class QueryColumn implements Serializable {
    private String beanName;
    private volatile QueryTable table;

    private String name;

    public QueryColumn() {
    }

    public QueryColumn(String beanName, String fieldName) {
        ColumnUtils.keepColumnSafely(fieldName);
        this.beanName = beanName;
        this.name = fieldName;
    }

    public QueryTable getTable() {
        if (table == null) {
            synchronized (this) {
                if (table == null) {
                    table = new QueryTable(ObjectUtils.fetchBeanClass(beanName));
                }
            }
        }
        return table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    ////order by ////
    public QueryOrderBy asc() {
        return new QueryOrderBy(this, VALUE_SORT_TYPE.ASC);
    }

    public QueryOrderBy desc() {
        return new QueryOrderBy(this, VALUE_SORT_TYPE.DESC);
    }

    public static QueryColumn getQueryColumn(Property property) {
        if (property == null) {
            return null;
        }

        QueryColumn column = null;
        switch (property.getType()) {
            case NONE -> {
            }
            case BOOLEAN -> column = new BooleanQueryColumn(property.getBelongName(), property.getPropertyCode());
            case CHAR -> column = new CharQueryColumn(property.getBelongName(), property.getPropertyCode());
            case SHORT -> column = new ShortQueryColumn(property.getBelongName(), property.getPropertyCode());
            case INTEGER -> column = new IntegerQueryColumn(property.getBelongName(), property.getPropertyCode());
            case LONG -> column = new LongQueryColumn(property.getBelongName(), property.getPropertyCode());
            case DOUBLE -> column = new DoubleQueryColumn(property.getBelongName(), property.getPropertyCode());
            case DATE -> column = new DateQueryColumn(property.getBelongName(), property.getPropertyCode());
            case TIME -> column = new TimeQueryColumn(property.getBelongName(), property.getPropertyCode());
            case DATE_TIME -> column = new DateTimeQueryColumn(property.getBelongName(), property.getPropertyCode());
            case DURATION -> column = new DurationQueryColumn(property.getBelongName(), property.getPropertyCode());
            case ENUM -> column = new EnumQueryColumn(property.getBelongName(), property.getPropertyCode());
            case MULTI_ENUM -> column = new MultiEnumQueryColumn(property.getBelongName(), property.getPropertyCode());
            case STRING -> column = new StringQueryColumn(property.getBelongName(), property.getPropertyCode());
            case BEAN -> column = new BeanQueryColumn(property.getBelongName(), property.getPropertyCode());
            case LIST -> column = new ListQueryColumn(property.getBelongName(), property.getPropertyCode());
        }
        return column;
    }
}
