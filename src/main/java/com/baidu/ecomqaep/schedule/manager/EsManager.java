package com.baidu.ecomqaep.schedule.manager;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.baidu.ecomqaep.schedule.base.JobEntity;
import com.baidu.ecomqaep.schedule.config.Config;
import com.baidu.ecomqaep.schedule.config.Constants;
import com.google.gson.Gson;

public class EsManager {
    private static Log logger = LogFactory.getLog(EsManager.class);
    private static TransportClient client = null;
    private static final EsManager instance = new EsManager();

    public EsManager() {
        super();
        // TODO Auto-generated constructor stub
        initEs();
    }

    public static EsManager getInstance() {
        return EsManager.instance;
    }

    private static void initEs() {
        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", Config.esName)
                .put("client.transport.sniff", true).build();
        try {
            client = TransportClient.builder().settings(settings).build();
            String[] hosts = Config.esHost.split(",");
            String[] ports = Config.esPort.split(",");
            for (int i = 0; i < hosts.length; i++) {
                client.addTransportAddress(new InetSocketTransportAddress(
                        InetAddress.getByName(hosts[i]), Integer
                                .parseInt(ports[i])));
            }

        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void addJobEntity(JobEntity job) {
        try {
            Gson gson = new Gson();
            IndexResponse response = client
                    .prepareIndex(Constants.ES_INDEX, Constants.ES_TYPE_JOB,
                            job.getUuid())
                    .setSource(gson.toJson(job)).get();
            // System.out.println(response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public JobEntity getJobEntity(String uuid) {
        try {
            Gson gson = new Gson();
            GetResponse response = client.prepareGet(Constants.ES_INDEX,
                    Constants.ES_TYPE_JOB, uuid).get();
            return gson.fromJson(response.getSourceAsString(), JobEntity.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public Long getJobEntityCount() {
        try {
            Long response = client.prepareCount(Constants.ES_INDEX).get().getCount();
            return response;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static void createIndex() {
        try {
            Settings settings = Settings.settingsBuilder()
                    .put("number_of_shards", 5).put("number_of_replicas", 0)
                    .build();
            CreateIndexResponse indexresponse = client.admin().indices()
                    .prepareCreate(Constants.ES_INDEX).setSettings(settings)
                    // 这里直接添加type的mapping
                    .addMapping(Constants.ES_TYPE_JOB, getJobMapping())
                    .execute().actionGet();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static String getJobMapping() {
        return "{\"job\":{\"properties\":{\"product\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
                + "\"topic\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
                + "\"priority\":{\"type\":\"integer\"},\"uuid\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
                + "\"correlationId\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
                + "\"type\":{\"type\":\"string\",\"index\":\"not_analyzed\"},\"status\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
                + "\"sendTimestamp\":{\"type\":\"long\"},\"consumeTimestamp\":{\"type\":\"long\"},\"ackTimestamp\":{\"type\":\"long\"},"
                + "\"name\":{\"type\":\"string\",\"index\":\"not_analyzed\"},\"data\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
                + "\"expiration\":{\"type\":\"long\"},\"timeout\":{\"type\":\"long\"},\"sender\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
                + "\"consumer\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
                + "\"topicGroup\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
                + "\"consumeGroup\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
                + "\"info\":{\"type\":\"string\"},\"result\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
                + "\"bigResult\":{\"type\":\"string\",\"index\":\"not_analyzed\",\"ignore_above\":256},"
                + "\"isDeleted\":{\"type\":\"integer\"},\"retryCount\":{\"type\":\"integer\"}}}}";
    }

   

}
