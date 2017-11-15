package com.baidu.ecomqaep.schedule.base;

import java.io.Serializable;

public class ConsumeEntity implements Serializable {
    private static final long serialVersionUID = 5731912048032862100L;
    private String topic;
    private String consumer;
    private String consumeType;
    private String product;
    private String consumeGroup;
    private Integer number;
    private Boolean isExclusive;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Boolean getIsExclusive() {
        return isExclusive;
    }

    public void setIsExclusive(Boolean isExclusive) {
        this.isExclusive = isExclusive;
    }

    public String getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(String consumeType) {
        this.consumeType = consumeType;
    }

    @Override
    public String toString() {
        return "ConsumeEntity [topic=" + topic + ", consumer=" + consumer
                + ", consumeType=" + consumeType + ", product=" + product
                + ", consumeGroup=" + consumeGroup + ", number=" + number
                + ", isExclusive=" + isExclusive + "]";
    }

}
