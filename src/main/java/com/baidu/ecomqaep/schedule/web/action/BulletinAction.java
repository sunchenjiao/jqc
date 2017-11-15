package com.baidu.ecomqaep.schedule.web.action;

import java.util.Date;

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

import com.baidu.ecomqaep.schedule.base.JobEntity;
import com.baidu.ecomqaep.schedule.manager.RedisManager;

@Component
@Path("/bulletin")
public class BulletinAction extends BaseAction {
    private static Log log = LogFactory.getLog(BulletinAction.class);

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/addMap")
    public String insertMap(@FormParam("product") String product,
            @FormParam("key") String key, @FormParam("value") String value) {
        if (RedisManager.getInstance().insertMap(product, key, value)) {
            return success();
        } else {
            return error();
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getMap")
    @GET
    public String getMap(@QueryParam("product") String product,
            @QueryParam("key") String key) {
        String value = RedisManager.getInstance().getMap(product, key);
        if (value == null) {
            return error("no such key in Redis");
        } else {
            return success(value);
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/delMap")
    public String deleteMap(@FormParam("product") String product,
            @FormParam("key") String key) {
        Long re = RedisManager.getInstance().deleteMap(product, key);
        if (re != null) {
            return success();
        } else {
            return error();
        }
    }

}
