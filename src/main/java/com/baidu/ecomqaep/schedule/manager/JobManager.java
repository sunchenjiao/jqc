package com.baidu.ecomqaep.schedule.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.ecomqaep.schedule.base.AckEntity;
import com.baidu.ecomqaep.schedule.base.ConsumeEntity;
import com.baidu.ecomqaep.schedule.base.JobEntity;
import com.baidu.ecomqaep.schedule.base.JobResultEntity;
import com.baidu.ecomqaep.schedule.config.Constants;
import com.baidu.ecomqaep.schedule.util.IdUtil;

public class JobManager {
    private static Log log = LogFactory.getLog(JobManager.class);
    private static RedisManager redisManager = RedisManager.getInstance();

    public static Boolean addJob(JobEntity jobEntity) {
        if (jobEntity.getType().toUpperCase().equals(Constants.JOB_TYPE_POINT)) {
            return RedisManager.getInstance().addPointJob(jobEntity);
        } else if (jobEntity.getType().toUpperCase()
                .equals(Constants.JOB_TYPE_PUBLISH)) {
            Boolean result = false;
            Set<String> allTopic = RedisManager.getInstance().getTopicGroups(
                    jobEntity.getTopicGroup(), jobEntity.getProduct());
            for (String topic : allTopic) {
                JobEntity newJob = jobEntity.clone();
                newJob.setUuid(IdUtil.getUUID());
                newJob.setTopic(topic);
                newJob.setCorrelationId(jobEntity.getUuid());
                newJob.setType(Constants.JOB_TYPE_POINT);
                if (StringUtils.isBlank(jobEntity.getCorrelationId())) {
                    jobEntity.setCorrelationId(newJob.getUuid());
                } else {
                    jobEntity.setCorrelationId(jobEntity.getCorrelationId()
                            + Constants.ARRAY_BOUND_SYMBOL + newJob.getUuid());
                }
                log.info("群发job信息" + newJob);
                if (RedisManager.getInstance().addPointJob(newJob)) {
                    result = true;
                }
            }
            // 处理这条群发消息，直接转入已完成队列
            RedisManager.getInstance().setJobEntity(jobEntity);
            RedisManager.getInstance().ackJob(jobEntity.getUuid(),
                    Constants.JOB_DEFAULT_RESULT_PUBLISH, null);
            return result;
        } else if (jobEntity.getType().toUpperCase()
                .equals(Constants.JOB_TYPE_COMPETE)) {
            return RedisManager.getInstance().addCompeteJob(jobEntity);
        } else {
            return false;
        }
    }

