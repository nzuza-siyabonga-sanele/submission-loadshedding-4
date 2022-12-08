package wethinkcode.web;

import com.google.common.annotations.VisibleForTesting;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import kong.unirest.Unirest;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import wethinkcode.AlertQueue;
import wethinkcode.loadshed.spikes.TopicReceiver;
import wethinkcode.places.PlaceNameService;
import wethinkcode.schedule.ScheduleService;
import wethinkcode.stage.StageService;

/**
 * I am the front-end web server for the LightSched project.
 * <p>
 * Remember that we're not terribly interested in the web front-end part of this server, more in the way it communicates
 * and interacts with the back-end services.
 */
public class WebService {

    public static final int DEFAULT_PORT = 8088;
    public static final String STAGE_SVC_URL = "http://localhost:" + StageService.DEFAULT_PORT;

    public static final String PLACES_SVC_URL = "http://localhost:" + PlaceNameService.DEFAULT_PORT;

    public static final String SCHEDULE_SVC_URL = "http://localhost:" + ScheduleService.DEFAULT_PORT;
    private static final String PAGES_DIR = "/templates";
    public static int TOPIC_STAGE ;
    private static TopicReceiver app;

    public AlertQueue getAlertService() {
        return alertService;
    }

    public static AlertQueue alertService;
    public Thread task;
    public PlaceNameService placeNameService;
    public ScheduleService scheduleService;
    public StageService stageService;

    public static void main(String[] args) {
        final WebService svc = new WebService().initialise();
        svc.start();
    }
    private Javalin server;
    private int servicePort;

    @VisibleForTesting
    WebService initialise() {
        configureHttpClient();
        startMQMachinery();
        server = configureHttpServer();
        // FIXME: Initialise HTTP client, MQ machinery and server from here
        return this;
    }

    public static int initTrackStage(){
        TOPIC_STAGE = app.getStage();
        return TOPIC_STAGE;
    }

    public void start() {
        start( DEFAULT_PORT );
    }

    @VisibleForTesting
    void start(int networkPort) {
        servicePort = networkPort;
        run();
    }

    public void stop() {
        server.stop();
    }

    public void run() {
        server.start(servicePort);
    }

    private void configureHttpClient() {
        placeNameService = new PlaceNameService();
        placeNameService.start();
        scheduleService = new ScheduleService();
        scheduleService.start();
        stageService = new StageService();
        stageService.start();

        TOPIC_STAGE = Unirest.get( STAGE_SVC_URL +"/stage" ).asJson().getBody().getObject().getInt("stage");
    }

    private Javalin configureHttpServer() {
        server = Javalin.create(config -> {
            config.addStaticFiles(PAGES_DIR, Location.CLASSPATH);
            JavalinRenderer.register(JavalinThymeleaf.INSTANCE);
            JavalinThymeleaf.configure(templateEngine());
        });
        Routes.configure(server);
        return server;
    }
    private TemplateEngine templateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(PAGES_DIR);
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine;
    }
    private WebService startMQMachinery(){
        app = new TopicReceiver();
        task = new Thread( app );
        task.start();
        alertService = new AlertQueue();
        alertService.configureReceiver();
        return this;
    }

    public void stopPlaceNameService(){
        placeNameService.stop();
    }
    public void stopScheduleService(){
        scheduleService.stop();
    }
    public void stopStageService(){
        stageService.stop();
    }

    public void stopMQMachinery(){
        task.stop();
        alertService.stopQueueReceiver();
    }

}
