package wethinkcode.loadshed.common;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.io.IOException;
import java.util.logging.*;

public class logger {
    private static Logger PLACENAME_LOGR = Logger.getLogger("loadshed.placename");
    private static Logger SCHEDULE_LOGR = Logger.getLogger("loadshed.schedule");
    private static Logger STAGE_LOGR = Logger.getLogger("loadshed.stage");
    private static Logger WEBSERVICE_LOGR = Logger.getLogger("loadshed.webservice");

    public logger() {
        setupLogger();
    }

    public static void logPLACENAME(Level level, String msg, Throwable thrown) throws IOException {
        final HttpResponse<JsonNode> post = Unirest.post( "https://ntfy.sh/AlertWebServer" )
                .header( "Content-Type", "application/json" )
                .body( msg)
                .asJson();
        PLACENAME_LOGR.log(level,msg,thrown);
    }

    public static void logSCHEDULE(Level level, String msg, Throwable thrown){
        SCHEDULE_LOGR.log(level,msg,thrown);
    }

    public static void logSTAGE(Level level, String msg, Throwable thrown){
        STAGE_LOGR.log(level,msg,thrown);
    }

    public static void logWEBSERVICE(Level level, String msg){
        final HttpResponse<JsonNode> post = Unirest.post( "https://ntfy.sh/AlertWebServer" )
                .header( "Content-Type", "application/json" )
                .body( msg)
                .asJson();
        WEBSERVICE_LOGR.info(msg);
//        WEBSERVICE_LOGR.log(level,msg,thrown);
    }

    private static void setupLogger() {
        LogManager.getLogManager().reset();
        PLACENAME_LOGR.setLevel(Level.ALL);
        SCHEDULE_LOGR.setLevel(Level.ALL);
        STAGE_LOGR.setLevel(Level.ALL);
        WEBSERVICE_LOGR.setLevel(Level.ALL);

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        PLACENAME_LOGR.addHandler(ch);
        SCHEDULE_LOGR.addHandler(ch);
        STAGE_LOGR.addHandler(ch);
        WEBSERVICE_LOGR.addHandler(ch);

        try {
            FileHandler fh = new FileHandler("/home/wtc_laptop/submission-loadshedding-4/FileLogger.log", true);
            fh.setFormatter(new SimpleFormatter());
            fh.setLevel(Level.ALL);
            PLACENAME_LOGR.addHandler(fh);
            SCHEDULE_LOGR.addHandler(fh);
            STAGE_LOGR.addHandler(fh);
            WEBSERVICE_LOGR.addHandler(fh);
        } catch (IOException e) {
            PLACENAME_LOGR.log(Level.SEVERE, "File logger not working.", e);
        }
    }
}
