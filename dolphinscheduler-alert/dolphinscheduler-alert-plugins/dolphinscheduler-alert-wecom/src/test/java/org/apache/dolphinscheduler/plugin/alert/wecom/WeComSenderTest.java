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

import org.apache.dolphinscheduler.alert.api.AlertResult;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WeComSenderTest {

    private static final Map<String, String> weComConfig = new HashMap<>();

    @Before
    public void initWeComConfig() {
        weComConfig.put(WeComParamsConstants.NAME_WECOM_WEB_HOOK, "url");
    }

    public void testSend() {
        WeComSender weComSender = new WeComSender(weComConfig);
        weComSender.sendWeComMsg("Welcome", "UTF-8");
        weComSender = new WeComSender(weComConfig);
        AlertResult alertResult = weComSender.sendWeComMsg("title", "content test");
        Assert.assertEquals("false", alertResult.getStatus());
        weComSender = new WeComSender(weComConfig);
        alertResult = weComSender.sendWeComMsg("title", "{\"content\": \"test\"}");
        Assert.assertEquals("false", alertResult.getStatus());
        weComSender = new WeComSender(weComConfig);
        alertResult = weComSender.sendWeComMsg("title", "[{\"content1\": \"test1\"}, {\"content2\": \"test2\"}]");
        Assert.assertEquals("false", alertResult.getStatus());
    }

    @Test
    public void testSendMarkdown() {
        weComConfig.put(WeComParamsConstants.NAME_WECOM_MSG_TYPE, WeComParamsConstants.WECOM_MSG_TYPE_MARKDOWN);
        testSend();
    }

    @Test
    public void testSendText() {
        weComConfig.put(WeComParamsConstants.NAME_WECOM_MSG_TYPE, WeComParamsConstants.WECOM_MSG_TYPE_TEXT);
        testSend();
    }

}
