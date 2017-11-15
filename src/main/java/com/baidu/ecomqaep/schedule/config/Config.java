package com.baidu.ecomqaep.schedule.config;

public class Config extends BaseConfiguration {

    // 测试任务的超时默认超时时间
    @ConfigInterface(defaultValue = "identity")
    public static String identity;

    // redis地址
    @ConfigInterface(defaultValue = "127.0.0.1")
    public static String redisHost;

    // redis端口
    @ConfigInterface(defaultValue = "8379")
    public static int redisPort;

    // redis密码
    @ConfigInterface(defaultValue = "null")
    public static String redisPass;

    // ElasticSearch集群名称
    @ConfigInterface(defaultValue = "es-name")
    public static String esName;

    // ElasticSearch ip
    @ConfigInterface(defaultValue = "127.0.0.1")
    public static String esHost;

    // ElasticSearch端口
    @ConfigInterface(defaultValue = "8333")
    public static String esPort;

    // 邮件发送服务器
    @ConfigInterface(defaultValue = "your mail address")
    public static String MailHost;

    // 发送邮件中附件的最大大小，2M
    @ConfigInterface(defaultValue = "2000000")
    public static long MAX_MAILFILE_SIZE;

    // 发送报警邮件中标题的最大长度，255
    @ConfigInterface(defaultValue = "255")
    public static int ALARM_MAIL_TITLE_LENGTH;

    // 监控平台发送报警邮件的发件人地址
    @ConfigInterface(defaultValue = "your email")
    public static String MailFrom;

    // 监控平台发送报警邮件的发件人地址
    @ConfigInterface(defaultValue = "your email")
    public static String MailTo;

    // server地址 用户配置
    @ConfigInterface(defaultValue = "127.0.0.1")
    public static String ServerHost;

    // server地址 用户配置
    @ConfigInterface(defaultValue = "Admin")
    public static String NODEROLE;

    static {
        autowareConfig(Config.class, "ep-schedule.conf");
    }
}
