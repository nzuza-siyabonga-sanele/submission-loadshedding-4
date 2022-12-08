package wethinkcode.web;

import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.*;
import wethinkcode.loadshed.common.transfer.StageDO;
import wethinkcode.schedule.ScheduleService;
import wethinkcode.stage.StageService;

import javax.jms.*;
import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag( "expensive" )
class WebServiceMQTest {

    public static final int TEST_PORT = 7777;

    private static WebService server;

    private static ActiveMQConnectionFactory factory;

    private static Connection mqConnection;

    @BeforeAll
    public static void startInfrastructure() throws JMSException {
        startMsgQueue();
        startScheduleSvc();
    }

    @AfterAll
    public static void cleanup(){
        server.stop();
//        mqConnection.close();
    }

    @AfterEach
    public void closeMqConnection() throws JMSException {
        server.scheduleService.stopMQMachinery();
        server.stopMQMachinery();
    }

    @Test
    public void sendMqEventWhenPlaceNameServerDown() throws JMSException {

        final ArrayList<String> eventLister = new ArrayList<>();

        server.stopPlaceNameService();
        final HttpResponse<JsonNode> startStage = Unirest.get( serverUrl() + "/" ).asJson();
        assertEquals( "PlaceNameService is Down!!!", server.getAlertService().getQueueReceiver().getAlert() );

    }

    private static void startMsgQueue(){
        factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    private static void startScheduleSvc(){
        server = new WebService().initialise();
        server.start( TEST_PORT );
    }

    private String serverUrl(){
        return "http://localhost:" + TEST_PORT;
    }

}