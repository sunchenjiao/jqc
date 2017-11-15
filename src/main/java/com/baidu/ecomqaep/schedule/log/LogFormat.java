package com.baidu.ecomqaep.schedule.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogFormat {
    public enum IdKeys {
        nodeId, jobId, jobTrackerId, taskTrackerId
    }

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }

    private static String formatField(String key, String value) {
        return " [" + key + ":" + value + "] ";
    }

    public static void formatLog(Log log, LogLevel level, IdKeys[] idKeys,
            Object[] idValues, String functionName, String from, String to,
            String action, String data, String result, String info) {
        String logOut = "";
        logOut += "["
                + Thread.currentThread().getStackTrace()[2].getLineNumber()
                + "] ";
        if (idKeys != null && idValues != null
                && idKeys.length == idValues.length) {
            for (int i = 0; i < idKeys.length; i++) {
                if (idValues[i] != null) {
                    logOut += formatField(idKeys[i].toString(),
                            idValues[i].toString());
                }

            }
        }
        if (functionName != null) {
            logOut += formatField("functionName", functionName);
        }
        if (from != null) {
            logOut += formatField("from", from);
        }
        if (to != null) {
            logOut += formatField("to", to);
        }
        if (action != null) {
            logOut += formatField("action", action);
        }
        if (data != null) {
            logOut += formatField("data", data);
        }
        if (result != null) {
            logOut += formatField("result", result);
        }
        if (info != null) {
            logOut += formatField("info", info);
        }

        switch (level) {
            case DEBUG:
                log.debug(logOut);
                break;
            case INFO:
                log.info(logOut);
                break;
            case WARN:
                log.warn(logOut);
                break;
            case ERROR:
                log.error(logOut);
                break;

        }

    }

    public static void formatLog(Log log, LogLevel level, IdKeys[] idKeys,
            String[] idValues, String functionName, String from, String to,
            String action, String data, String result) {
        formatLog(log, level, idKeys, idValues, functionName, from, to, action,
                data, result, null);

    }

    public static void formatLog(Log log, IdKeys[] idKeys, String[] idValues,
            String functionName, String from, String to, String action,
            String eMessage, String result, String info) {
        formatLog(log, LogLevel.ERROR, idKeys, idValues, functionName, from,
                to, action, eMessage, result, info);

    }

    public static void formatLog(Log log, LogLevel level, IdKeys[] idKeys,
            String[] idValues, String functionName, String from, String to,
            String action, String data) {
        formatLog(log, level, idKeys, idValues, functionName, from, to, action,
                data, null, null);

    }

    public static void main(String[] args) {
        Log logger = LogFactory.getLog(LogFormat.class);
        formatLog(logger, LogLevel.INFO, new IdKeys[] { IdKeys.jobId,
                IdKeys.nodeId }, new String[] { "123", "456" }, "main", "from",
                "to", "test", "mayaming", "OK", null);

    }

}
