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
import org.apache.dolphinscheduler.alert.api.AlertChannelFactory;
import org.apache.dolphinscheduler.spi.params.base.ParamsOptions;
import org.apache.dolphinscheduler.spi.params.base.PluginParams;
import org.apache.dolphinscheduler.spi.params.base.Validate;
import org.apache.dolphinscheduler.spi.params.input.InputParam;
import org.apache.dolphinscheduler.spi.params.radio.RadioParam;

import java.util.Arrays;
import java.util.List;

import com.google.auto.service.AutoService;

@AutoService(AlertChannelFactory.class)
public final class WeComAlertChannelFactory implements AlertChannelFactory {
    @Override
    public String name() {
        return "WeCom";
    }

    @Override
    public List<PluginParams> params() {
        InputParam webHookParam = InputParam
                .newBuilder(WeComParamsConstants.NAME_WECOM_WEB_HOOK, WeComParamsConstants.WECOM_WEB_HOOK)
                .addValidate(Validate.newBuilder()
                        .setRequired(true)
                        .build())
                .build();

        RadioParam msgTypeParam = RadioParam
                .newBuilder(WeComParamsConstants.NAME_WECOM_MSG_TYPE, WeComParamsConstants.WECOM_MSG_TYPE)
                .addParamsOptions(new ParamsOptions(WeComParamsConstants.WECOM_MSG_TYPE_MARKDOWN, WeComParamsConstants.WECOM_MSG_TYPE_MARKDOWN, false))
                .addParamsOptions(new ParamsOptions(WeComParamsConstants.WECOM_MSG_TYPE_TEXT, WeComParamsConstants.WECOM_MSG_TYPE_TEXT, false))
                .setValue(WeComParamsConstants.WECOM_MSG_TYPE_MARKDOWN)
                .addValidate(Validate.newBuilder()
                        .setRequired(false)
                        .build())
                .build();

        InputParam atMobilesParam = InputParam
                .newBuilder(WeComParamsConstants.NAME_WECOM_AT_MOBILES, WeComParamsConstants.WECOM_AT_MOBILES)
                .addValidate(Validate.newBuilder()
                        .setRequired(false)
                        .build())
                .build();
        InputParam atUserIdsParam = InputParam
                .newBuilder(WeComParamsConstants.NAME_WECOM_AT_USERIDS, WeComParamsConstants.WECOM_AT_USERIDS)
                .addValidate(Validate.newBuilder()
                        .setRequired(false)
                        .build())
                .build();

        return Arrays.asList(webHookParam, msgTypeParam, atMobilesParam, atUserIdsParam);
    }

    @Override
    public AlertChannel create() {
        return new WeComAlertChannel();
    }
}
