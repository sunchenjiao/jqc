package com.baidu.ecomqaep.schedule.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import com.baidu.ecomqaep.schedule.base.JobEntity;
import com.baidu.ecomqaep.schedule.base.NodeEntity;
import com.baidu.ecomqaep.schedule.base.TopicGroupEntity;
import com.baidu.ecomqaep.schedule.config.Config;
import com.baidu.ecomqaep.schedule.config.Constants;
import com.baidu.ecomqaep.schedule.log.LogFormat;
import com.baidu.ecomqaep.schedule.log.LogFormat.IdKeys;
import com.baidu.ecomqaep.schedule.log.LogFormat.LogLevel;
import com.google.gson.Gson;

public class RedisManager {
    private static Log log = LogFactory.getLog(RedisManager.class);
    private static JedisPool jedisPool = null;

    private static final RedisManager instance = new RedisManager();

    public RedisManager() {
        super();
        // TODO Auto-generated constructor stub
        initRedisPool();
    }

    public static RedisManager getInstance() {
        return RedisManager.instance;
    }

    protected static void initRedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 控制一个pool最多有多少个可用的的jedis实例
        jedisPoolConfig.setMaxTotal(1000);
        // 最大能够保持空闲状态的对象数
        jedisPoolConfig.setMaxIdle(300);
        // 超时时间
        jedisPoolConfig.setMaxWaitMillis(3 * 1000);
        if (Config.redisPass.equals(Constants.NULL_STRING)) {
            int database = 3;
            jedisPool = new JedisPool(jedisPoolConfig, Config.redisHost,
                    Config.redisPort, Protocol.DEFAULT_TIMEOUT, null, database);

        } else {
            jedisPool = new JedisPool(jedisPoolConfig, Config.redisHost,
                    Config.redisPort, Protocol.DEFAULT_TIMEOUT,
                    Config.redisPass);
        }

    }

    /**
     * Return jedis connection to the pool, call different return methods depends on the conectionBroken status.
     */
    protected static void closeResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public void clear() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.flushDB();
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
    }

    public boolean setNodeEntity(NodeEntity nodeEntity) {
        Jedis jedis = null;
        boolean result = false;
        try {
            LogFormat.formatLog(log, LogLevel.INFO,
                    new IdKeys[] { IdKeys.nodeId },
                    new String[] { nodeEntity.getIdentity() }, "setNodeEntity",
                    nodeEntity.getRole(), "redis", "set", null, null, null);
            jedis = jedisPool.getResource();
            Gson gson = new Gson();
            jedis.hset(Constants.MAP_NODE_ENTITY_PREFIX + nodeEntity.getRole(),
                    nodeEntity.getIdentity(), gson.toJson(nodeEntity));
            result = true;
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }

    public NodeEntity getNodeEntity(String identity, String role) {
        Jedis jedis = null;
        String node = null;
        NodeEntity result = null;
        Gson gson = new Gson();
        try {
            jedis = jedisPool.getResource();
            node = jedis
                    .hget(Constants.MAP_NODE_ENTITY_PREFIX + role, identity);
            if (node == null) {
                return result;
            }
            result = gson.fromJson(node, NodeEntity.class);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }

    public boolean setJobEntity(JobEntity jobEntity) {
        Jedis jedis = null;
        boolean result = false;
        try {
            LogFormat.formatLog(log, LogLevel.INFO,
                    new IdKeys[] { IdKeys.nodeId },
                    new String[] { jobEntity.getUuid() }, "setJobEntity",
                    "JobTracker", "redis", "set", jobEntity.toString(), null,
                    null);
            jedis = jedisPool.getResource();
            Gson gson = new Gson();
            jedis.hset(Constants.MAP_JOB_ENTITY, jobEntity.getUuid(),
                    gson.toJson(jobEntity));
            result = true;
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }

    public Set<String> getTopicGroups(String topicGroup, String product) {
        Jedis jedis = null;
        Set<String> result = new HashSet<String>();
        try {
            LogFormat.formatLog(log, LogLevel.INFO, null, null,
                    "getTopicGroups", "redis", "JobTracker", "get", topicGroup,
                    null, null);
            jedis = jedisPool.getResource();
            if (topicGroup == null) {
                return result;
            }
            String[] groups = topicGroup.split(",");
            for (String group : groups) {
                Set<String> topics = jedis
                        .hkeys(Constants.MAP_TOPIC_GROUP_PREFIX
                                + Constants.BOUND_SYMBOL + product
                                + Constants.BOUND_SYMBOL + group);
                result.addAll(topics);
            }
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }

    public boolean addPointJob(JobEntity jobEntity) {
        Jedis jedis = null;
        boolean result = false;
        try {
            LogFormat.formatLog(log, LogLevel.INFO,
                    new IdKeys[] { IdKeys.jobId },
                    new String[] { jobEntity.getUuid() }, "addPointJob",
                    "JobTracker", "redis", "set", jobEntity.toString(), null,
                    null);
            String listName = Constants.LIST_READY_JOB_PREFIX
                    + Constants.BOUND_SYMBOL + jobEntity.getProduct()
                    + Constants.BOUND_SYMBOL + jobEntity.getTopic()
                    + Constants.BOUND_SYMBOL + jobEntity.getPriority();
            jedis = jedisPool.getResource();
            Gson gson = new Gson();
            Transaction tx = jedis.multi();
            tx.sadd(Constants.SET_PRODUCT, jobEntity.getProduct());
            tx.sadd(Constants.SET_TOPIC_PREFIX + Constants.BOUND_SYMBOL
                    + jobEntity.getProduct(), listName);
            tx.hset(Constants.MAP_JOB_ENTITY, jobEntity.getUuid(),
                    gson.toJson(jobEntity));
            tx.lpush(listName, jobEntity.getUuid());
            tx.exec();
            result = true;
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }

    public boolean addCompeteJob(JobEntity jobEntity) {
        Jedis jedis = null;
        boolean result = false;
        Gson gson = new Gson();
        try {
            LogFormat.formatLog(log, LogLevel.INFO,
                    new IdKeys[] { IdKeys.jobId },
                    new String[] { jobEntity.getUuid() }, "addCompeteJob",
                    "JobTracker", "redis", "set", jobEntity.toString(), null,
                    null);
            String consumeGroup = jobEntity.getConsumeGroup();
            if (consumeGroup == null) {
                return false;
            }
            String[] groups = consumeGroup.split(",");
            String listName = Constants.LIST_READY_JOB_PREFIX
                    + Constants.BOUND_SYMBOL + jobEntity.getProduct()
                    + Constants.BOUND_SYMBOL + jobEntity.getTopic()
                    + Constants.BOUND_SYMBOL + jobEntity.getPriority();
            if (consumeGroup.toUpperCase().equals(Constants.CONSUME_GROUP_ALL)) {
                listName += Constants.BOUND_SYMBOL
                        + Constants.CONSUME_GROUP_ALL;
            } else {
                for (String group : groups) {
                    listName += Constants.BOUND_SYMBOL + group;
                }
            }
            jedis = jedisPool.getResource();
            Transaction tx = jedis.multi();
            tx.sadd(Constants.SET_PRODUCT, jobEntity.getProduct());
            tx.sadd(Constants.SET_COMPETE_TOPIC_PREFIX + Constants.BOUND_SYMBOL
                    + jobEntity.getProduct(), listName);
            tx.hset(Constants.MAP_JOB_ENTITY, jobEntity.getUuid(),
                    gson.toJson(jobEntity));
            tx.lpush(listName, jobEntity.getUuid());
            tx.exec();
            result = true;
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }

    public Set<String> getCompeteReadyListNames(String product) {
        Jedis jedis = null;
        Set<String> result = new HashSet<String>();
        try {
            LogFormat.formatLog(log, LogLevel.INFO, null, null,
                    "getCompeteReadyListNames", "redis", "JobTracker", "get",
                    product, null, null);
            jedis = jedisPool.getResource();
            result = jedis.smembers(Constants.SET_COMPETE_TOPIC_PREFIX
                    + Constants.BOUND_SYMBOL + product);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }

    public Set<String> getPointReadyListNames(String product) {
        Jedis jedis = null;
        Set<String> result = new HashSet<String>();
        try {
            LogFormat.formatLog(log, LogLevel.INFO, null, null,
                    "getPointReadyListNames", "redis", "JobTracker", "get",
                    product, null, null);
            jedis = jedisPool.getResource();
            result = jedis.smembers(Constants.SET_TOPIC_PREFIX
                    + Constants.BOUND_SYMBOL + product);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }

    public JobEntity consumeJob(String listName, String consumer) {
        Jedis jedis = null;
        JobEntity result = null;
        Gson gson = new Gson();
        try {
            jedis = jedisPool.getResource();
            String jobId = jedis.rpoplpush(listName,
                    Constants.LIST_CONSUMED_JOB);
            if (jobId == null) {
                return null;
            }
            String jobString = jedis.hget(Constants.MAP_JOB_ENTITY, jobId);
            JobEntity jobEntity = gson.fromJson(jobString, JobEntity.class);
            if (!jobEntity.getStatus().equals(Constants.JOB_STATUS_READY)) {               
                return jobEntity;
            }
            Date now = new Date();
            jobEntity.setConsumeTimestamp(now.getTime());
            jobEntity.setStatus(Constants.JOB_STATUS_CONSUMED);
            jobEntity.setConsumer(consumer);
            if (jobEntity.getExpiration() != null
                    && jobEntity.getExpiration() > Constants.EXPIRATION_ZERO) {
                if (now.getTime() - jobEntity.getSendTimestamp() > jobEntity
                        .getExpiration()) {
                    jobEntity.setIsDeleted(Constants.DELETED);
                }
            }
            jedis.hset(Constants.MAP_JOB_ENTITY, jobEntity.getUuid(),
                    gson.toJson(jobEntity));
            result = jobEntity;

        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }

    public Boolean ackJob(String jobId, String jobResult, String jobBigResult) {
        Jedis jedis = null;
        Boolean result = false;
        Gson gson = new Gson();
        try {
            jedis = jedisPool.getResource();
            String jobString = jedis.hget(Constants.MAP_JOB_ENTITY, jobId);
            if (jobString == null) {
                return false;
            }
            JobEntity jobEntity = gson.fromJson(jobString, JobEntity.class);
            if (jobEntity.getStatus().equals(Constants.JOB_STATUS_ACK)) {
                return false;
            }
            Date now = new Date();
            jobEntity.setAckTimestamp(now.getTime());
            jobEntity.setStatus(Constants.JOB_STATUS_ACK);
            if (jobBigResult != null) {
                jobEntity.setResult("look at bigResult");
                jobEntity.setBigResult(jobBigResult);
            } else {
                jobEntity.setResult(jobResult);
            }
            Transaction tx = jedis.multi();
            tx.hset(Constants.MAP_JOB_ENTITY, jobEntity.getUuid(),
                    gson.toJson(jobEntity));
            tx.sadd(Constants.SET_ACK_JOB, jobId);
            tx.lrem(Constants.LIST_CONSUMED_JOB, Constants.REDIS_LIST_REM,
                    jobId);
            tx.exec();
            result = true;
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }

    public Boolean subTopicGroup(TopicGroupEntity topicGroupEntity) {
        Jedis jedis = null;
        Boolean result = false;
        Gson gson = new Gson();
        try {
            String name = Constants.MAP_TOPIC_GROUP_PREFIX
                    + Constants.BOUND_SYMBOL + topicGroupEntity.getProduct()
                    + Constants.BOUND_SYMBOL + topicGroupEntity.getTopicGroup();
            jedis = jedisPool.getResource();
            jedis.hset(name, topicGroupEntity.getTopic(),
                    gson.toJson(topicGroupEntity));
            jedis.sadd(Constants.SET_TOPIC_GROUP_NAME_SET, name);
            result = true;
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }

    public Boolean cancelTopicGroup(TopicGroupEntity topicGroupEntity) {
        Jedis jedis = null;
        Boolean result = false;
        try {
            String name = Constants.MAP_TOPIC_GROUP_PREFIX
                    + Constants.BOUND_SYMBOL + topicGroupEntity.getProduct()
                    + Constants.BOUND_SYMBOL + topicGroupEntity.getTopicGroup();
            jedis = jedisPool.getResource();
            jedis.hdel(name, topicGroupEntity.getTopic());
            result = true;
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }

    public Set<String> getAllTopicGroupNames() {
        Jedis jedis = null;
        Set<String> result = new HashSet<String>();
        try {
            jedis = jedisPool.getResource();
            result = jedis.smembers(Constants.SET_TOPIC_GROUP_NAME_SET);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }

    public NodeEntity getTaskTrackerEntity(String identity) {
        Jedis jedis = null;
        NodeEntity result = null;
        Gson gson = new Gson();
        try {
            jedis = jedisPool.getResource();
            String node = jedis.hget(Constants.MAP_NODE_ENTITY_PREFIX
                    + Constants.NODE_ROLE_TASKTRACKER, identity);
            if (node == null) {
                return result;
            }
            result = gson.fromJson(node, NodeEntity.class);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }

    public JobEntity getJobEntity(String uuid) {
        if (uuid == null) {
            return null;
        }
        Jedis jedis = null;
        JobEntity result = null;
        Gson gson = new Gson();
        try {
            jedis = jedisPool.getResource();
            String jobString = jedis.hget(Constants.MAP_JOB_ENTITY, uuid);
            if (jobString == null) {
                return result;
            }
            result = gson.fromJson(jobString, JobEntity.class);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }

    public void deleteJobEntity(String jobId) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hdel(Constants.MAP_JOB_ENTITY, jobId);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
    }

    /**
     * 检查超时jobTracker和taskTracker，共用同一套逻辑。
     * 
     * @param role
     * @return
     */
    public Map<String, String> getAllRoleNode(String role) {
        Jedis jedis = null;
        Map<String, String> allTrackers = new HashMap<String, String>();
        allTrackers = null;
        try {
            jedis = jedisPool.getResource();
            allTrackers = jedis
                    .hgetAll(Constants.MAP_NODE_ENTITY_PREFIX + role);

        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return allTrackers;

    }

    public void deleteNodeEntity(String nodeRole, String nodeIdentity) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hdel(Constants.MAP_NODE_ENTITY_PREFIX + nodeRole,
                    nodeIdentity);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
    }

    public List<String> getConsumeJobList() {
        Jedis jedis = null;
        List<String> result = new ArrayList<String>();
        result = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.lrange(Constants.LIST_CONSUMED_JOB, 0, -1);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;

    }

    public Long deleteFromConsumeList(String jobId) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long re = jedis.lrem(Constants.LIST_CONSUMED_JOB,
                    Constants.REDIS_LIST_REM, jobId);
            return re;
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return -1L;
    }

    public void recycleJob(JobEntity jobEntity) {
        Jedis jedis = null;
        Gson gson = new Gson();
        try {
            jedis = jedisPool.getResource();
            Transaction tx = jedis.multi();
            jobEntity.setStatus(Constants.JOB_STATUS_READY);
            tx.hset(Constants.MAP_JOB_ENTITY, jobEntity.getUuid(),
                    gson.toJson(jobEntity));
            tx.lrem(Constants.LIST_CONSUMED_JOB, Constants.REDIS_LIST_REM,
                    jobEntity.getUuid());
            if (jobEntity.getType().equals(Constants.JOB_TYPE_COMPETE)) {
                String consumeGroup = jobEntity.getConsumeGroup();
                String[] groups = consumeGroup.split(",");
                String listName = Constants.LIST_READY_JOB_PREFIX
                        + Constants.BOUND_SYMBOL + jobEntity.getProduct()
                        + Constants.BOUND_SYMBOL + jobEntity.getTopic()
                        + Constants.BOUND_SYMBOL + jobEntity.getPriority();
                if (consumeGroup.toUpperCase().equals(
                        Constants.CONSUME_GROUP_ALL)) {
                    listName += Constants.BOUND_SYMBOL
                            + Constants.CONSUME_GROUP_ALL;
                } else {
                    for (String group : groups) {
                        listName += Constants.BOUND_SYMBOL + group;
                    }
                }
                tx.rpush(listName, jobEntity.getUuid());
            } else {
                String listName = Constants.LIST_READY_JOB_PREFIX
                        + Constants.BOUND_SYMBOL + jobEntity.getProduct()
                        + Constants.BOUND_SYMBOL + jobEntity.getTopic()
                        + Constants.BOUND_SYMBOL + jobEntity.getPriority();
                tx.rpush(listName, jobEntity.getUuid());
            }
            tx.exec();
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
    }

    public Set<String> getAckJobSet() {
        Jedis jedis = null;
        Set<String> result = new HashSet<String>();
        result = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.smembers(Constants.SET_ACK_JOB);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;

    }

    public boolean deleteAckJob(String uuid) {
        Jedis jedis = null;
        Boolean result = false;
        result = null;
        try {
            jedis = jedisPool.getResource();
            jedis.srem(Constants.SET_ACK_JOB, uuid);
            jedis.hdel(Constants.MAP_JOB_ENTITY, uuid);
            result = true;
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;

    }

    public Set<String> getAllProduct() {
        Jedis jedis = null;
        Set<String> allProduct = new HashSet<String>();
        allProduct = null;
        try {
            jedis = jedisPool.getResource();
            allProduct = jedis.smembers(Constants.SET_PRODUCT);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return allProduct;
    }

    public Long getReadyListJobCount(String listName) {
        Jedis jedis = null;
        Long result = 0L;
        try {
            jedis = jedisPool.getResource();
            result = jedis.llen(listName);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;

    }

    /**
     * 
     * @param listName
     * @param length
     * @return 返回length+1个元素
     */
    public List<String> getListRange(String listName, Integer length) {
        Jedis jedis = null;
        List<String> result = new ArrayList<String>();
        try {
            jedis = jedisPool.getResource();
            result = jedis.lrange(listName, 0, length);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;

    }

    public Boolean insertMap(String product, String key, String value) {
        Jedis jedis = null;
        Boolean result = false;
        try {
            jedis = jedisPool.getResource();
            String re = jedis.set(product + key, value);
            if (re.equals("OK")) {
                result = true;
            }
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }

        return result;
    }

    public String getMap(String product, String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.get(product + key);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }

        return result;
    }

    public Long deleteMap(String product, String key) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.del(product + key);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }

        return result;
    }

    public Long deleteKey(String key) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.del(key);
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }

        return result;
    }

    public int getJobCount() {
        int result = 0;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.hgetAll(Constants.MAP_JOB_ENTITY).size();
        } catch (JedisException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            closeResource(jedis);
        }
        return result;
    }
}
