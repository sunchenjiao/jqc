package com.baidu.ecomqaep.schedule.manager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.ecomqaep.schedule.log.LogFormat;
import com.baidu.ecomqaep.schedule.base.NodeEntity;
import com.baidu.ecomqaep.schedule.log.LogFormat.IdKeys;
import com.baidu.ecomqaep.schedule.log.LogFormat.LogLevel;
import com.baidu.ecomqaep.schedule.config.Config;
import com.baidu.ecomqaep.schedule.util.IdUtil;
import com.baidu.ecomqaep.schedule.util.Md5Util;

public class NodeManager {
    private static Log log = LogFactory.getLog(NodeManager.class);
    private static String version = "0.0.1";

    public static NodeEntity jobTrackerRegister(String role) {
        NodeEntity nodeEntity = new NodeEntity();
        try {
            InetAddress netAddress = InetAddress.getLocalHost();
            nodeEntity.setHostname(netAddress.getHostName());
            nodeEntity.setIp(netAddress.getHostAddress());
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }
        nodeEntity.setIdentity(Config.identity);
        nodeEntity.setRole(role);
        nodeEntity.setVersion(version);
        nodeEntity.setStatus("Registered");
        nodeEntity.setLastRegisterTimestamp((new Date()).getTime());
        nodeEntity.setLastHeartbeatTimestamp((new Date()).getTime());
        if (RedisManager.getInstance().setNodeEntity(nodeEntity)) {
            return nodeEntity;
        } else {
            return null;
        }
    }

    public static String taskTrackerRegister(String identity, String passwd,
            NodeEntity nodeEntity) {
        try {
            String checkpasswd = agentIdentityEncrypt(identity);

            if (checkpasswd.equals(passwd)) {
                String token = IdUtil.getUUID();
                LogFormat.formatLog(log, LogLevel.INFO,
                        new IdKeys[] { IdKeys.nodeId },
                        new String[] { identity }, "TaskTrackerRegister",
                        "agent", "server", "Register", null, "success", null);
                nodeEntity.setToken(token);
                RedisManager.getInstance().setNodeEntity(nodeEntity);
                return token;
            } else {
                LogFormat.formatLog(log, LogLevel.INFO,
                        new IdKeys[] { IdKeys.nodeId },
                        new String[] { identity }, "TaskTrackerRegister",
                        "agent", "server", "Register", null, "fail",
                        "login fail: " + identity + "," + passwd + ","
                                + checkpasswd);
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            LogFormat.formatLog(log, LogLevel.ERROR,
                    new IdKeys[] { IdKeys.nodeId }, new String[] { identity },
                    "TaskTrackerRegister", "agent", "server", "Register",
                    e.getMessage(), "fail", null);
            return null;
        }
    }

    public static Boolean heartbeatUpdate(NodeEntity nodeEntity) {
        return RedisManager.getInstance().setNodeEntity(nodeEntity);
    }

    private static String agentIdentityEncrypt(String identity) {
        String md5One = Md5Util.MD5(identity);
        String md5Two = md5One.substring(md5One.length() / 2, md5One.length())
                + md5One.substring(0, md5One.length() / 2);
        String md5Three = Md5Util.MD5(md5Two);
        return md5Three.substring(0, 16);
    }

   

}
