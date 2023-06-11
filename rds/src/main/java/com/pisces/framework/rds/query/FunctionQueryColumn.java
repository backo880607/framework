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

import com.pisces.framework.core.utils.lang.StringUtils;
import com.pisces.framework.rds.datasource.dialect.IDialect;
import com.pisces.framework.rds.utils.SqlUtil;

import java.util.List;

/**
 * 数据库 聚合函数，例如 count(id) ，max(account.age) 等等
 */
public class FunctionQueryColumn extends QueryColumn {

    protected String fnName;
    protected QueryColumn column;

    public FunctionQueryColumn(String fnName, String column) {
        SqlUtil.keepColumnSafely(fnName);
        SqlUtil.keepColumnSafely(column);
        this.fnName = fnName;
        this.column = new QueryColumn(column);
    }

    public FunctionQueryColumn(String fnName, QueryColumn column) {
        SqlUtil.keepColumnSafely(fnName);
        this.fnName = fnName;
        this.column = column;
    }

    public String getFnName() {
        return fnName;
    }

    public void setFnName(String fnName) {
        this.fnName = fnName;
    }

    public QueryColumn getColumn() {
        return column;
    }

    public void setColumn(QueryColumn column) {
        this.column = column;
    }

    @Override
    public String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        String sql = column.toSelectSql(queryTables, dialect);
        return StringUtils.isBlank(sql) ? "" : fnName + "(" + sql + ")" + WrapperUtil.buildAsAlias(alias);
    }

    @Override
    public QueryColumn as(String alias) {
        SqlUtil.keepColumnSafely(alias);
        this.alias = alias;
        return this;
    }

    @Override
    public String toString() {
        return "FunctionQueryColumn{" +
                "fnName='" + fnName + '\'' +
                ", column=" + column +
                '}';
    }
}