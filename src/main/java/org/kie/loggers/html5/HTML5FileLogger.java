package org.kie.loggers.html5;


import org.drools.core.WorkingMemory;
import org.drools.core.audit.WorkingMemoryLogger;
import org.drools.core.audit.event.*;
import org.kie.internal.event.KnowledgeRuntimeEventManager;
import org.kie.internal.logger.KnowledgeRuntimeLogger;
import org.kie.loggers.LogFileWriter;

public class HTML5FileLogger extends WorkingMemoryLogger {

    protected LogFileWriter html5FileWriter;

    private HTML5FileLogger(WorkingMemory workingMemory) {
        super(workingMemory);
        html5FileWriter = new HTML5FileWriter();
    }

    private HTML5FileLogger(KnowledgeRuntimeEventManager session) {
        super(session);
        html5FileWriter = new HTML5FileWriter();
    }

    /**
     * Sets the maximum number of log events that are allowed in memory. If this
     * number is reached, all events are written to file. The default is 1000.
     *
     * @param maxEventsInMemory
     *            The maximum number of events in memory.
     */
    public void setMaxEventsInMemory(final int maxEventsInMemory) {
        html5FileWriter.setMaxEventsInMemory(maxEventsInMemory);
    }

    /**
     * @see WorkingMemoryLogger
     */
    public void logEventCreated(final LogEvent logEvent) {
        html5FileWriter.logEventCreated(logEvent);
    }

    public void setFileName(String fileName) {
        html5FileWriter.setFileName(fileName);
    }

    public void start() {
        html5FileWriter.start();
    }

    public void stop() {
        html5FileWriter.stop();
    }

    public static KnowledgeRuntimeHTML5LoggerWrapper create(KnowledgeRuntimeEventManager session, String fileName, int interval) {
        HTML5FileLogger fileLogger = new HTML5FileLogger(session);
        fileLogger.setFileName(fileName);
        fileLogger.setMaxEventsInMemory(interval);
        fileLogger.start();
        return new KnowledgeRuntimeHTML5LoggerWrapper(fileLogger);
    }

    public static KnowledgeRuntimeHTML5LoggerWrapper create(KnowledgeRuntimeEventManager session, String fileName) {
        return create(session, fileName, 1000);
    }

    private static class KnowledgeRuntimeHTML5LoggerWrapper
            implements
            KnowledgeRuntimeLogger {

        private HTML5FileLogger logger;

        public KnowledgeRuntimeHTML5LoggerWrapper(HTML5FileLogger logger) {
            this.logger = logger;
        }

        public void close() {
            logger.stop();
        }

    }
}
