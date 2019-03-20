package com.handywork.loanchecker.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Simple print service that produce output to desired location.
 */
@Component
public class ObjectWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger("print-service-log");

    /**
     * Print data objet into console.
     *
     * @param object the object to print
     */
    public void printToConsole(Object object) {
        LOGGER.info(object.toString());
    }
}
