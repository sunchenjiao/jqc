package com.baidu.ecomqaep.schedule.manager;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.ecomqaep.schedule.base.TopicGroupEntity;

public class TopicManager {
    private static Log log = LogFactory.getLog(TopicManager.class);

    public static Boolean subTopicGroup(TopicGroupEntity topicGroupEntity) {
        return RedisManager.getInstance().subTopicGroup(topicGroupEntity);
    }

    public static Boolean cancelTopicGroup(TopicGroupEntity topicGroupEntity) {
        return RedisManager.getInstance().cancelTopicGroup(topicGroupEntity);
    }

    public static Set<String> getAllTopicGroupNames() {
        return RedisManager.getInstance().getAllTopicGroupNames();
    }

}
