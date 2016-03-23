package org.openmhealth.dpu.processor

import org.openmhealth.dpu.util.DataPoint
import org.openmhealth.dpu.util.EndUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component

@Component("blankProcessor")
public class BlankProcessor implements ItemProcessor<EndUser, DataPoint> {

    private static final Logger log = LoggerFactory.getLogger(BlankProcessor.class);

    @Override
    public DataPoint process(EndUser user) throws Exception {
        DataPoint result = new DataPoint();
        log.info("=== Processing the user now.")
        return result;
    }
}