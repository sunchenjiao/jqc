package com.baidu.ecomqaep.schedule.config;

public class Constants {

    /**
     * 常用数值
     */
    public static final String NULL_STRING = "null"; // 空值String
    public static final Integer EXPIRATION_ZERO = 0;
    public static final Integer REDIS_LIST_REM = 0;
    public static final Integer DELETED = 1;
    public static final String CONSUME_GROUP_ALL = "ALL"; // 特殊的机器分组，代表所有机器
    public static final Integer LIST_EMPTY = 0;
    public static final Integer PARAM_GROUP_BEGIN_INDEX = 3;
    public static final Integer DEFAULT_CONSUME_NUM = 1;
    public static final String ARRAY_BOUND_SYMBOL = ",";

    /**
     * Reids中key名称的连接符
     */
    public static final String BOUND_SYMBOL = "_";

    /**
     * 待分配队列名称前缀
     */
    public static final String LIST_READY_JOB_PREFIX = "list_readyJob";

    /**
     * 已分配Job的队列名
     */
    public static final String LIST_CONSUMED_JOB = "list_consumedJob";

    /**
     * 已确认Job的队列名
     */
    public static final String SET_ACK_JOB = "set_acknowledgedJob";

    /**
     * Redis中业务线集合名
     */
    public static final String SET_PRODUCT = "set_product";

    /**
     * Reids中Topic集合前缀
     */
    public static final String SET_TOPIC_PREFIX = "set_topic"; // 普通topic集合前缀
    public static final String SET_COMPETE_TOPIC_PREFIX = "set_competeTopic"; // 竞争topic集合前缀

    /**
     * Redis中Entity的名称
     */
    public static final String MAP_JOB_ENTITY = "map_jobEntity";
    public static final String MAP_NODE_ENTITY_PREFIX = "map_nodeEntity_";

    /**
     * Redis中订阅Topic群组相关集合
     */
    public static final String MAP_TOPIC_GROUP_PREFIX = "map_topicGroup"; // topicgroup群组名称前缀
    public static final String SET_TOPIC_GROUP_NAME_SET = "set_topicGroupNames";

    /**
     * Job的状态
     */
    public static final String JOB_STATUS_CONSUMED = "consumed";
    public static final String JOB_STATUS_ACK = "acknowledged";
    public static final String JOB_STATUS_READY = "ready";

    /**
     * Job类型
     */
    public static final String JOB_TYPE_PUBLISH = "PUBLISH";
    public static final String JOB_TYPE_POINT = "POINT";
    public static final String JOB_TYPE_COMPETE = "COMPETE";

    /**
     * Job的默认值
     */
    public static final Integer JOB_DEFAULT_PRIORITY = 1;
    public static final Long JOB_DEFAULT_EXPIRATION = 0L;
    public static final Long JOB_DEFAULT_TIMEOUT = 1000 * 300L;
    public static final Integer JOB_DEFAULT_RETRY_COUNT = 0;
    public static final String JOB_DEFAULT_SENDER = "unknown";
    public static final String JOB_DEFAULT_NAME = "defaultJob";
    public static final Integer JOB_DEFAULT_ISDELETED = 0;
    public static final String JOB_DEFAULT_RESULT_EXPIRED = "expired";
    public static final String JOB_DEFAULT_RESULT_TIMEOUT = "timeout";
    public static final String JOB_DEFAULT_RESULT_PUBLISH = "publish";

    /**
     * 消费消息的类型
     */
    public static final String CONSUME_TYPE_POINT = "POINT";
    public static final String CONSUME_TYPE_COMPETE = "COMPETE";

    /**
     * 优先级区域
     */
    public static final Integer PRIORITY_MIN = 0;
    public static final Integer PRIORITY_MAX = 4;

    /**
     * 节点Role
     */
    public static final String NODE_ROLE_TASKTRACKER = "TaskTracker";
    public static final String NODE_ROLE_JOBTRACKER = "JobTracker";
    public static final String NODE_ROLE_ADMIN = "Admin";

    /**
     * 节点状态
     */
    public static final String NODE_STATUS_REGISTERED = "registered";
    public static final String NODE_STATUS_ALIVE = "alive";

    /**
     * Action层返回
     */
    public static final String ACTION_RESULT_ERROR = "发送内部错误";
    public static final String ACTION_RESULT_FAIL = "失败";

    /**
     * 监管线程休眠间隔
     */
    public static final Integer ADMINTHREAD_INTERVAL = 60 * 1000;

    /**
     * JobTracker和TaskTracker掉线时限
     */
    public static final Integer JOBTRACKER_TIMEOUTLIMIT = 60 * 1000;
    public static final Integer TASKTRACKER_TIMEOUTLIMIT = 3 * 60 * 1000;

    /**
     * ElasticSearch参数
     */
    public static final String ES_INDEX = "ep-schedule";
    public static final String ES_TYPE_JOB = "job";

}
