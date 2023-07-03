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

/**
 * sql连接器
 *
 * @author jason
 * @date 2023/06/25
 */
public enum QueryConnector {
    AND(" AND "),
    OR(" OR ");

    private final String value;

    QueryConnector(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
