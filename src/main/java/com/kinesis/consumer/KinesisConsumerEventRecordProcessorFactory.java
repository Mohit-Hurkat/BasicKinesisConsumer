package com.kinesis.consumer;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;

public class KinesisConsumerEventRecordProcessorFactory implements IRecordProcessorFactory {

    public IRecordProcessor createProcessor(){
        return new KinesisConsumerEventRecordProcessor();
    }

}
