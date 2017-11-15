package com.baidu.ecomqaep.schedule.base;

import java.io.Serializable;
import java.util.List;

public class TaskTracker2JobTrackerModel implements Serializable {
    private static final long serialVersionUID = 123456L;

    private String identity;
    private String passwd;
    private String token;
    private String version;
    private String signal;
    private String product;
    private String consumeGroup;
    private Boolean isExclusive;
    private String topic;
    private String consumeType;
    private Integer consumeNum;
    private String ip;
    private String hostname;
    private List<JobEntity> ackJobs;
    private String info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getConsumeGroup() {
        return consumeGroup;
    }

    public void setConsumeGroup(String consumeGroup) {
        this.consumeGroup = consumeGroup;
    }

    public Boolean getIsExclusive() {
        return isExclusive;
    }

    public void setIsExclusive(Boolean isExclusive) {
        this.isExclusive = isExclusive;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(String consumeType) {
        this.consumeType = consumeType;
    }

    public Integer getConsumeNum() {
        return consumeNum;
    }

    public void setConsumeNum(Integer consumeNum) {
        this.consumeNum = consumeNum;
    }

    public List<JobEntity> getAckJobs() {
        return ackJobs;
    }

    public void setAckJobs(List<JobEntity> ackJobs) {
        this.ackJobs = ackJobs;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    @Override
    public String toString() {
        return "TaskTracker2JobTrackerModel [identity=" + identity
                + ", passwd=" + passwd + ", token=" + token + ", version="
                + version + ", signal=" + signal + ", product=" + product
                + ", consumeGroup=" + consumeGroup + ", isExclusive="
                + isExclusive + ", topic=" + topic + ", consumeType="
                + consumeType + ", consumeNum=" + consumeNum + ", ip=" + ip
                + ", hostname=" + hostname + ", ackJobs=" + ackJobs + ", info="
                + info + "]";
    }

}
