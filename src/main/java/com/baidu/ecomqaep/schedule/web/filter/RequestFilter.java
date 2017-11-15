package com.baidu.ecomqaep.schedule.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

@Component
public class RequestFilter implements ContainerRequestFilter {
    protected final static Logger logger = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    public ContainerRequest filter(ContainerRequest arg0) {
        String apiRecord = String.format("%s%10s", arg0.getAbsolutePath().toString(), "[" + arg0.getMethod() + "]");
        logger.info(apiRecord);
        return arg0;
    }
}
