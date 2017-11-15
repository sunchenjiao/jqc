package com.baidu.ecomqaep.schedule.base;

import java.io.Serializable;
import java.util.List;

public class JobResultEntity extends JobEntity implements Serializable {
    private static final long serialVersionUID = 8654257637301683198L;
    private Long total;
    private Long readyCount;
    private Long consumedCount;
    private Long ackedCount;
    private List<JobEntity> details;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getReadyCount() {
        return readyCount;
    }

    public void setReadyCount(Long readyCount) {
        this.readyCount = readyCount;
    }

    public Long getConsumedCount() {
        return consumedCount;
    }

    public void setConsumedCount(Long consumedCount) {
        this.consumedCount = consumedCount;
    }

    public Long getAckedCount() {
        return ackedCount;
    }

    public void setAckedCount(Long ackedCount) {
        this.ackedCount = ackedCount;
    }

    public JobResultEntity transform2JobResult(JobEntity job) {
        JobResultEntity jobResult = new JobResultEntity();
        if (job.getConsumeGroup() != null) {
            jobResult.setConsumeGroup(job.getConsumeGroup());
        }
        if (job.getConsumer() != null) {
            jobResult.setConsumer(job.getConsumer());
        }
        if (job.getCorrelationId() != null) {
            jobResult.setCorrelationId(job.getCorrelationId());
        }
        if (job.getData() != null) {
            jobResult.setData(job.getData());
        }
        if (job.getExpiration() != null) {
            jobResult.setExpiration(job.getExpiration());
        }
        if (job.getAckTimestamp() != null) {
            jobResult.setAckTimestamp(job.getAckTimestamp());
        }
        if (job.getInfo() != null) {
            jobResult.setInfo(job.getInfo());
        }
        if (job.getIsDeleted() != null) {
            jobResult.setIsDeleted(job.getIsDeleted());
        }
        if (job.getName() != null) {
            jobResult.setName(job.getName());
        }
        if (job.getPriority() != null) {
            jobResult.setPriority(job.getPriority());
        }
        if (job.getProduct() != null) {
            jobResult.setProduct(job.getProduct());
        }
        if (job.getConsumeTimestamp() != null) {
            jobResult.setConsumeTimestamp(job.getConsumeTimestamp());
        }
        if (job.getResult() != null) {
            jobResult.setResult(job.getResult());
        }
        if (job.getBigResult() != null) {
            jobResult.setBigResult(job.getBigResult());
        }
        if (job.getSender() != null) {
            jobResult.setSender(job.getSender());
        }
        if (job.getSendTimestamp() != null) {
            jobResult.setSendTimestamp(job.getSendTimestamp());
        }
        if (job.getStatus() != null) {
            jobResult.setStatus(job.getStatus());
        }
        if (job.getTimeout() != null) {
            jobResult.setTimeout(job.getTimeout());
        }
        if (job.getOriTimeout() != null) {
            jobResult.setOriTimeout(job.getOriTimeout());
        }
        if (job.getTopic() != null) {
            jobResult.setTopic(job.getTopic());
        }
        if (job.getTopicGroup() != null) {
            jobResult.setTopicGroup(job.getTopicGroup());
        }
        if (job.getType() != null) {
            jobResult.setType(job.getType());
        }
        if (job.getUuid() != null) {
            jobResult.setUuid(job.getUuid());
        }
        if (job.getRetryCount() != null) {
            jobResult.setRetryCount(job.getRetryCount());
        }
        return jobResult;
    }

    public List<JobEntity> getDetails() {
        return details;
    }

    public void setDetails(List<JobEntity> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "JobResultEntity [total=" + total + ", readyCount=" + readyCount
                + ", consumedCount=" + consumedCount + ", ackedCount="
                + ackedCount + ", details=" + details + "]";
    }
}
