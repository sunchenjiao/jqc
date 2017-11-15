package com.baidu.ecomqaep.schedule.jobTracker;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.ecomqaep.schedule.base.NodeEntity;
import com.baidu.ecomqaep.schedule.config.Constants;
import com.baidu.ecomqaep.schedule.manager.NodeManager;

public class JobTrackerHeartBeatThread extends Thread {
    public static Log log = LogFactory.getLog(JobTrackerHeartBeatThread.class);
    public static final JobTrackerHeartBeatThread INSTANCE = new JobTrackerHeartBeatThread();

    public static JobTrackerHeartBeatThread getInstance() {
        return JobTrackerHeartBeatThread.INSTANCE;
    }

    public JobTrackerHeartBeatThread() {
        super();
    }

    public void run() {
        NodeEntity node = register();
        if (node != null) {
            while (true) {
                try {
                    node.setLastHeartbeatTimestamp(new Date().getTime());
                    NodeManager.heartbeatUpdate(node);
                    sleep(5000);
                } catch (Exception e) {
                    try {
                        sleep(5000);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        log.error(e.getMessage(), e);
                    }
                    log.error(e.getMessage(), e);
                }
            }
        }

    }

    public NodeEntity register() {
        return NodeManager.jobTrackerRegister(Constants.NODE_ROLE_JOBTRACKER);
    }

}
