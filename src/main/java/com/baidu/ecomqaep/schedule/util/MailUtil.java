package com.baidu.ecomqaep.schedule.util;

import com.baidu.ecomqaep.schedule.config.Config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 邮件发送公共类
 * 
 * @author modi
 * @version 1.0.0
 */
public class MailUtil {
    protected static Log logger = LogFactory.getLog(MailUtil.class);

    // private static JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    // static {
    // mailSender.setHost(Variables.MAIL_HOST);
    // }
    static JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    static {
        mailSender.setHost(Config.MailHost);
        Properties prop = new Properties();
        prop.setProperty("mail.smtp.auth", "false");
        mailSender.setJavaMailProperties(prop);
    }

    /**
     * 发送html邮件,带附件
     */
    public static void sendHtmlAttachMail(String from, String[] to,
            String[] cc, String title, String text, Integer priority,
            Map<String, String> filesMap) {
        try {
            long start = System.currentTimeMillis();
            if (from == null || to == null)
                throw new Exception("from is null or to is null");

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(
                    mimeMessage, true, "UTF-8");

            InternetAddress[] toArray = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++) {
                toArray[i] = new InternetAddress(to[i]);
            }
            messageHelper.setFrom(new InternetAddress(from));
            messageHelper.setTo(toArray);
            messageHelper.setSubject(title);
            messageHelper.setText(text, true);
            if (cc != null && cc.length > 0) {
                InternetAddress[] ccArray = new InternetAddress[cc.length];
                for (int i = 0; i < cc.length; i++) {
                    ccArray[i] = new InternetAddress(cc[i]);
                }
                messageHelper.setCc(ccArray);
            }
            // 加入附件列表
            if (filesMap != null && filesMap.size() > 0) {
                Set<String> keys = filesMap.keySet();
                Iterator<String> keyIt = keys.iterator();
                String fileName = "";
                String filePath = "";
                while (keyIt.hasNext()) {
                    fileName = keyIt.next();
                    filePath = (String) filesMap.get(fileName);
                    File theFile = new File(filePath);
                    if (theFile != null
                            && theFile.length() < Config.MAX_MAILFILE_SIZE) {
                        messageHelper
                                .addAttachment(MimeUtility.encodeWord(fileName,
                                        "UTF-8", null), theFile);
                    }
                }
            }
            mimeMessage = messageHelper.getMimeMessage();
            if (priority != null) {
                mimeMessage.addHeader("X-Priority", priority.toString());
            }
            mailSender.send(mimeMessage);
            long end = System.currentTimeMillis();
            logger.info("send mail start:" + start + " end :" + end);
        } catch (Exception e) {
            logger.error("send mail failed", e);
            // throw new Exception("send Mail Failed", e);
        }
    }

   
}
