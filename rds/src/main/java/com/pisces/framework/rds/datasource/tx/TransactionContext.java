/*
 * Copyright © 2018 organization baomidou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pisces.framework.rds.datasource.tx;

import com.pisces.framework.core.utils.lang.StringUtils;

/**
 * @author funkye
 */
public class TransactionContext {

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * Gets xid.
     *
     * @return the xid
     */
    public static String getXID() {
        String xid = CONTEXT_HOLDER.get();
        if (!StringUtils.isEmpty(xid)) {
            return xid;
        }
        return null;
    }

    /**
     * Unbind string.
     */
    public static void unbind() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * bind string.
     *
     * @return the string
     */
    public static String bind(String xid) {
        CONTEXT_HOLDER.set(xid);
        return xid;
    }

    /**
     * remove
     */
    public static void remove() {
        CONTEXT_HOLDER.remove();
    }

}
