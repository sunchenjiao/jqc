package com.baidu.ecomqaep.schedule.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.ecomqaep.schedule.log.LogFormat;
import com.baidu.ecomqaep.schedule.log.LogFormat.LogLevel;
import com.baidu.ecomqaep.schedule.config.Constants;
import com.baidu.ecomqaep.schedule.manager.AdminManager;

public class CheckJobThread extends Thread {

    private static Log log = LogFactory.getLog(CheckJobThread.class);

    private static final CheckJobThread INSTANCE = new CheckJobThread();
    AdminManager adminManager = AdminManager.getInstance();

    private CheckJobThread() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static CheckJobThread getInstance() {
        return CheckJobThread.INSTANCE;

    }

    public void run() {
        try {
            Thread.sleep(Constants.ADMINTHREAD_INTERVAL);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (true) {
            try {
                log.info("checkTimeOutJob!");
                LogFormat.formatLog(log, LogLevel.INFO, null, null, "run",
                        null, null, "start", null, null, "checkTimeOutJob");
                adminManager.checkTimeOutJob();
                Thread.sleep(Constants.ADMINTHREAD_INTERVAL);

            } catch (Exception e) {
                log.error(e.getMessage(), e);

            }

        }
    }

}
