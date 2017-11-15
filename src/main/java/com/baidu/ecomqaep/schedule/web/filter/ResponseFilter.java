package com.baidu.ecomqaep.schedule.web.filter;

import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import net.sf.json.JSONObject;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class ResponseFilter implements ContainerResponseFilter {

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        try {
            JSONObject jsonObject = JSONObject.fromObject(response.getEntity());
            if (jsonObject.containsKey("status")) {
                final int status = jsonObject.getInt("status");
                StatusType st = new StatusType() {
                    @Override
                    public int getStatusCode() {
                        return status;
                    }

                    @Override
                    public String getReasonPhrase() {
                        return null;
                    }

                    @Override
                    public Family getFamily() {
                        return null;
                    }
                };
                response.setStatusType(st);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return response;
    }

}
