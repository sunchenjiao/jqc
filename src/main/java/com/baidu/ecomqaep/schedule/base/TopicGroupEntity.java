package com.baidu.ecomqaep.schedule.base;

import java.io.Serializable;

public class TopicGroupEntity implements Serializable {
    private static final long serialVersionUID = 5731576048032862190L;

    private String topicGroup;
    private String product;
    private String topic;
    private String operator;
    private Long timestamp;

    public String getTopicGroup() {
        return topicGroup;
    }

    public void setTopicGroup(String topicGroup) {
        this.topicGroup = topicGroup;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((operator == null) ? 0 : operator.hashCode());
        result = prime * result + ((product == null) ? 0 : product.hashCode());
        result = prime * result
                + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        result = prime * result
                + ((topicGroup == null) ? 0 : topicGroup.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TopicGroupEntity other = (TopicGroupEntity) obj;
        if (operator == null) {
            if (other.operator != null)
                return false;
        } else if (!operator.equals(other.operator))
            return false;
        if (product == null) {
            if (other.product != null)
                return false;
        } else if (!product.equals(other.product))
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        if (topic == null) {
            if (other.topic != null)
                return false;
        } else if (!topic.equals(other.topic))
            return false;
        if (topicGroup == null) {
            if (other.topicGroup != null)
                return false;
        } else if (!topicGroup.equals(other.topicGroup))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TopicGroupEntity [topicGroup=" + topicGroup + ", product="
                + product + ", topic=" + topic + ", operator=" + operator
                + ", timestamp=" + timestamp + "]";
    }

}
