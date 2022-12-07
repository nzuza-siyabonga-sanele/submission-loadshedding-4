package wethinkcode;


import kong.unirest.UnirestException;
import wethinkcode.loadshed.common.logger;
import wethinkcode.loadshed.spikes.QueueReceiver;
import wethinkcode.loadshed.spikes.QueueSender;

import java.util.logging.Level;

public class AlertQueue {

    private QueueReceiver queueReceiver;

    public AlertQueue() {}

    public void configureReceiver(){
        queueReceiver = new QueueReceiver();
        Thread receiveTask =new Thread( queueReceiver );
        receiveTask.start();
    }
    public void broadcastStageChangeEvent( String ctx ){
        logger log = new logger();
        log.logWEBSERVICE( Level.WARNING, ctx );
        QueueSender.main( new String[]{ctx} );
    }

    public QueueReceiver getQueueReceiver() {
        return queueReceiver;
    }
}
