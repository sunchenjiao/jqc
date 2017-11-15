package com.baidu.ecomqaep.schedule.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.baidu.ecomqaep.schedule.base.NodeEntity;
import com.baidu.ecomqaep.schedule.config.Constants;
import com.baidu.ecomqaep.schedule.manager.AdminManager;
import com.baidu.ecomqaep.schedule.manager.EsManager;
import com.baidu.ecomqaep.schedule.manager.RedisManager;
import com.google.gson.Gson;

@Component
@Path("/admin")
public class AdminAction extends BaseAction {
    private static Log log = LogFactory.getLog(AdminAction.class);

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/nodeCheck")
    @GET
    public String nodeSnapshot() {
        String result = null;
        try {
            AdminManager admin = AdminManager.getInstance();
            result = admin.nodeSnapshot();
            return success(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error("fail");
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jobCheck")
    @GET
    public String jobSnapshot() {
        String result = null;
        try {
            AdminManager admin = AdminManager.getInstance();
            result = admin.jobSnapshot();
            return success(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error("fail");
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/clearRedis")
    @GET
    public String clear() {
        try {
            RedisManager.getInstance().clear();
        } catch (Exception e) {
            return error("clear redis fail");
        }
        return success();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/deleteNode")
    public String deleteNode(@FormParam("role") String role,
            @FormParam("identity") String identity) {
        try {
            log.info("[deleteNode] identity=" + identity + "  role=" + role);
            AdminManager admin = AdminManager.getInstance();
            admin.deleteNodeEntity(role.toUpperCase(), identity);
            return success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error("fail");
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getAllNode")
    @GET
    public String getAllNode(@QueryParam("role") String role) {
        try {
            Gson gson = new Gson();
            if (role != null
                    && (role.equals(Constants.NODE_ROLE_ADMIN)
                            || role.equals(Constants.NODE_ROLE_TASKTRACKER) || role
                                    .equals(Constants.NODE_ROLE_JOBTRACKER))) {
                Map<String, String> nodes = RedisManager.getInstance()
                        .getAllRoleNode(role);
                List<NodeEntity> result = new ArrayList<NodeEntity>();
                for (String key : nodes.keySet()) {
                    NodeEntity node = gson.fromJson(nodes.get(key),
                            NodeEntity.class);
                    result.add(node);
                }
                return success(result);
            } else {
                return error("role error");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error("getAllNode fail");
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jobSummary")
    @GET
    public String getJobSummary() {
        try {
            Map<String, Object> summary = new HashMap<String, Object>();
            int currentJob = RedisManager.getInstance().getJobCount();
            summary.put("currentJobCount", String.valueOf(currentJob));
            int consumeJob = RedisManager.getInstance().getConsumeJobList()
                    .size();
            summary.put("consumeJobCount", String.valueOf(consumeJob));
            int ackJob = RedisManager.getInstance().getAckJobSet().size();
            summary.put("ackJobCount", String.valueOf(ackJob));
            int readyJob = currentJob - consumeJob - ackJob;
            summary.put("readyJobCount", String.valueOf(readyJob));
            Long totalCount = EsManager.getInstance().getJobEntityCount();
            summary.put("totalCount", String.valueOf(totalCount));
            Set<String> products = new HashSet<String>();
            products = RedisManager.getInstance().getAllProduct();
            summary.put("products", products);
            return success(summary);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error("getJobSummary fail");
        }

    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getNodeAndCount")
    @GET
    public String getNodeAndCount(@QueryParam("role") String role) {
        try {
            Gson gson = new Gson();
            Map<String, Object> re = new HashMap<String, Object>();
            if (role != null
                    && (role.equals(Constants.NODE_ROLE_ADMIN)
                            || role.equals(Constants.NODE_ROLE_TASKTRACKER) || role
                                    .equals(Constants.NODE_ROLE_JOBTRACKER))) {
                Map<String, String> nodes = RedisManager.getInstance()
                        .getAllRoleNode(role);
                List<NodeEntity> result = new ArrayList<NodeEntity>();
                for (String key : nodes.keySet()) {
                    NodeEntity node = gson.fromJson(nodes.get(key),
                            NodeEntity.class);
                    result.add(node);
                }
                re.put("nodeDetails", result);
                re.put("nodeCount", result.size());
                return success(re);
            } else {
                return error("role error");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return error("getNodeAndCount fail");
        }
    }

}
