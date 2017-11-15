package com.baidu.ecomqaep.schedule.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.baidu.ecomqaep.schedule.base.JobEntity;
import com.baidu.ecomqaep.schedule.base.JobResultEntity;
import com.baidu.ecomqaep.schedule.base.NodeEntity;
import com.baidu.ecomqaep.schedule.base.TopicGroupEntity;
import com.baidu.ecomqaep.schedule.config.Constants;
import com.baidu.ecomqaep.schedule.manager.EsManager;
import com.baidu.ecomqaep.schedule.manager.JobManager;
import com.baidu.ecomqaep.schedule.manager.RedisManager;
import com.baidu.ecomqaep.schedule.manager.TopicManager;
import com.baidu.ecomqaep.schedule.util.IdUtil;
import com.google.gson.Gson;

import ch.qos.logback.classic.Logger;

@Component
@Path("/job")
public class JobAction extends BaseAction {
    private static Log log = LogFactory.getLog(JobAction.class);

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/create")
    public String createJob(@FormParam("product") String product,
            @FormParam("topic") String topic, @FormParam("type") String type,
            @FormParam("priority") Integer priority,
            @FormParam("name") String name, @FormParam("data") String data,
            @FormParam("expiration") Long expiration,
            @FormParam("timeout") Long timeout,
            @FormParam("sender") String sender,
            @FormParam("topicGroup") String topicGroup,
            @FormParam("consumeGroup") String consumeGroup,
            @FormParam("retryCount") Integer retryCount,
            @FormParam("info") String info) {
        Gson gson = new Gson();
        try {
            JobEntity job = new JobEntity();
            if (StringUtils.isBlank(product)) {
                return error("product is null");
            } else {
                job.setProduct(product);
            }
            if (StringUtils.isBlank(data)) {
                return error("data is null");
            } else {
                job.setData(data);
            }
            if (StringUtils.isBlank(type)) {
                job.setType(Constants.JOB_TYPE_POINT);
            } else {
                job.setType(type.toUpperCase());
            }
            if (job.getType().equals(Constants.JOB_TYPE_POINT)
                    || job.getType().equals(Constants.JOB_TYPE_COMPETE)) {
                if (StringUtils.isBlank(topic)) {
                    return error("topic is null");
                } else {
                    job.setTopic(topic);
                }
            } else if (job.getType().equals(Constants.JOB_TYPE_PUBLISH)) {
                if (StringUtils.isBlank(topicGroup)) {
                    return error("topicGroup is null");
                } else {
                    job.setTopicGroup(topicGroup);
                }
            }
            if (job.getType().equals(Constants.JOB_TYPE_COMPETE)) {
                if (StringUtils.isBlank(consumeGroup)) {
                    return error("consumeGroup is null");
                } else {
                    job.setConsumeGroup(consumeGroup.toUpperCase());
                }
            }
            if (StringUtils.isBlank(sender)) {
                job.setSender(Constants.JOB_DEFAULT_SENDER);
            } else {
                job.setSender(sender);
            }
            if (StringUtils.isBlank(name)) {
                job.setName(Constants.JOB_DEFAULT_NAME);
            } else {
                job.setName(name);
            }
            if (expiration == null) {
                job.setExpiration(Constants.JOB_DEFAULT_EXPIRATION);
            } else {
                job.setExpiration(expiration * 1000);
            }
            if (retryCount == null) {
                job.setRetryCount(Constants.JOB_DEFAULT_RETRY_COUNT);
            } else {
                job.setRetryCount(retryCount);
            }
            if (timeout == null) {
                job.setTimeout(Constants.JOB_DEFAULT_TIMEOUT);
                job.setOriTimeout(Constants.JOB_DEFAULT_TIMEOUT);
            } else {
                job.setTimeout(timeout * 1000);
                job.setOriTimeout(timeout * 1000);
            }

            if (StringUtils.isNotBlank(info)) {
                job.setInfo(info);
            }

            if (priority == null) {
                job.setPriority(Constants.JOB_DEFAULT_PRIORITY);
            } else if (priority > Constants.PRIORITY_MAX
                    || priority < Constants.PRIORITY_MIN) {
                return error("priority must in [0,4]");
            } else {
                job.setPriority(priority);
            }
            job.setUuid(IdUtil.getUUID());
            job.setStatus(Constants.JOB_STATUS_READY);
            job.setSendTimestamp(new Date().getTime());
            job.setIsDeleted(Constants.JOB_DEFAULT_ISDELETED);
            if (JobManager.addJob(job)) {
                Map<String, String> map = new LinkedHashMap<String, String>();
                map.put("jobId", job.getUuid());
                return success(map);
            } else {
                return error(Constants.ACTION_RESULT_FAIL);
            }

        } catch (Exception e) {
            return error(Constants.ACTION_RESULT_ERROR);
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/subTopicGroup")
    public String subTopicGroup(@FormParam("product") String product,
            @FormParam("topic") String topic,
            @FormParam("topicGroup") String topicGroup,
            @FormParam("operator") String operator) {
        try {
            TopicGroupEntity topicGroupEntity = new TopicGroupEntity();
            if (product != null) {
                topicGroupEntity.setProduct(product);
            } else {
                return error("product is null");
            }
            topicGroupEntity.setTimestamp(new Date().getTime());

            if (topicGroup != null) {
                topicGroupEntity.setTopicGroup(topicGroup);
            } else {
                return error("topicGroup is null");
            }

            if (operator != null) {
                topicGroupEntity.setOperator(operator);
            } else {
                return error("operator is null");
            }
            if (topic != null) {
                String[] topics = topic.split(",");
                for (String singleTopic : topics) {
                    topicGroupEntity.setTopic(singleTopic);
                    TopicManager.subTopicGroup(topicGroupEntity);
                }
            } else {
                return error("topic is null");
            }
            return success("succeed to subscribe topicGroup");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error("fail to subscribe topicGroup");
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/cancelTopicGroup")
    public String cancelTopicGroup(@FormParam("product") String product,
            @FormParam("topic") String topic,
            @FormParam("topicGroup") String topicGroup,
            @FormParam("operator") String operator) {
        try {
            TopicGroupEntity topicGroupEntity = new TopicGroupEntity();
            if (product != null) {
                topicGroupEntity.setProduct(product);
            } else {
                return error("product is null");
            }
            topicGroupEntity.setTimestamp(new Date().getTime());

            if (topicGroup != null) {
                topicGroupEntity.setTopicGroup(topicGroup);
            } else {
                return error("topicGroup is null");
            }

            if (operator != null) {
                topicGroupEntity.setOperator(operator);
            } else {
                return error("operator is null");
            }
            if (topic != null) {
                String[] topics = topic.split(",");
                for (String singleTopic : topics) {
                    topicGroupEntity.setTopic(singleTopic);
                    TopicManager.cancelTopicGroup(topicGroupEntity);
                }
            } else {
                return error("topic is null");
            }
            return success("succeed to cancel topicGroup");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error("fail to cancle topicGroup");
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/searchProduct")
    @GET
    public String searchProduct(@QueryParam("product") String product) {
        try {
            JSONObject result = JobManager.getProductDetail(product);
            return success(result);
        } catch (Exception e) {
            return error("search product failed!");
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/searchProductForJQC")
    @GET
    public String searchProductForJQC(@QueryParam("product") String product) {
        try {
            JSONObject result = JobManager.getProductDetailForJQC(product);
            return success(result);
        } catch (Exception e) {
            return error("search product failed!");
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/searchJob")
    @GET
    public String searchJob(@QueryParam("uuid") String uuid) {
        try {
            JobResultEntity jobResult = new JobResultEntity();
            JobEntity job = RedisManager.getInstance().getJobEntity(uuid);
            if (job == null) {
                job = EsManager.getInstance().getJobEntity(uuid);
            }
            if (job == null) {
                return error("illegal uuid!");
            }
            jobResult = jobResult.transform2JobResult(job);
            if (job.getResult() != null && job.getType().toUpperCase().equals(Constants.JOB_TYPE_PUBLISH)
                    && job.getResult().equals(
                            Constants.JOB_DEFAULT_RESULT_PUBLISH)) {
                jobResult = JobManager.getPublishJobProgress(jobResult);
            }
            return success(jobResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error("search job failed!");
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/searchNode")
    @GET
    public String searchNode(@QueryParam("identity") String identity,
            @QueryParam("role") String role) {
        try {
            if (role.equals(Constants.NODE_ROLE_ADMIN)
                    || role.equals(Constants.NODE_ROLE_TASKTRACKER)
                    || role.equals(Constants.NODE_ROLE_JOBTRACKER)) {
                NodeEntity node = RedisManager.getInstance().getNodeEntity(
                        identity, role);
                return success(node);
            } else {
                return error("illegal role!");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error("search node failed!");
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/searchPublishJobDetails")
    @GET
    public String searchPublishJob(@QueryParam("uuid") String uuid) {
        JobResultEntity jobResult = new JobResultEntity();
        Gson gson = new Gson();
        JobEntity job = RedisManager.getInstance().getJobEntity(uuid);
        if (job == null) {
            job = EsManager.getInstance().getJobEntity(uuid);

        }
        if (job == null) {
            return error("illegal uuid!");
        }
        if (!job.getType().equals(Constants.JOB_TYPE_PUBLISH)) {
            return error("this job is not PUBLISH!");
        }
        jobResult = jobResult.transform2JobResult(job);
        String[] ids = job.getCorrelationId().split(
                Constants.ARRAY_BOUND_SYMBOL);
        List<JobEntity> sonJobEntitys = new ArrayList<JobEntity>();

        for (String id : ids) {
            JobEntity sonJobEntity = new JobEntity();
            sonJobEntity = RedisManager.getInstance().getJobEntity(id);
            if (sonJobEntity == null) {
                sonJobEntity = EsManager.getInstance().getJobEntity(id);
            }
            if (sonJobEntity != null) {
                sonJobEntitys.add(sonJobEntity);
            }
        }
        jobResult.setDetails(sonJobEntitys);
        return success(jobResult);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/extime")
    public String extendTimeoutTime(@FormParam("uuid") String uuid) {
        try {
            log.info("extend uuid=" + uuid); 
            JobEntity job = RedisManager.getInstance().getJobEntity(uuid);
            if (job == null) {
                log.error("extent uuid is invalid " + uuid); 
                return error("uuid is invalid"); 
            }
            job.setTimeout(job.getOriTimeout()
                    + (new Date().getTime() - job.getConsumeTimestamp()));
            if (RedisManager.getInstance().setJobEntity(job)) {
                return success();
            } else {
                return error("fail to update time-out limit");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error(e.getMessage());
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/update")
    public String updateJob(@FormParam("uuid") String uuid,
            @FormParam("data") String data,
            @FormParam("expiration") Long expiration,
            @FormParam("timeout") Long timeout,
            @FormParam("retryCount") Integer retryCount,
            @FormParam("info") String info,
            @FormParam("isDeleted") Integer isDeleted) {
        try {
            JobEntity job = RedisManager.getInstance().getJobEntity(uuid);
            if (job == null) {
                return error("uuid is error");
            }
            if (StringUtils.isNotBlank(data)) {
                job.setData(data);
            }
            if (expiration != null) {
                job.setExpiration(expiration * 1000);
            }
            if (retryCount != null) {
                job.setRetryCount(retryCount);
            }
            if (timeout != null) {
                job.setTimeout(timeout * 1000);
            }
            if (isDeleted != null && (isDeleted == 1 || isDeleted == 0)) {
                job.setIsDeleted(isDeleted);
            }

            if (RedisManager.getInstance().setJobEntity(job)) {
                return success();
            } else {
                return error("fail to update job");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error(e.getMessage());
        }

    }

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/deleteTopic")
    public String deleteTopic(@FormParam("product") String product,
            @FormParam("topic") String topic, @FormParam("sender") String sender) {
        Gson gson = new Gson();
        try {
            if (StringUtils.isBlank(product)) {
                return error("product is null");
            }
            if (StringUtils.isBlank(topic)) {
                return error("topic is null");
            }
            if (StringUtils.isBlank(sender)) {
                return error("sender is null");
            }
            log.info("[deleteTopic] " + sender + " delete topic: " + topic
                    + " product is " + product);
            JobManager.deleteTopic(product, topic);
            return success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error(e.getMessage());
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/recycle")
    public String recycleJob(@FormParam("uuid") String uuid) {
        Gson gson = new Gson();
        try {
            if (StringUtils.isBlank(uuid)) {
                return error("uuid is null");
            }
            JobEntity job = RedisManager.getInstance().getJobEntity(uuid);
            if (job == null) {
                return error("uuid is error");
            }
            log.info("[recycleTopic]uuid = " + uuid);
            JobManager.recycleJob(job);
            return success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error(e.getMessage());
        }
    }

}
