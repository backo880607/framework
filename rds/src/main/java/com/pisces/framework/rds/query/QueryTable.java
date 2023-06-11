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
package com.pisces.framework.rds.query;

import com.pisces.framework.rds.datasource.dialect.IDialect;
import com.pisces.framework.rds.utils.MsUtil;

import java.io.Serializable;
import java.util.Objects;

/**
 * 查询列，描述的是一张表的字段
 */
public class QueryTable implements Serializable {

    protected String name;
    protected String alias;

    public QueryTable() {
    }

    public QueryTable(String name) {
        this.name = name;
    }

    public QueryTable(String table, String alias) {
        this.name = table;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public QueryTable as(String alias) {
        this.alias = alias;
        return this;
    }

    boolean isSameTable(QueryTable table) {
        return table != null && Objects.equals(name, table.name);
    }

    Object[] getValueArray() {
        return new Object[0];
    }

    public String toSql(IDialect dialect) {
        return dialect.wrap(name) + MsUtil.buildAsAlias(dialect.wrap(alias));
    }

    @Override
    public String toString() {
        return "QueryTable{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }
}
