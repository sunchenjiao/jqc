package com.baidu.ecomqaep.schedule.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.ecomqaep.schedule.base.JobEntity;
import com.baidu.ecomqaep.schedule.base.NodeEntity;
import com.baidu.ecomqaep.schedule.config.Config;
import com.baidu.ecomqaep.schedule.config.Constants;
import com.baidu.ecomqaep.schedule.log.LogFormat;
import com.baidu.ecomqaep.schedule.log.LogFormat.IdKeys;
import com.baidu.ecomqaep.schedule.log.LogFormat.LogLevel;
import com.baidu.ecomqaep.schedule.util.MailUtil;
import com.baidu.ecomqaep.schedule.util.ShortMessageUtil;
import com.google.gson.Gson;

public class AdminManager {
    private static Log log = LogFactory.getLog(RedisManager.class);
    private static final AdminManager instance = new AdminManager();
    RedisManager redisManager = RedisManager.getInstance();
    EsManager esManager = EsManager.getInstance();

    public static AdminManager getInstance() {
        return AdminManager.instance;
    }

    public void checkTimeOutJobTracker() {
        Map<String, String> allJobTrackers = redisManager
                .getAllRoleNode(Constants.NODE_ROLE_JOBTRACKER);
        List<NodeEntity> timeOutJobTracker = checkTimeoutNode(allJobTrackers,
                Constants.JOBTRACKER_TIMEOUTLIMIT);
        log.info("所属节点种类：" + Config.NODEROLE);
        if (timeOutJobTracker != null) {
            for (NodeEntity jobTracker : timeOutJobTracker) {
                try {
                    redisManager.deleteNodeEntity(
                            Constants.NODE_ROLE_JOBTRACKER,
                            jobTracker.getIdentity());
                    String hostname = jobTracker.getHostname();
                    String ip = jobTracker.getIp();
                    String title = "jobTracker " + jobTracker.getIdentity()
                            + "掉线";
                    String content = jobTracker.getIdentity()
                            + "掉线,所属类型：jobTracker，请速度重启" + "; Hostname: "
                            + hostname + "; Ip:" + ip + "；  注册时间："
                            + getTime(jobTracker.getLastRegisterTimestamp())
                            + "; 上次心跳时间： "
                            + getTime(jobTracker.getLastHeartbeatTimestamp());
                    MailUtil.sendHtmlAttachMail(Config.MailFrom,
                            Config.MailTo.split(","), null, title, content,
                            null, null);
                    ShortMessageUtil.send(content, "13816966688");
                    ShortMessageUtil.send(content, "13817713601");

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

        }
    }

    public void checkTimeOutTaskTracker() {
        Map<String, String> allTaskTrackers = redisManager
                .getAllRoleNode(Constants.NODE_ROLE_TASKTRACKER);
        List<NodeEntity> timeOutTaskTracker = checkTimeoutNode(allTaskTrackers,
                Constants.TASKTRACKER_TIMEOUTLIMIT);
        if (timeOutTaskTracker != null) {
            for (NodeEntity taskTracker : timeOutTaskTracker) {
                try {
                    redisManager.deleteNodeEntity(
                            Constants.NODE_ROLE_TASKTRACKER,
                            taskTracker.getIdentity());
                    String hostname = taskTracker.getHostname();
                    String ip = taskTracker.getIp();
                    String title = "taskTracker " + taskTracker.getIdentity()
                            + "掉线";
                    String content = taskTracker.getIdentity()
                            + "掉线,所属类型：taskTracker。请速度重启" + "; Hostname: "
                            + hostname + "; Ip:" + ip + "；  注册时间："
                            + getTime(taskTracker.getLastRegisterTimestamp())
                            + "; 上次心跳时间： "
                            + getTime(taskTracker.getLastHeartbeatTimestamp());

                    MailUtil.sendHtmlAttachMail(Config.MailFrom,
                            Config.MailTo.split(","), null, title, content,
                            null, null);
                    // ShortMessageUtil.send(content, "13816966688");
                    // ShortMessageUtil.send(content, "13817713601");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

        }
    }

    public void checkTimeOutJob() {
        List<String> consumeList = redisManager.getConsumeJobList();
        for (String jobId : consumeList) {
            try {
                JobEntity jobEntity = redisManager.getJobEntity(jobId);
                if (jobEntity != null) {
                    if (jobEntity.getIsDeleted().equals(Constants.DELETED)) {
                        redisManager.ackJob(jobId,
                                Constants.JOB_DEFAULT_RESULT_EXPIRED, null);
                        continue;
                    }
                    if (jobEntity.getStatus().equals(
                            Constants.JOB_STATUS_CONSUMED)) {
                        if (jobEntity.getConsumeTimestamp() != null) {
                            Date date = new Date();
                            if (date.getTime()
                                    - jobEntity.getConsumeTimestamp() > jobEntity
                                            .getTimeout()) {
                                if (jobEntity.getRetryCount() > Constants.JOB_DEFAULT_RETRY_COUNT) {
                                    int retryCount = jobEntity.getRetryCount() - 1;
                                    jobEntity.setRetryCount(retryCount);
                                    if (jobEntity.getOriTimeout() != null) {
                                        jobEntity.setTimeout(jobEntity.getOriTimeout());
                                    }
                                    redisManager.recycleJob(jobEntity);
                                } else {
                                    redisManager
                                            .ackJob(jobId,
                                                    Constants.JOB_DEFAULT_RESULT_TIMEOUT, null);
                                }
                            }
                        } else {
                            LogFormat.formatLog(log, LogLevel.ERROR,
                                    new IdKeys[] { IdKeys.jobId },
                                    new Object[] { jobId }, "checkTimeOutJob",
                                    "redis", "admin", "check",
                                    jobEntity.toString(), null,
                                    "no consume timestamp.");
                        }

                    } else if (jobEntity.getStatus().equals(
                            Constants.JOB_STATUS_ACK)) {
                        redisManager.ackJob(jobId, jobEntity.getResult(), jobEntity.getBigResult());
                    } else {
                        redisManager.recycleJob(jobEntity);
                    }
                } else {
                    redisManager.deleteFromConsumeList(jobId);
                    LogFormat.formatLog(log, LogLevel.ERROR,
                            new IdKeys[] { IdKeys.jobId },
                            new Object[] { jobId }, "checkTimeOutJob", "redis",
                            "admin", "check", null, null,
                            "Wrong jobId: no such JobEntity.");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                LogFormat.formatLog(log, LogLevel.ERROR, null, null,
                        "checkTimeOutJob", "redis", "admin", "end",
                        e.getMessage(), null, null);
            }
        }

    }

    public void insertJobToEs() {
        Set<String> jobIds = redisManager.getAckJobSet();
        for (String jobId : jobIds) {
            JobEntity job = redisManager.getJobEntity(jobId);
            if (job != null) {
                esManager.addJobEntity(job);
            }
            redisManager.deleteAckJob(jobId);
        }
    }

    private List<NodeEntity> checkTimeoutNode(Map<String, String> allTrackers,
            Integer timeOutLimit) {
        Gson gson = new Gson();
        List<NodeEntity> timeOutTracker = new ArrayList<NodeEntity>();
        for (String identity : allTrackers.keySet()) {
            try {
                NodeEntity nodeEntity = new NodeEntity();
                nodeEntity = gson.fromJson(allTrackers.get(identity),
                        NodeEntity.class);
                Long lastHeartBeatTime = nodeEntity.getLastHeartbeatTimestamp();
                if (lastHeartBeatTime != null) {
                    if (new Date().getTime() - lastHeartBeatTime > timeOutLimit) {
                        timeOutTracker.add(nodeEntity);
                    }
                } else {
                    LogFormat
                            .formatLog(log, LogLevel.WARN, null, null,
                                    "checkTimeOutJobTracker", "redis", null,
                                    "check",
                                    nodeEntity != null ? nodeEntity.toString() : null, null,
                                    "empty nodeEntity or empty last heartbeat timestamp");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return timeOutTracker;
    }

    public String jobSnapshot() {
        String result = "";
        Set<String> productSet = redisManager.getAllProduct();
        Set<String> allTopicGroupNames = redisManager.getAllTopicGroupNames();
        String consumeAndAckJobCount = "";
        consumeAndAckJobCount = consumeAndAckJobCount
                + String.format(
                        "执行中任务数（consumeJobCount）: %10d;\r\n已完成任务数（ackJobCount）: %10d\r\n",
                        redisManager.getConsumeJobList().size(), redisManager
                                .getAckJobSet().size());

        String[] productDetails = new String[productSet.size()];
        int i = 0;
        Long count = 0L;
        Long competeReadyJobTotalCount = 0L;
        Long pointReadyJobTotalCount = 0L;
        for (String product : productSet) {
            try {
                competeReadyJobTotalCount = 0L;
                pointReadyJobTotalCount = 0L;
                productDetails[i] = String.format("\r\n \r\n业务线：%10s\r\n",
                        product);
                Set<String> competeListName = redisManager
                        .getCompeteReadyListNames(product);
                productDetails[i] = productDetails[i] + "竞争待执行任务队列：\r\n";
                for (String listName : competeListName) {
                    try {
                        count = redisManager.getReadyListJobCount(listName);
                        competeReadyJobTotalCount = competeReadyJobTotalCount
                                + count;
                        productDetails[i] = productDetails[i]
                                + String.format("%20s   COUNT: %8d\r\n",
                                        listName, count);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }

                productDetails[i] = productDetails[i]
                        + String.format("竞争待执行任务总数: %10d;\r\n",
                                competeReadyJobTotalCount);
                Set<String> pointListName = redisManager
                        .getPointReadyListNames(product);
                productDetails[i] = productDetails[i] + "\r\n点对点待执行任务队列：\r\n";
                for (String listName : pointListName) {
                    try {
                        count = redisManager.getReadyListJobCount(listName);
                        pointReadyJobTotalCount = pointReadyJobTotalCount
                                + count;
                        productDetails[i] = productDetails[i]
                                + String.format("%20s  COUNT: %8d\r\n",
                                        listName, count);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                productDetails[i] = productDetails[i]
                        + String.format("点对点待执行任务总数: %10d; \r\n",
                                pointReadyJobTotalCount);
                productDetails[i] = productDetails[i]
                        + "----------------------------------------------------------------";
                i = i + 1;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        String topicGroups = "\r\n \r\ntopicGroup订阅情况：";
        for (String topicGroup : allTopicGroupNames) {
            try {
                topicGroups = topicGroups
                        + String.format("\r\n%10s\r\n    ", topicGroup);
                String[] args = topicGroup.split(Constants.BOUND_SYMBOL);
                String topicGroupName = args[3];
                String product = args[2];
                Set<String> topics = redisManager.getTopicGroups(
                        topicGroupName, product);
                for (String topic : topics) {
                    topicGroups = topicGroups + topic + ", ";
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        result = consumeAndAckJobCount;
        for (int j = 0; j < productSet.size(); j++) {
            try {
                result = result + productDetails[j];
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        result = result + topicGroups;
        return result;
    }

    public void deleteNodeEntity(String role, String identity) {
        RedisManager.getInstance().deleteNodeEntity(role, identity);
    }

    public String nodeSnapshot() {
        String trackers = "JOBTRACKERS:";
        Map<String, String> allJobTrackers = redisManager
                .getAllRoleNode(Constants.NODE_ROLE_JOBTRACKER);
        Map<String, String> allTaskTrackers = redisManager
                .getAllRoleNode(Constants.NODE_ROLE_TASKTRACKER);
        Map<String, String> allAdmins = redisManager
                .getAllRoleNode(Constants.NODE_ROLE_ADMIN);
        Gson gson = new Gson();
        if (allJobTrackers.size() > 0) {
            for (String jobTracker : allJobTrackers.keySet()) {
                try {
                    NodeEntity jobTrackerEntity = gson.fromJson(
                            allJobTrackers.get(jobTracker), NodeEntity.class);
                    trackers = trackers
                            + String.format(
                                    "\r\n\r\nIdentity: %15s; \r\nVersion: %10s; \r\nLastRegisterTime: %15s; "
                                            + "\r\nLastHeartbeatTime: %15s; \r\nIP: %12s; \r\nHostName: %10s ",
                                    jobTracker, jobTrackerEntity.getVersion(),
                                    getTime(jobTrackerEntity
                                            .getLastRegisterTimestamp()),
                                    getTime(jobTrackerEntity
                                            .getLastHeartbeatTimestamp()),
                                    jobTrackerEntity.getIp(), jobTrackerEntity
                                            .getHostname());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        trackers = trackers + "\r\n\r\nTASKTRACKERS: ";

        if (allTaskTrackers.size() > 0) {
            for (String taskTracker : allTaskTrackers.keySet()) {
                try {
                    NodeEntity taskTrackerEntity = gson.fromJson(
                            allTaskTrackers.get(taskTracker), NodeEntity.class);
                    trackers = trackers
                            + String.format(
                                    "\r\nIdentity: %15s; \r\nVersion: %10s; \r\nLastRegisterTime: %15s; "
                                            + "\r\nLastHeartbeatTime: %15s; \r\nIP: %12s; \r\nHostName: %10s "
                                            + "\r\nInfo: %s;",
                                    taskTracker,
                                    taskTrackerEntity.getVersion(),
                                    getTime(taskTrackerEntity
                                            .getLastRegisterTimestamp()),
                                    getTime(taskTrackerEntity
                                            .getLastHeartbeatTimestamp()),
                                    taskTrackerEntity.getIp(),
                                    taskTrackerEntity.getHostname(),
                                    taskTrackerEntity.getInfo());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        trackers = trackers + "\r\n\r\nADMIN:";
        if (allAdmins.size() > 0) {
            for (String admin : allAdmins.keySet()) {
                try {
                    NodeEntity adminEntity = gson.fromJson(
                            allAdmins.get(admin), NodeEntity.class);
                    trackers = trackers
                            + String.format(
                                    "\r\n\r\nIdentity: %15s; \r\nVersion: %10s; \r\nLastRegisterTime: %15s; "
                                            + "\r\nIP: %12s; \r\nHostName: %10s ",
                                    admin, adminEntity.getVersion(),
                                    getTime(adminEntity
                                            .getLastRegisterTimestamp()),
                                    adminEntity.getIp(), adminEntity
                                            .getHostname());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        return trackers;
    }

    private static String getTime(Long date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(date));
    }
}
