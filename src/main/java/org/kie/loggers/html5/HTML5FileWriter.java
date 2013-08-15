package org.kie.loggers.html5;

import org.drools.core.audit.event.LogEvent;
import org.drools.core.util.IoUtils;
import org.kie.loggers.Event;
import org.kie.loggers.EventSortUtil;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class HTML5FileWriter extends org.kie.loggers.LogFileWriter {

    @Override
    public String getFileExtension() {
        return ".html";
    }

    @Override
    public synchronized void writeToDisk() {

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter( this.fileName, false );
            fileWriter.append("<!DOCTYPE html>\n");
            fileWriter.append( "<html>\n" );
            fileWriter.append(readHeaderStyles());
            fileWriter.append( "<body>\n" );
            List<LogEvent> eventsToWrite;
            synchronized ( this.events ) {
                eventsToWrite = new ArrayList<LogEvent>( this.events );
            }
            writeContent(fileWriter, eventsToWrite);
            fileWriter.append("</body>\n");
            fileWriter.append("</html>\n");

        } catch ( final FileNotFoundException exc ) {
            throw new RuntimeException( "Could not create the log file.  Please make sure that directory that the log file should be placed in does exist." );
        } catch ( final Throwable t ) {
            logger.error("error", t);
        } finally {
            if ( fileWriter != null ) {
                try {
                    fileWriter.close();
                } catch ( Exception e ) {
                    //ignore
                }
            }
        }
    }

    private void writeContent(FileWriter fileWriter, List<LogEvent> events) throws IOException {
        List<Event> organizedEvents = EventSortUtil.createEventList(events);
        writeEvent(fileWriter, organizedEvents, 0);
    }

    private void writeEvent(FileWriter fileWriter, List<Event> eventsList, int indent) throws IOException {
        for (Event event : eventsList) {
            String eventAsString = event.toString();
            fileWriter.write("<details open>\n");
            fileWriter.write("<summary style=\"text-indent: "+indent+"px;padding-top:5px;padding-bottom:5px;\">\n");
            writeStyleString(fileWriter, eventAsString);
            fileWriter.append(eventAsString).append("\n");
            fileWriter.write("</summary>\n");
            if ( event.hasSubEvents() ) {
                writeEvent(fileWriter, event.getSubEvents(), indent+20);
            }
            fileWriter.write("</details>\n");
        }
    }

    private void writeStyleString(FileWriter fileWriter, String  textToWrite) throws IOException {
        if ( textToWrite.startsWith("Object inserted")) {
            fileWriter.append("<div id=\"green\">&nbsp;</div>&nbsp;");
        } else if (textToWrite.startsWith("Activation executed:")){
            fileWriter.append("<div id=\"orange\">&nbsp;&nbsp;</div>&nbsp;");
        } else if (textToWrite.startsWith("Activation created:")){
            fileWriter.append("<div id=\"blue\">&nbsp;&nbsp;</div>&nbsp;");
        }
    }
    static String headerStylesStr = null;
    protected String readHeaderStyles() throws IOException{
        if ( headerStylesStr == null) {
            byte[] bytes = IoUtils.readBytesFromInputStream(HTML5FileWriter.class.getResourceAsStream("/org/kie/loggers/html5/html-head.template.txt"));
            headerStylesStr = new String(bytes);
        }
        return headerStylesStr;
    }

}
