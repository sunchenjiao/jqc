package com.baidu.ecomqaep.schedule.util;

import java.nio.charset.Charset;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

/**
 * 
 * @author wangyuqing at baidu dot com
 * @date 2014-07-29
 * 
 */

public class ShortMessageUtil {
    private static String url = "your msg url";
    private static String username = "username";
    private static String password = "password";
    private static String businessCode = "businessCode in baidu";
    private static Integer priority = 3;
    private static String scheduleDate = "";
    private static String extId = "";
    private static String original = "";
    private static Integer transmitMode = 0;

    /**
     * 发送短信
     * 
     * @param content 发送内容
     * @param dest 手机号,可多个逗号分隔。
     * 
     */

    public static void send(String content, String dest) {
        if (dest == null || dest.isEmpty() || content == null || content.isEmpty()) {
            throw new IllegalArgumentException("发送短信失败，参数content，dest不能为空");
        }

        // 准备签名 ,请保证被md5的内容的编码为utf8
        String signature = DigestUtils.md5Hex(username + password + dest + content + businessCode + priority
                + scheduleDate + extId + original + transmitMode);
        post(content, dest, signature);
    }

    /**
     * 简单测试下，沒有对结果解析
     * 
     * @param content 发送内容
     * @param dest 手机号,可多个逗号,分隔
     * @param signature 请求签名,不需要再传密码了
     */
    private static void post(String content, String dest, String signature) {
        try {
            Content resp = Request
                    .Post(url)
                    .bodyForm(
                            Form.form().add("username", username)
                                    .add("businessCode", businessCode)
                                    .add("priority", String.valueOf(priority)).add("extId", extId)
                                    .add("scheduleDate", scheduleDate).add("original", original)
                                    .add("transmitMode", String.valueOf(transmitMode))
                                    .add("msgDest", dest)
                                    .add("msgContent", content).add("signature", signature).build(),
                            Charset.forName("UTF-8"))
                    .execute().returnContent();
            String respString = resp.asString();
            System.out.println(respString);
            // do others
        } catch (Exception e) {
            throw new RuntimeException("发送短信失败", e);
        }
    }

   
}
