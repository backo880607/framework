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

import com.pisces.framework.core.entity.BeanObject;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 查询列，描述的是一张表的字段
 *
 * @author jason
 * @date 2023/06/25
 */
@Getter
@Setter
public class QueryTable implements Serializable {
    protected Class<? extends BeanObject> beanClass;

    public QueryTable(Class<? extends BeanObject> beanClass) {
        this.beanClass = beanClass;
    }

    public boolean isSame(QueryTable table) {
        return table != null && beanClass == table.beanClass;
    }

    public Object[] getValueArray() {
        return new Object[0];
    }
}
