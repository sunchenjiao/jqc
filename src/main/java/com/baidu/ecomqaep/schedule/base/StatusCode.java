package com.baidu.ecomqaep.schedule.base;

public class StatusCode {
    // 2XX --成功
    // 4XX --客户端错误
    // 5XX --服务器错误

    // 200 运行正常
    // 406 数据错误(服务器不接受)
    // 500 系统异常
    // 403 拒绝服务
    public static int SUCCESS = 200; // 成功
    public static int REGECT = 403; // 拒绝服务
    public static int DATA_ERROR = 406; // 数据错误
    public static int EXCEPTION = 500; // 系统异常

}