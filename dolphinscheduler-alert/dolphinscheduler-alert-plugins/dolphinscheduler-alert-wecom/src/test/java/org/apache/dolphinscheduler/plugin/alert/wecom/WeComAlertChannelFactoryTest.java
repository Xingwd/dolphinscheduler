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

import org.apache.dolphinscheduler.alert.api.AlertChannel;
import org.apache.dolphinscheduler.common.utils.JSONUtils;
import org.apache.dolphinscheduler.spi.params.base.PluginParams;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class WeComAlertChannelFactoryTest {
    @Test
    public void testGetParams() {
        WeComAlertChannelFactory weComAlertChannelFactory = new WeComAlertChannelFactory();
        List<PluginParams> params = weComAlertChannelFactory.params();
        JSONUtils.toJsonString(params);
        Assert.assertEquals(4, params.size());
    }

    @Test
    public void testCreate() {
        WeComAlertChannelFactory weComAlertChannelFactory = new WeComAlertChannelFactory();
        AlertChannel alertChannel = weComAlertChannelFactory.create();
        Assert.assertNotNull(alertChannel);
    }
}
