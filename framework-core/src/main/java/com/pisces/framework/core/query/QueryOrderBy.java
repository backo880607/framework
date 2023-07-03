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

import com.pisces.framework.core.enums.VALUE_SORT_TYYPE;
import com.pisces.framework.core.query.column.QueryColumn;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 排序字段
 *
 * @author jason
 * @date 2023/06/27
 */
@Getter
@Setter
public class QueryOrderBy implements Serializable {

    private QueryColumn column;
    private VALUE_SORT_TYYPE type;

    public QueryOrderBy(QueryColumn column, VALUE_SORT_TYYPE type) {
        this.column = column;
        this.type = type;
    }
}
