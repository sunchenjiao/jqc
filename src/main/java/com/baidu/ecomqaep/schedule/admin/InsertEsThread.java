package com.baidu.ecomqaep.schedule.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.ecomqaep.schedule.log.LogFormat;
import com.baidu.ecomqaep.schedule.log.LogFormat.LogLevel;
import com.baidu.ecomqaep.schedule.config.Constants;
import com.baidu.ecomqaep.schedule.manager.AdminManager;

public class InsertEsThread extends Thread {

    private static Log log = LogFactory.getLog(InsertEsThread.class);

    private static final InsertEsThread INSTANCE = new InsertEsThread();
    AdminManager adminManager = AdminManager.getInstance();

    private InsertEsThread() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static InsertEsThread getInstance() {
        return InsertEsThread.INSTANCE;

    }

    public void run() {

        while (true) {
            try {
                log.info("insertJobToEs!");
                LogFormat.formatLog(log, LogLevel.INFO, null, null, "run",
                        null, null, "start", null, null, "checkTimeOutJob");
                adminManager.insertJobToEs();
                Thread.sleep(Constants.ADMINTHREAD_INTERVAL);

            } catch (Exception e) {
                log.error(e.getMessage(), e);

            }

        }
    }

}