    public static List<JobEntity> consumeJobs(ConsumeEntity consumeEntity) {
        List<JobEntity> result = new ArrayList<JobEntity>();
        if (consumeEntity.getConsumeType().toUpperCase()
                .equals(Constants.CONSUME_TYPE_POINT)) {
            String listNamePre = Constants.LIST_READY_JOB_PREFIX
                    + Constants.BOUND_SYMBOL + consumeEntity.getProduct()
                    + Constants.BOUND_SYMBOL + consumeEntity.getTopic();
            for (int priority = Constants.PRIORITY_MIN; priority <= Constants.PRIORITY_MAX; priority++) {
                String listName = listNamePre + Constants.BOUND_SYMBOL
                        + priority;
                while (result.size() < consumeEntity.getNumber()) {
                    JobEntity job = RedisManager.getInstance().consumeJob(
                            listName, consumeEntity.getConsumer());
                    if (job != null && job.getStatus().equals(Constants.JOB_STATUS_ACK)) {
                        redisManager.ackJob(job.getUuid(), job.getResult(), job.getBigResult());
                    }
                    if (job != null && job.getIsDeleted() < Constants.DELETED) {
                        result.add(job);
                    } else {
                        break;
                    }
                }
                if (result.size() >= consumeEntity.getNumber()) {
                    break;
                }
            }
            return result;
        } else if (consumeEntity.getConsumeType().toUpperCase()
                .equals(Constants.CONSUME_TYPE_COMPETE)) {
            Set<String> competeReadyListNames = RedisManager.getInstance()
                    .getCompeteReadyListNames(consumeEntity.getProduct());
            if (competeReadyListNames == null
                    || competeReadyListNames.size() == Constants.LIST_EMPTY) {
                return result;
            }
            List<String> listNames = new ArrayList<String>();
            for (String competeReadyListName : competeReadyListNames) {
                String paramString = competeReadyListName
                        .substring(Constants.LIST_READY_JOB_PREFIX.length()
                                + Constants.BOUND_SYMBOL.length());
                String[] params = paramString.split(Constants.BOUND_SYMBOL);
                String product = params[0];
                String topic = params[1];
                // String priority = params[2];
                if (!topic.equals(consumeEntity.getTopic())
                        || !product.equals(consumeEntity.getProduct())) {
                    continue;
                }
                List<String> jobConsumeGroups = new ArrayList<String>();
                for (int i = Constants.PARAM_GROUP_BEGIN_INDEX; i < params.length; i++) {
                    jobConsumeGroups.add(params[i]);
                }
                if (jobConsumeGroups.contains(Constants.CONSUME_GROUP_ALL)) {
                    if (consumeEntity.getIsExclusive()) {
                        continue;
                    } else {
                        listNames.add(competeReadyListName);
                    }
                } else {
                    if (jobConsumeGroups.contains(consumeEntity
                            .getConsumeGroup())) {
                        listNames.add(competeReadyListName);
                    }
                }
            }
            Collections.sort(listNames);
            for (String listName : listNames) {
                while (result.size() < consumeEntity.getNumber()) {
                    JobEntity job = RedisManager.getInstance().consumeJob(
                            listName, consumeEntity.getConsumer());
                    if (job != null && job.getIsDeleted() < Constants.DELETED) {
                        result.add(job);
                    } else {
                        break;
                    }
                }
                if (result.size() >= consumeEntity.getNumber()) {
                    break;
                }
            }
            return result;
        } else {
            return result;
        }
    }

    public static Boolean ackJob(AckEntity ackEntity) {
        return RedisManager.getInstance().ackJob(ackEntity.getUuid(),
                ackEntity.getResult(), ackEntity.getBigResult());
    }

    public static void recycleJob(JobEntity jobEntity) {
        redisManager.recycleJob(jobEntity);
    }

