package com.baidu.ecomqaep.schedule.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.ecomqaep.schedule.config.Constants;
import com.baidu.ecomqaep.schedule.manager.AdminManager;

public class CheckNodeThread extends Thread {

    private static Log log = LogFactory.getLog(CheckNodeThread.class);

    private static final CheckNodeThread INSTANCE = new CheckNodeThread();
    AdminManager adminManager = AdminManager.getInstance();

    private CheckNodeThread() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static CheckNodeThread getInstance() {
        return CheckNodeThread.INSTANCE;

    }

    public void run() {
        try {
            sleep(Constants.ADMINTHREAD_INTERVAL);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (true) {
            try {
                log.info("checkTimeOutJobTracker!");
                adminManager.checkTimeOutJobTracker();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            try {
                log.info("checkTimeOutTaskTracker!");
                adminManager.checkTimeOutTaskTracker();
                Thread.sleep(Constants.ADMINTHREAD_INTERVAL);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            try {
                Thread.sleep(Constants.ADMINTHREAD_INTERVAL);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
