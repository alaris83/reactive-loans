package com.handywork.loanchecker.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Simple print service that produce output to desired location.
 */
@Component
public class ObjectWriter {

    // Use separate logger to control where print will be route to
    private static final Logger LOGGER = LoggerFactory.getLogger("print-service-log");

    /**
     * Print data object into console.
     *
     * @param object the object to be printed into console
     */
    public void printToConsole(Object object) {
        LOGGER.info(object.toString());
    }
}
