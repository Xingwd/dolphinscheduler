/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dolphinscheduler.plugin.alert.wecom;

public final class WeComParamsConstants {
    static final String WECOM_WEB_HOOK = "$t('webhook')";
    static final String NAME_WECOM_WEB_HOOK = "WebHook";

    static final String WECOM_MSG_TYPE = "$t('msgType')";
    static final String NAME_WECOM_MSG_TYPE = "MsgType";

    static final String WECOM_MSG_TYPE_TEXT = "text";
    static final String WECOM_MSG_TYPE_MARKDOWN = "markdown";

    static final String WECOM_AT_MOBILES = "$t('atMobiles')";
    static final String NAME_WECOM_AT_MOBILES = "AtMobiles";

    static final String WECOM_AT_USERIDS = "$t('atUserIds')";
    static final String NAME_WECOM_AT_USERIDS = "AtUserIds";

    private WeComParamsConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
