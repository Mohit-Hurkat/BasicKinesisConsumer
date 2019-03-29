package com.kinesis.consumer;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.metrics.impl.NullMetricsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Security;


public class KinesisConsumer {

    private static Logger logger = LoggerFactory.getLogger(KinesisConsumer.class);
    private static final String NETWORK_CACHE_TTL = "networkaddress.cache.ttl";
    private static final String DEFAULT_NETWORK_CACHE_TTL = "60";
    private static final InitialPositionInStream STREAM_LATEST = InitialPositionInStream.LATEST;



    public KinesisConsumer() {

        String streamName = "streamName";
        String applicationName = "applicationName";
        String region = "region";
        String accessKey = "accessKey";
        String secretKey = "secretKey";
        String workerId = "workerId-0001";

        try {
            Security.setProperty(NETWORK_CACHE_TTL, DEFAULT_NETWORK_CACHE_TTL);
            BasicAWSCredentials awsCreds = new BasicAWSCredentials
                    (accessKey, secretKey);
            KinesisClientLibConfiguration clientLibConfiguration =
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
}
