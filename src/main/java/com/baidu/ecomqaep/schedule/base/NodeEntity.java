package com.baidu.ecomqaep.schedule.base;

import java.io.Serializable;

public class NodeEntity implements Serializable {
    private static final long serialVersionUID = 1761912048032862100L;
    private String identity;
    private String passwd;
    private String token;
    private String version;
    private String role;
    private String group;
    private String info;
    private String status;
    private String ip;
    private String hostname;
    private Long lastRegisterTimestamp;
    private Long lastHeartbeatTimestamp;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Long getLastRegisterTimestamp() {
        return lastRegisterTimestamp;
    }

    public void setLastRegisterTimestamp(Long lastRegisterTimestamp) {
        this.lastRegisterTimestamp = lastRegisterTimestamp;
    }

    public Long getLastHeartbeatTimestamp() {
        return lastHeartbeatTimestamp;
    }

    public void setLastHeartbeatTimestamp(Long lastHeartbeatTimestamp) {
        this.lastHeartbeatTimestamp = lastHeartbeatTimestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((group == null) ? 0 : group.hashCode());
        result = prime * result
                + ((hostname == null) ? 0 : hostname.hashCode());
        result = prime * result
                + ((identity == null) ? 0 : identity.hashCode());
        result = prime * result + ((info == null) ? 0 : info.hashCode());
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime
                * result
                + ((lastHeartbeatTimestamp == null) ? 0
                        : lastHeartbeatTimestamp.hashCode());
        result = prime
                * result
                + ((lastRegisterTimestamp == null) ? 0 : lastRegisterTimestamp
                        .hashCode());
        result = prime * result + ((passwd == null) ? 0 : passwd.hashCode());
        result = prime * result + ((role == null) ? 0 : role.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
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
        NodeEntity other = (NodeEntity) obj;
        if (group == null) {
            if (other.group != null)
                return false;
        } else if (!group.equals(other.group))
            return false;
        if (hostname == null) {
            if (other.hostname != null)
                return false;
        } else if (!hostname.equals(other.hostname))
            return false;
        if (identity == null) {
            if (other.identity != null)
                return false;
        } else if (!identity.equals(other.identity))
            return false;
        if (info == null) {
            if (other.info != null)
                return false;
        } else if (!info.equals(other.info))
            return false;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (lastHeartbeatTimestamp == null) {
            if (other.lastHeartbeatTimestamp != null)
                return false;
        } else if (!lastHeartbeatTimestamp.equals(other.lastHeartbeatTimestamp))
            return false;
        if (lastRegisterTimestamp == null) {
            if (other.lastRegisterTimestamp != null)
                return false;
        } else if (!lastRegisterTimestamp.equals(other.lastRegisterTimestamp))
            return false;
        if (passwd == null) {
            if (other.passwd != null)
                return false;
        } else if (!passwd.equals(other.passwd))
            return false;
        if (role == null) {
            if (other.role != null)
                return false;
        } else if (!role.equals(other.role))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (token == null) {
            if (other.token != null)
                return false;
        } else if (!token.equals(other.token))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "NodeEntity [identity=" + identity + ", passwd=" + passwd
                + ", token=" + token + ", version=" + version + ", role="
                + role + ", group=" + group + ", info=" + info + ", status="
                + status + ", ip=" + ip + ", hostname=" + hostname
                + ", lastRegisterTimestamp=" + lastRegisterTimestamp
                + ", lastHeartbeatTimestamp=" + lastHeartbeatTimestamp + "]";
    }

}
