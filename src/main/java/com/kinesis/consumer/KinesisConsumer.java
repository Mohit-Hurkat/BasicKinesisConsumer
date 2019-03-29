package com.kinesis.consumer;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.metrics.impl.NullMetricsFactory;
import com.kinesis.server.ServiceLocatorFactory;
import com.kinesis.service.LoadPropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Security;

public class KinesisConsumer {

    @Autowired
    private LoadPropertyService loadPropertyService;

    private static Logger logger = LoggerFactory.getLogger(KinesisConsumer.class);
    private static final String NETWORK_CACHE_TTL = "networkaddress.cache.ttl";
    private static final String DEFAULT_NETWORK_CACHE_TTL = "60";
    private static final InitialPositionInStream STREAM_LATEST = InitialPositionInStream.LATEST;
    private String applicationName;
    private String streamName;
    private String region;
    private String accessKey;
    private String secretKey;


    public KinesisConsumer() {

        KinesisClientLibConfiguration clientLibConfiguration = null;
        try {
            streamName = (String) getLoadPropertyService().getProperty("streamNameConsumer");
            applicationName = (String) getLoadPropertyService().getProperty("applicationName");
            region = (String) getLoadPropertyService().getProperty("region");
            accessKey = (String) getLoadPropertyService().getProperty("accessKey");
            secretKey = (String) getLoadPropertyService().getProperty("secretKey");
            String workerId = (String) getLoadPropertyService().getProperty("workerId");
            Security.setProperty(NETWORK_CACHE_TTL, DEFAULT_NETWORK_CACHE_TTL);
            BasicAWSCredentials awsCreds = new BasicAWSCredentials
                    (accessKey, secretKey);
            clientLibConfiguration =
                    new KinesisClientLibConfiguration(applicationName + streamName,
                            streamName,
                            new AWSStaticCredentialsProvider(awsCreds),
                            workerId);

            clientLibConfiguration.withInitialPositionInStream(STREAM_LATEST);
            IRecordProcessorFactory recordProcessorFactory = new KinesisConsumerEventRecordProcessorFactory();
            clientLibConfiguration.withRegionName(region);
            Worker worker = new Worker.Builder()
                    .recordProcessorFactory(recordProcessorFactory)
                    .config(clientLibConfiguration)
                    .metricsFactory(new NullMetricsFactory())
                    .build();
            new Thread(() -> {
                logger.info("Initailizing kinesisWorker");
                worker.run();
            }).start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("Failed to create configuration.", e);
            e.printStackTrace();
        }
    }

    private LoadPropertyService getLoadPropertyService() {
        if (loadPropertyService == null) {
            loadPropertyService = ServiceLocatorFactory.getService(LoadPropertyService.class);
        }
        return loadPropertyService;
    }
}
