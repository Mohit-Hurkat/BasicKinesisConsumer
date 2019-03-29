package com.kinesis.consumer;

import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ThrottlingException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.ShutdownReason;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import java.util.List;

public class KinesisConsumerEventRecordProcessor implements IRecordProcessor {

    private static Logger logger = LoggerFactory.getLogger(KinesisConsumerEventRecordProcessor.class);
    private String kinesisShardId;

    private static final long BACKOFF_TIME_IN_MILLIS = 3000L;
    private static final int NUM_RETRIES = 10;

    @Override
    public void initialize(InitializationInput initializationInput) {
        logger.info("Initializing record processor for shard: {}", initializationInput.getShardId());
        this.kinesisShardId = initializationInput.getShardId();
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        List<Record> records = processRecordsInput.getRecords();
        if (records != null) {

            logger.info("Processing " + records.size() + " records from " + kinesisShardId);

            // Process records and perform all exception handling.
            processRecordsWithRetries(records);

            // Checkpoint once every checkpoint interval.
            checkpoint(processRecordsInput.getCheckpointer());
        }
    }

    private void processRecordsWithRetries(List<Record> records) {
        for (Record record : records) {
            boolean processedSuccessfully = false;
            for (int i = 0; i < NUM_RETRIES; i++) {
                try {
                    //
                    // Logic to process record goes here.
                    //
                    processSingleRecord(record);

                    processedSuccessfully = true;
                    break;
                } catch (Throwable t) {
                    logger.warn("Caught throwable while processing record " + record, t);
                }

                // backoff if we encounter an exception.
                try {
                    Thread.sleep(BACKOFF_TIME_IN_MILLIS);
                } catch (InterruptedException e) {
                    logger.debug("Interrupted sleep", e);
                }
            }

            if (!processedSuccessfully) {
                logger.error("Couldn't process record " + record + ". Skipping the record.");
            }
        }
    }

    /**
     * Process a single record.
     *
     * @param record The record to be processed.
     */
    private void processSingleRecord(Record record) {
        try {
            Object data = SerializationUtils.deserialize(record.getData().array());
            if (data != null) {
                logger.info("Data Fetched data: " + data.toString());
            }
        } catch (Exception e) {
            logger.info("Exception while mapping Json string to Object", e);
            return;
        }

        logger.info("inside process single record");
    }

    @Override
    public void shutdown(ShutdownInput shutdownInput) {
        logger.info("Shutting down record processor for shard: {}", kinesisShardId);
        // Important to checkpoint after reaching end of shard, so we can start processing data from child shards.
        if (shutdownInput.getShutdownReason() == ShutdownReason.TERMINATE) {
            checkpoint(shutdownInput.getCheckpointer());
        }
    }


    private void checkpoint(IRecordProcessorCheckpointer checkpointer) {
        logger.info("Checkpointing shard " + kinesisShardId);
        for (int i = 0; i < NUM_RETRIES; i++) {
            try {
                checkpointer.checkpoint();
                break;
            } catch (ShutdownException se) {
                // Ignore checkpoint if the processor instance has been shutdown (fail over).
                logger.info("Caught shutdown exception, skipping checkpoint.", se);
                break;
            } catch (ThrottlingException e) {
                // Backoff and re-attempt checkpoint upon transient failures
                if (i >= (NUM_RETRIES - 1)) {
                    logger.error("Checkpoint failed after " + (i + 1) + "attempts.", e);
                    break;
                } else {
                    logger.info("Transient issue when checkpointing - attempt " + (i + 1) + " of "
                            + NUM_RETRIES, e);
                }
            } catch (InvalidStateException e) {
                // This indicates an issue with the DynamoDB table (check for table, provisioned IOPS).
                logger.error("Cannot save checkpoint to the DynamoDB table used by the Amazon Kinesis Client Library.", e);
                break;
            }
            try {
                Thread.sleep(BACKOFF_TIME_IN_MILLIS);
            } catch (InterruptedException e) {
                logger.debug("Interrupted sleep", e);
            }
        }
    }
}
