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
import org.apache.dolphinscheduler.common.utils.JSONUtils;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WeComSender {

    private static final Logger logger = LoggerFactory.getLogger(WeComSender.class);
    private final String url;
    private String msgType;

    private final String atMobiles;
    private final String atUserIds;

    WeComSender(Map<String, String> config) {
        url = config.get(WeComParamsConstants.NAME_WECOM_WEB_HOOK);
        msgType = config.get(WeComParamsConstants.NAME_WECOM_MSG_TYPE);

        atMobiles = config.get(WeComParamsConstants.NAME_WECOM_AT_MOBILES);
        atUserIds = config.get(WeComParamsConstants.NAME_WECOM_AT_USERIDS);
    }

    private static HttpPost constructHttpPost(String url, String msg) {
        HttpPost post = new HttpPost(url);
        StringEntity entity = new StringEntity(msg, StandardCharsets.UTF_8);
        post.setEntity(entity);
        post.addHeader("Content-Type", "application/json; charset=utf-8");
        return post;
    }

    private static CloseableHttpClient getDefaultClient() {
        return HttpClients.createDefault();
    }

    private AlertResult checkSendWeComSendMsgResult(String result) {
        AlertResult alertResult = new AlertResult();
        alertResult.setStatus("false");

        if (null == result) {
            alertResult.setMessage("send wecom msg error");
            logger.info("send wecom msg error,wecom server resp is null");
            return alertResult;
        }
        WeComSendMsgResponse sendMsgResponse = JSONUtils.parseObject(result, WeComSendMsgResponse.class);
        if (null == sendMsgResponse) {
            alertResult.setMessage("send wecom msg fail");
            logger.info("send wecom msg error,resp error");
            return alertResult;
        }
        if (sendMsgResponse.errcode == 0) {
            alertResult.setStatus("true");
            alertResult.setMessage("send wecom msg success");
            return alertResult;
        }
        alertResult.setMessage(String.format("alert send wecom msg error : %s", sendMsgResponse.getErrmsg()));
        logger.info("alert send wecom msg error : {}", sendMsgResponse.getErrmsg());
        return alertResult;
    }

    /**
     * send WeCom msg handler
     *
     * @param title title
     * @param content content
     * @return
     */
    public AlertResult sendWeComMsg(String title, String content) {
        AlertResult alertResult;
        try {
            String resp = sendMsg(title, content);
            return checkSendWeComSendMsgResult(resp);
        } catch (Exception e) {
            logger.info("send wecom alert msg  exception : {}", e.getMessage());
            alertResult = new AlertResult();
            alertResult.setStatus("false");
            alertResult.setMessage("send wecom alert fail.");
        }
        return alertResult;
    }

    private String sendMsg(String title, String content) throws IOException {

        String msg = generateMsgJson(title, content);

        HttpPost httpPost = constructHttpPost(url, msg);

        CloseableHttpClient httpClient = getDefaultClient();

        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String resp;
            try {
                HttpEntity entity = response.getEntity();
                resp = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
            logger.info("wecom send msg :{}, resp: {}", msg, resp);
            return resp;
        } finally {
            httpClient.close();
        }
    }

    /**
     * generate msg json
     *
     * @param title title
     * @param content content
     * @return msg
     */
    private String generateMsgJson(String title, String content) {
        if (org.apache.commons.lang3.StringUtils.isBlank(msgType)) {
            msgType = WeComParamsConstants.WECOM_MSG_TYPE_TEXT;
        }
        Map<String, Object> items = new HashMap<>();
        items.put("msgtype", msgType);
        Map<String, Object> text = new HashMap<>();
        items.put(msgType, text);

        if (WeComParamsConstants.WECOM_MSG_TYPE_MARKDOWN.equals(msgType)) {
            generateMarkdownMsg(title, content, text);
        } else {
            generateTextMsg(title, content, text);
        }

        return JSONUtils.toJsonString(items);

    }

    /**
     * generate text msg
     *
     * @param title title
     * @param content content
     * @param text text
     */
    private void generateTextMsg(String title, String content, Map<String, Object> text) {
        StringBuilder builder = new StringBuilder("[DolphinSchedulerAlert] ");
        builder.append(title);
        builder.append("\n");
        builder.append(formatJson(content));
        byte[] byt = StringUtils.getBytesUtf8(builder.toString());
        String txt = StringUtils.newStringUtf8(byt);
        text.put("content", formatJson(txt));

        String[] atMobileArray =
                org.apache.commons.lang3.StringUtils.isNotBlank(atMobiles) ? atMobiles.split(",")
                        : new String[0];
        String[] atUserArray =
                org.apache.commons.lang3.StringUtils.isNotBlank(atUserIds) ? atUserIds.split(",")
                        : new String[0];
        text.put("mentioned_mobile_list", atMobileArray);
        text.put("mentioned_list", atUserArray);
    }

    /**
     * generate markdown msg
     *
     * @param title title
     * @param content content
     * @param text text
     */
    private void generateMarkdownMsg(String title, String content, Map<String, Object> text) {
        StringBuilder builder = new StringBuilder("**[DolphinSchedulerAlert]** ");
        builder.append(title);
        builder.append("\n><font color=\"comment\">");
        builder.append(formatJson(content));
        builder.append("</font>\n\n");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(atUserIds)) {
            Arrays.stream(atUserIds.split(",")).forEach(value -> {
                builder.append("<@");
                builder.append(value);
                builder.append("> ");
            });
        }

        byte[] byt = StringUtils.getBytesUtf8(builder.toString());
        String txt = StringUtils.newStringUtf8(byt);
        text.put("content", txt);
    }

    private String formatJson(String jsonString) {
        if (JSONUtils.checkJsonValid(jsonString)) {
            ArrayNode objectNode = JSONUtils.parseArray(jsonString);
            return JSONUtils.toPrettyJsonString(objectNode);
        } else {
            return jsonString;
        }
    }

    static final class WeComSendMsgResponse {
        private Integer errcode;
        private String errmsg;

        public WeComSendMsgResponse() {
        }

        public Integer getErrcode() {
            return this.errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return this.errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof WeComSendMsgResponse)) {
                return false;
            }
            final WeComSendMsgResponse other = (WeComSendMsgResponse) o;
            final Object this$errcode = this.getErrcode();
            final Object other$errcode = other.getErrcode();
            if (this$errcode == null ? other$errcode != null : !this$errcode.equals(other$errcode)) {
                return false;
            }
            final Object this$errmsg = this.getErrmsg();
            final Object other$errmsg = other.getErrmsg();
            if (this$errmsg == null ? other$errmsg != null : !this$errmsg.equals(other$errmsg)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $errcode = this.getErrcode();
            result = result * PRIME + ($errcode == null ? 43 : $errcode.hashCode());
            final Object $errmsg = this.getErrmsg();
            result = result * PRIME + ($errmsg == null ? 43 : $errmsg.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return "WeComSender.WeComSendMsgResponse(errcode=" + this.getErrcode() + ", errmsg=" + this.getErrmsg() + ")";
        }
    }
}
