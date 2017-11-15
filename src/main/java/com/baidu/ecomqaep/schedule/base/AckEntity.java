package com.baidu.ecomqaep.schedule.base;

import java.io.Serializable;

public class AckEntity implements Serializable {
    private static final long serialVersionUID = 5731576048032862100L;

    private String result;
    private String uuid;
    private String bigResult;

    public String getBigResult() {
        return bigResult;
    }

    public void setBigResult(String bigResult) {
        this.bigResult = bigResult;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bigResult == null) ? 0 : bigResult.hashCode());
        result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
        AckEntity other = (AckEntity) obj;
        if (bigResult == null) {
            if (other.bigResult != null)
                return false;
        } else if (!bigResult.equals(other.bigResult))
            return false;
        if (result == null) {
            if (other.result != null)
                return false;
        } else if (!result.equals(other.result))
            return false;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AckEntity [result=" + result + ", uuid=" + uuid + ", bigResult=" + bigResult + "]";
    }

}
