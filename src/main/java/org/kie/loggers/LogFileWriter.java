package org.kie.loggers;

import org.drools.core.audit.event.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class LogFileWriter {
    protected  static transient Logger logger = LoggerFactory.getLogger(LogFileWriter.class);
    protected  final List<LogEvent> events            = new ArrayList<LogEvent>();
    protected  int          interval = 1000;
    protected  String       fileName          = "event";
    protected  int          maxEventsInMemory = 1000;
    protected  Writer       writer;
    protected  boolean      terminate;

    public void logEventCreated(final LogEvent logEvent) {
        synchronized ( this.events ) {
            this.events.add( logEvent );
            if ( this.events.size() > this.maxEventsInMemory ) {
                writeToDisk();
            }
        }
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        String extension = getFileExtension();
        if ( !this.fileName.endsWith(extension) ) {
            this.fileName += extension;
        }
    }

    public void start() {
        writer = new Writer();
        new Thread( writer ).start();
    }

    public void stop() {
        if ( terminate ) {
            throw new IllegalStateException( "Logger has already been closed." );
        }
        terminate = true;
        writeToDisk();
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * Sets the maximum number of log events that are allowed in memory. If this
     * number is reached, all events are written to file. The default is 1000.
     *
     * @param maxEventsInMemory
     *            The maximum number of events in memory.
     */
    public void setMaxEventsInMemory(final int maxEventsInMemory) {
        this.maxEventsInMemory = maxEventsInMemory;
    }

    public abstract String getFileExtension();
    public abstract void writeToDisk();

    private class Writer
            implements
            Runnable {
        private boolean interrupt = false;

        public void run() {
            while ( !interrupt ) {
                try {
                    Thread.sleep( interval );
                } catch ( Throwable t ) {
                    // do nothing
                }
                writeToDisk();
            }
        }

        public void interrupt() {
            this.interrupt = true;
        }
    }
}
