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

import com.pisces.framework.core.query.QueryOrderBy;
import com.pisces.framework.core.query.QueryTable;
import com.pisces.framework.core.query.TableDef;
import com.pisces.framework.core.utils.lang.ColumnUtils;

import java.io.Serializable;

/**
 * 查询列，描述的是一张表的字段
 *
 * @author jason
 * @date 2023/06/25
 */
public abstract class QueryColumn implements Serializable {
    protected QueryTable table;
    protected String name;

    public QueryColumn(TableDef tableDef, String name) {
        ColumnUtils.keepColumnSafely(name);
        this.table = new QueryTable(tableDef.getTableName());
        this.name = name;
    }

    public QueryTable getTable() {
        return table;
    }

    public void setTable(QueryTable table) {
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    ////order by ////
    public QueryOrderBy asc() {
        return new QueryOrderBy(this);
    }

    public QueryOrderBy desc() {
        return new QueryOrderBy(this, "DESC");
    }
}
