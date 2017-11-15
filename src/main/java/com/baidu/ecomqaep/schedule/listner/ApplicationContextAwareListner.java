package com.baidu.ecomqaep.schedule.listner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.baidu.ecomqaep.schedule.admin.AdminHeartbeatThread;
import com.baidu.ecomqaep.schedule.admin.CheckJobThread;
import com.baidu.ecomqaep.schedule.admin.CheckNodeThread;
import com.baidu.ecomqaep.schedule.admin.InsertEsThread;
import com.baidu.ecomqaep.schedule.config.Config;
import com.baidu.ecomqaep.schedule.config.Constants;
import com.baidu.ecomqaep.schedule.jobTracker.JobTrackerHeartBeatThread;
import com.baidu.ecomqaep.schedule.manager.AdminManager;
import com.baidu.ecomqaep.schedule.manager.EsManager;
import com.baidu.ecomqaep.schedule.manager.RedisManager;

public class ApplicationContextAwareListner implements ApplicationContextAware {
    private static Log logger = LogFactory
            .getLog(ApplicationContextAwareListner.class);

    public static ApplicationContext context;
    public static String nodeRole = Config.NODEROLE;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        logger.info("[setApplicationContext],applicationContext initial and system get context"
                + " by [ApplicationContextAwareListner.context]");
        ApplicationContextAwareListner.context = applicationContext;
        if (nodeRole.equals(Constants.NODE_ROLE_ADMIN)) {
            AdminManager admin = AdminManager.getInstance();
            logger.info("[setApplicationContext], initial ADMIN NODE!");
            AdminHeartbeatThread.getInstance().start();
            CheckNodeThread.getInstance().start();
            CheckJobThread.getInstance().start();
            InsertEsThread.getInstance().start();
            RedisManager redis = RedisManager.getInstance();
            EsManager es = EsManager.getInstance();
            JobTrackerHeartBeatThread.getInstance().start();

        } else {
            RedisManager redis = RedisManager.getInstance();
            EsManager es = EsManager.getInstance();
            JobTrackerHeartBeatThread.getInstance().start();
        }

    }
}
