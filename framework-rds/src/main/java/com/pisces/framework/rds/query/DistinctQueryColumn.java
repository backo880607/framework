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

import com.pisces.framework.core.utils.lang.CollectionUtils;
import com.pisces.framework.core.utils.lang.StringUtils;
import com.pisces.framework.rds.datasource.dialect.IDialect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DistinctQueryColumn extends QueryColumn {

    private final List<QueryColumn> queryColumns;

    public DistinctQueryColumn(QueryColumn... queryColumns) {
        this.queryColumns = new ArrayList<>(Arrays.asList(queryColumns));
    }

    @Override
    public String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        if (CollectionUtils.isEmpty(queryTables)) {
            return "";
        }
        return " DISTINCT " + StringUtils.join(queryColumns, ", ", queryColumn ->
                queryColumn.toSelectSql(queryTables, dialect));
    }
}
