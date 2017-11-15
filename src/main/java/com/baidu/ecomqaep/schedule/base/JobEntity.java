package com.baidu.ecomqaep.schedule.base;

import java.io.Serializable;

public class JobEntity implements Serializable {
    private static final long serialVersionUID = 3761932048032862100L;
    private String product;
    private String topic;
    private Integer priority;
    private String uuid;
    private String correlationId;
    private String type;
    private String status;
    private Long sendTimestamp;
    private Long consumeTimestamp;
    private Long ackTimestamp;
    private String name;
    private String data;
    private Long expiration;
    private Long timeout;
    private String sender;
    private String consumer;
    private String topicGroup;
    private String consumeGroup;
    private String info;
    private String result;
    private Integer retryCount;
    private Integer isDeleted;
    private Long oriTimeout;
    private String bigResult;

    public JobEntity clone() {
        JobEntity newJob = new JobEntity();
        newJob.setConsumeGroup(consumeGroup);
        newJob.setConsumer(consumer);
        newJob.setCorrelationId(correlationId);
        newJob.setData(data);
        newJob.setExpiration(expiration);
        newJob.setAckTimestamp(ackTimestamp);
        newJob.setInfo(info);
        newJob.setIsDeleted(isDeleted);
        newJob.setName(name);
        newJob.setPriority(priority);
        newJob.setProduct(product);
        newJob.setConsumeTimestamp(consumeTimestamp);
        newJob.setResult(result);
        newJob.setBigResult(bigResult);
        newJob.setSender(sender);
        newJob.setSendTimestamp(sendTimestamp);
        newJob.setStatus(status);
        newJob.setTimeout(timeout);
        newJob.setTopic(topic);
        newJob.setTopicGroup(topicGroup);
        newJob.setType(type);
        newJob.setUuid(uuid);
        newJob.setRetryCount(retryCount);
        newJob.setOriTimeout(oriTimeout);
        return newJob;
    }

    public String getBigResult() {
        return bigResult;
    }

    public void setBigResult(String bigResult) {
        this.bigResult = bigResult;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getSendTimestamp() {
        return sendTimestamp;
    }

    public void setSendTimestamp(Long sendTimestamp) {
        this.sendTimestamp = sendTimestamp;
    }

    public Long getConsumeTimestamp() {
        return consumeTimestamp;
    }

    public void setConsumeTimestamp(Long consumeTimestamp) {
        this.consumeTimestamp = consumeTimestamp;
    }

    public Long getAckTimestamp() {
        return ackTimestamp;
    }

    public void setAckTimestamp(Long ackTimestamp) {
        this.ackTimestamp = ackTimestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public String getTopicGroup() {
        return topicGroup;
    }

    public void setTopicGroup(String topicGroup) {
        this.topicGroup = topicGroup;
    }

    public String getConsumeGroup() {
        return consumeGroup;
    }

    public void setConsumeGroup(String consumeGroup) {
        this.consumeGroup = consumeGroup;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer failCount) {
        this.retryCount = failCount;
    }

    public Long getOriTimeout() {
        return oriTimeout;
    }

    public void setOriTimeout(Long oriTimeout) {
        this.oriTimeout = oriTimeout;
    }

    @Override
    public String toString() {
        return "JobEntity [product=" + product + ", topic=" + topic
                + ", priority=" + priority + ", uuid=" + uuid
                + ", correlationId=" + correlationId + ", type=" + type
                + ", status=" + status + ", sendTimestamp=" + sendTimestamp
                + ", consumeTimestamp=" + consumeTimestamp + ", ackTimestamp="
                + ackTimestamp + ", name=" + name + ", data=" + data
                + ", expiration=" + expiration + ", timeout=" + timeout
                + ", oriTimeout=" + oriTimeout + ", sender="
                + sender + ", consumer=" + consumer
                + ", topicGroup=" + topicGroup + ", consumeGroup="
                + consumeGroup + ", info=" + info + ", result=" + result
                + ", retryCount=" + retryCount + ", isDeleted=" + isDeleted
                + "]";
    }

}
