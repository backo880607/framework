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

public class UnionWrapper {

    private String key;
    private QueryWrapper queryWrapper;

    public static UnionWrapper union(QueryWrapper queryWrapper) {
        UnionWrapper unionWrapper = new UnionWrapper();
        unionWrapper.key = " UNION ";
        unionWrapper.queryWrapper = queryWrapper;
        return unionWrapper;
    }

    public static UnionWrapper unionAll(QueryWrapper queryWrapper) {
        UnionWrapper unionWrapper = new UnionWrapper();
        unionWrapper.key = " UNION ALL ";
        unionWrapper.queryWrapper = queryWrapper;
        return unionWrapper;
    }


    private UnionWrapper() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public QueryWrapper getQueryWrapper() {
        return queryWrapper;
    }

    public void setQueryWrapper(QueryWrapper queryWrapper) {
        this.queryWrapper = queryWrapper;
    }

    public void buildSql(StringBuilder sqlBuilder, IDialect dialect) {
        sqlBuilder.append(key).append("(").append(dialect.buildSelectSql(queryWrapper)).append(")");
    }
}