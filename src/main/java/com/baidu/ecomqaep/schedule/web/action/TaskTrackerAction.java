package com.baidu.ecomqaep.schedule.web.action;

import java.util.Date;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.baidu.ecomqaep.schedule.base.AckEntity;
import com.baidu.ecomqaep.schedule.base.ConsumeEntity;
import com.baidu.ecomqaep.schedule.base.JobEntity;
import com.baidu.ecomqaep.schedule.base.NodeEntity;
import com.baidu.ecomqaep.schedule.base.TaskTracker2JobTrackerModel;
import com.baidu.ecomqaep.schedule.config.Constants;
import com.baidu.ecomqaep.schedule.log.LogFormat;
import com.baidu.ecomqaep.schedule.log.LogFormat.IdKeys;
import com.baidu.ecomqaep.schedule.log.LogFormat.LogLevel;
import com.baidu.ecomqaep.schedule.manager.JobManager;
import com.baidu.ecomqaep.schedule.manager.NodeManager;
import com.baidu.ecomqaep.schedule.manager.RedisManager;
import com.google.gson.Gson;

@Component
@Path("/taskTracker")
public class TaskTrackerAction extends BaseAction {
    private static Log log = LogFactory.getLog(TaskTrackerAction.class);

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/register")
    public String taskTrackerRegister(String data) {
        Gson gson = new Gson();
        try {
            TaskTracker2JobTrackerModel model = gson.fromJson(data,
                    TaskTracker2JobTrackerModel.class);
            String identity = model.getIdentity();
            String passwd = model.getPasswd();
            if (identity == null || passwd == null) {
                return error("identity or passwd is null");
            }
            identity = identity.trim();
            passwd = passwd.trim();
            LogFormat.formatLog(log, LogLevel.INFO,
                    new IdKeys[] { IdKeys.nodeId }, new String[] { identity },
                    "taskTrackerRegister", model.getConsumeGroup(), "action",
                    "heartbeat", null, null, null);
            Date now = new Date();
            NodeEntity nodeEntity = new NodeEntity();
            nodeEntity.setGroup(model.getConsumeGroup());
            nodeEntity.setHostname(model.getHostname());
            nodeEntity.setIdentity(identity);
            nodeEntity.setIp(model.getIp());
            nodeEntity.setLastHeartbeatTimestamp(now.getTime());
            nodeEntity.setLastRegisterTimestamp(now.getTime());
            nodeEntity.setPasswd(passwd);
            nodeEntity.setRole(Constants.NODE_ROLE_TASKTRACKER);
            nodeEntity.setStatus(Constants.NODE_STATUS_REGISTERED);
            nodeEntity.setVersion(model.getVersion());
            nodeEntity.setInfo(model.getInfo());

            String token = NodeManager.taskTrackerRegister(identity, passwd,
                    nodeEntity);
            if (token != null) {
                return success(token);
            } else {
                return error(Constants.ACTION_RESULT_FAIL);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error(Constants.ACTION_RESULT_ERROR);
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/heartbeat")
    public String taskTrackerHeartbeat(String data) {
        Gson gson = new Gson();
        try {
            log.info("[taskTrackerHeartbeat] data=" + data);
            TaskTracker2JobTrackerModel model = gson.fromJson(data,
                    TaskTracker2JobTrackerModel.class);
            String identity = model.getIdentity().trim();
            String token = model.getToken().trim();
            NodeEntity taskTracker = RedisManager.getInstance()
                    .getTaskTrackerEntity(identity);
            if (taskTracker == null) {
                return error("taskTracker为空");
            }
            if (token == null || taskTracker.getToken() == null
                    || !taskTracker.getToken().equals(token)) {
                return error("token为空或不正确，token=" + token);
            }
            Date now = new Date();
            taskTracker.setLastHeartbeatTimestamp(now.getTime());
            taskTracker.setStatus(Constants.NODE_STATUS_ALIVE);
            if (model.getInfo() != null) {
                taskTracker.setInfo(model.getInfo());
            }
            if (StringUtils.isNotBlank(model.getConsumeGroup())) {
                taskTracker.setGroup(model.getConsumeGroup());
            }
            if (StringUtils.isNotBlank(model.getIp())) {
                taskTracker.setIp(model.getIp());
            }
            if (StringUtils.isNotBlank(model.getHostname())) {
                taskTracker.setHostname(model.getHostname());
            }
            if (StringUtils.isNotBlank(model.getVersion())) {
                taskTracker.setVersion(model.getVersion());
            }
            NodeManager.heartbeatUpdate(taskTracker);
            if (model.getAckJobs() != null
                    && model.getAckJobs().size() > Constants.LIST_EMPTY) {
                for (JobEntity jobEntity : model.getAckJobs()) {
                    AckEntity ackEntity = new AckEntity();
                    if (StringUtils.isNotBlank(jobEntity.getBigResult())) {
                        ackEntity.setBigResult(jobEntity.getBigResult());
                    }
                    ackEntity.setResult(jobEntity.getResult());
                    ackEntity.setUuid(jobEntity.getUuid());
                    JobManager.ackJob(ackEntity);
                }
            }
            if (StringUtils.isNotBlank(model.getProduct())
                    && StringUtils.isNotBlank(model.getTopic())) {
                ConsumeEntity consumeEntity = new ConsumeEntity();

                consumeEntity.setConsumer(model.getIdentity());
                if (model.getIsExclusive() == null) {
                    consumeEntity.setIsExclusive(true);
                } else {
                    consumeEntity.setIsExclusive(model.getIsExclusive());
                }
                if (model.getConsumeNum() == null) {
                    consumeEntity.setNumber(Constants.DEFAULT_CONSUME_NUM);
                } else {
                    consumeEntity.setNumber(model.getConsumeNum());
                }
                if (model.getConsumeType() == null) {
                    consumeEntity.setConsumeType(Constants.CONSUME_TYPE_POINT);
                } else {
                    consumeEntity.setConsumeType(model.getConsumeType());
                }
                if (consumeEntity.getConsumeType().equals(
                        Constants.CONSUME_TYPE_COMPETE)
                        && StringUtils.isBlank(model.getConsumeGroup())) {
                    return success();
                }
                consumeEntity.setConsumeGroup(model.getConsumeGroup());
                consumeEntity.setProduct(model.getProduct());
                consumeEntity.setTopic(model.getTopic());
                List<JobEntity> jobs = JobManager.consumeJobs(consumeEntity);
                return success(jobs);
            }
            return success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error(Constants.ACTION_RESULT_ERROR);
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/job")
    public String interactWithoutToken(String data) {
        Gson gson = new Gson();
        try {
            log.info("[interactWithoutToken] data=" + data);
            TaskTracker2JobTrackerModel model = gson.fromJson(data,
                    TaskTracker2JobTrackerModel.class);
            String identity = model.getIdentity().trim();
            String pwd = model.getPasswd().trim();
            NodeEntity taskTracker = RedisManager.getInstance()
                    .getTaskTrackerEntity(identity);
            if (taskTracker == null) {
                return error(Constants.ACTION_RESULT_FAIL);
            }
            if (pwd == null || taskTracker.getPasswd() == null
                    || !taskTracker.getPasswd().equals(pwd)) {
                return error(Constants.ACTION_RESULT_FAIL);
            }
            Date now = new Date();
            taskTracker.setLastHeartbeatTimestamp(now.getTime());
            taskTracker.setStatus(Constants.NODE_STATUS_ALIVE);
            if (model.getInfo() != null) {
                taskTracker.setInfo(model.getInfo());
            }
            if (StringUtils.isNotBlank(model.getConsumeGroup())) {
                taskTracker.setGroup(model.getConsumeGroup());
            }
            if (StringUtils.isNotBlank(model.getIp())) {
                taskTracker.setIp(model.getIp());
            }
            if (StringUtils.isNotBlank(model.getHostname())) {
                taskTracker.setHostname(model.getHostname());
            }
            if (StringUtils.isNotBlank(model.getVersion())) {
                taskTracker.setVersion(model.getVersion());
            }
            NodeManager.heartbeatUpdate(taskTracker);
            if (model.getAckJobs() != null
                    && model.getAckJobs().size() > Constants.LIST_EMPTY) {
                for (JobEntity jobEntity : model.getAckJobs()) {
                    AckEntity ackEntity = new AckEntity();
                    if (StringUtils.isNotBlank(jobEntity.getBigResult())) {
                        ackEntity.setBigResult(jobEntity.getBigResult());
                    }
                    ackEntity.setResult(jobEntity.getResult());
                    ackEntity.setUuid(jobEntity.getUuid());
                    JobManager.ackJob(ackEntity);
                }
            }
            if (StringUtils.isNotBlank(model.getProduct())
                    && StringUtils.isNotBlank(model.getTopic())) {
                ConsumeEntity consumeEntity = new ConsumeEntity();

                consumeEntity.setConsumer(model.getIdentity());
                if (model.getIsExclusive() == null) {
                    consumeEntity.setIsExclusive(true);
                } else {
                    consumeEntity.setIsExclusive(model.getIsExclusive());
                }
                if (model.getConsumeNum() == null) {
                    consumeEntity.setNumber(Constants.DEFAULT_CONSUME_NUM);
                } else {
                    consumeEntity.setNumber(model.getConsumeNum());
                }
                if (model.getConsumeType() == null) {
                    consumeEntity.setConsumeType(Constants.CONSUME_TYPE_POINT);
                } else {
                    consumeEntity.setConsumeType(model.getConsumeType());
                }
                if (consumeEntity.getConsumeType().equals(
                        Constants.CONSUME_TYPE_COMPETE)
                        && StringUtils.isBlank(model.getConsumeGroup())) {
                    return success();
                }
                consumeEntity.setConsumeGroup(model.getConsumeGroup());
                consumeEntity.setProduct(model.getProduct());
                consumeEntity.setTopic(model.getTopic());
                List<JobEntity> jobs = JobManager.consumeJobs(consumeEntity);
                return success(jobs);
            }
            return success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error(Constants.ACTION_RESULT_FAIL);
        }
    }

}
