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

import com.pisces.framework.core.query.QueryWrapper;

/**
 * 操作类型的操作
 * 示例1：and EXISTS (select 1 from ... where ....)
 * 示例2：and not EXISTS (select ... from ... where ....)
 */
public class OperatorSelectCondition extends QueryCondition {
    //操作符，例如 exist, not exist
    private final String operator;
    private final QueryWrapper queryWrapper;

    public OperatorSelectCondition(String operator, QueryWrapper queryWrapper) {
        this.operator = operator;
        this.queryWrapper = queryWrapper;
    }

    @Override
    public Object getValue() {
        return queryWrapper.getValueArray();
    }
}