    public static Boolean deleteTopic(String product, String topic) {
        try {
            for (int i = Constants.PRIORITY_MIN; i <= Constants.PRIORITY_MAX; i++) {
                String listName = Constants.LIST_READY_JOB_PREFIX
                        + Constants.BOUND_SYMBOL + product
                        + Constants.BOUND_SYMBOL + topic
                        + Constants.BOUND_SYMBOL + i;
                Long listLength = redisManager.getReadyListJobCount(listName);
                if (listLength != null && listLength > 0) {
                    while (listLength >= 0) {
                        List<String> jobIds = redisManager.getListRange(
                                listName, 10000);
                        for (String jobId : jobIds) {
                            redisManager.deleteJobEntity(jobId);
                        }
                        listLength -= 10000;
                    }
                    redisManager.deleteKey(listName);
                }
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }

    }

    public static JSONObject getProductDetail(String product) {
        JSONObject json = null;
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> topicGroupMap = new HashMap<String, Object>();
        try {
            Long count = 0L;
            Long competeReadyJobTotalCount = 0L;
            Long pointReadyJobTotalCount = 0L;
            result.put("product", product);
            Set<String> competeListName = redisManager
                    .getCompeteReadyListNames(product);
            for (String listName : competeListName) {
                try {
                    count = redisManager.getReadyListJobCount(listName);
                    if (count != 0L) {
                        competeReadyJobTotalCount = competeReadyJobTotalCount
                                + count;
                        result.put(Constants.JOB_TYPE_COMPETE
                                + Constants.BOUND_SYMBOL + listName, count);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

            Set<String> pointListName = redisManager
                    .getPointReadyListNames(product);
            for (String listName : pointListName) {
                try {
                    count = redisManager.getReadyListJobCount(listName);
                    if (count != 0L) {
                        pointReadyJobTotalCount = pointReadyJobTotalCount
                                + count;
                        result.put(Constants.JOB_TYPE_POINT
                                + Constants.BOUND_SYMBOL + listName, count);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            result.put("competeReadyJobCount", competeReadyJobTotalCount);
            result.put("pointReadyJobCount", pointReadyJobTotalCount);
            Set<String> allTopicGroupNames = redisManager
                    .getAllTopicGroupNames();
            for (String topicGroup : allTopicGroupNames) {
                try {
                    String[] args = topicGroup.split(Constants.BOUND_SYMBOL);
                    String topicGroupName = args[3];
                    if (args[2].equals(product)) {
                        Set<String> topics = redisManager.getTopicGroups(
                                topicGroupName, product);
                        String groups = "";
                        for (String topic : topics) {
                            groups = groups + topic + Constants.BOUND_SYMBOL;
                        }
                        groups = groups.substring(0, groups.length() - 1);
                        result.put(topicGroup, groups);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

            json = JSONObject.fromObject(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return json;
    }

    public static JobResultEntity getPublishJobProgress(JobResultEntity job) {
        String[] ids = job.getCorrelationId().split(
                Constants.ARRAY_BOUND_SYMBOL);
        Long ready = 0L;
        Long consumed = 0L;
        Long acked = 0L;
        JobEntity son = null;
        for (String id : ids) {
            try {
                son = redisManager.getJobEntity(id);
                if (son != null) {
                    if (son.getStatus().equals(Constants.JOB_STATUS_READY)) {
                        ready = ready + 1;
                    } else if (son.getStatus().equals(
                            Constants.JOB_STATUS_CONSUMED)) {
                        consumed = consumed + 1;
                    } else if (son.getStatus().equals(Constants.JOB_STATUS_ACK)) {
                        acked = acked + 1;
                    }
                } else {
                    son = EsManager.getInstance().getJobEntity(id);
                    if (son != null) {
                        acked = acked + 1;
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        job.setTotal(ready + consumed + acked);
        job.setReadyCount(ready);
        job.setConsumedCount(consumed);
        job.setAckedCount(acked);
        return job;
    }

    public static JSONObject getProductDetailForJQC(String product) {
        JSONObject json = null;
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> pointList2count = new HashMap<String, Object>();
        Map<String, Object> competeList2count = new HashMap<String, Object>();
        Map<String, Object> topicGroupMap = new HashMap<String, Object>();
        try {
            Long count = 0L;
            Long competeReadyJobTotalCount = 0L;
            Long pointReadyJobTotalCount = 0L;
            result.put("product", product);
            Set<String> competeListName = redisManager
                    .getCompeteReadyListNames(product);
            for (String listName : competeListName) {
                try {
                    count = redisManager.getReadyListJobCount(listName);
                    if (count != 0L) {
                        competeReadyJobTotalCount = competeReadyJobTotalCount
                                + count;
                        competeList2count.put(listName, count);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            result.put("competeList", competeList2count);
            Set<String> pointListName = redisManager
                    .getPointReadyListNames(product);
            for (String listName : pointListName) {
                try {
                    count = redisManager.getReadyListJobCount(listName);
                    if (count != 0L) {
                        pointReadyJobTotalCount = pointReadyJobTotalCount
                                + count;
                        pointList2count.put(listName, count);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            result.put("pointList", pointList2count);
            result.put("competeReadyJobCount", competeReadyJobTotalCount);
            result.put("pointReadyJobCount", pointReadyJobTotalCount);
            Set<String> allTopicGroupNames = redisManager
                    .getAllTopicGroupNames();
            for (String topicGroup : allTopicGroupNames) {
                try {
                    String[] args = topicGroup.split(Constants.BOUND_SYMBOL);
                    String topicGroupName = args[3];
                    if (args[2].equals(product)) {
                        Set<String> topics = redisManager.getTopicGroups(
                                topicGroupName, product);
                        String groups = "";
                        for (String topic : topics) {
                            groups = groups + topic + Constants.BOUND_SYMBOL;
                        }
                        groups = groups.substring(0, groups.length() - 1);
                        topicGroupMap.put(topicGroup, groups);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            result.put("topicGroup", topicGroupMap);
            json = JSONObject.fromObject(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return json;
    }
}
