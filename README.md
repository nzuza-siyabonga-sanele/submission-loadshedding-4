LoadShedding Schedule Application
Overview
This is a Java 17, Maven multi-module application for exploring South African load-shedding information. It combines a web interface with small HTTP services for place names, the current load-shedding stage, and schedules. ActiveMQ is used for messaging between parts of the application.

Important: The schedule service currently returns a mock schedule for most locations. Treat the schedule data as demonstration/test data, not as a live Eskom or municipal schedule.
Main features
- Web interface for selecting a province and town.
- Place-name service that loads South African place data from CSV.
- REST endpoint for reading and updating the current load-shedding stage.
- Schedule endpoint that returns schedule data for a town and stage.
- ActiveMQ topic/queue messaging for stage-change and alert events.
- Automated tests across the modules.
Technology
- Java 17
- Maven
- Javalin HTTP framework
- Thymeleaf templates
- ActiveMQ messaging
- Unirest HTTP client
- Jackson / Gson JSON libraries
- JUnit 5 and AssertJ for tests
Project structure
Module / file	Purpose
places/	Place-name CSV parsing, in-memory place data, and place-name HTTP service
stage/	REST API for reading and setting the current load-shedding stage
schedule/	REST API for town schedules; currently includes a mock schedule
web/	Main web application, routes, controller, and Thymeleaf templates
common/	Shared transfer objects, logging, checks, and messaging utilities
alert/	Alert queue sender/receiver support
places/resources/PlaceNamesZA2008.csv	Place-name dataset
pom.xml	Parent Maven project and module definitions
Makefile	Optional grading helper that uses Docker and rsync


Requirements
Install the following before running the application:
1. JDK 17 (ensure java -version reports Java 17).
2. Apache Maven (ensure mvn -version works).
3. ActiveMQ Classic, configured to listen on tcp://localhost:61616, for messaging features.
4. A terminal or Java IDE such as IntelliJ IDEA.
The messaging utility defines the broker URL as tcp://localhost:61616 and credentials as admin / admin. Use a local development broker only, and change these defaults before using the application in a shared or production environment.
Build the project
From the repository root (the directory containing the parent pom.xml), run:
mvn clean install
Run the tests with:
mvn test
If the build fails, check that Java 17 and Maven are installed and that dependencies can be downloaded. Some modules refer to other project modules, so building from the root is recommended rather than building a child module in isolation.
Running the application
The web service starts the other HTTP services itself. Start the ActiveMQ broker first, then run the wethinkcode.web.WebService main class.
Recommended: run from an IDE
1. Open the project root in IntelliJ IDEA as a Maven project.
2. Wait for Maven dependency import to finish.
3. Ensure the ActiveMQ broker is running at tcp://localhost:61616.
4. Create a run configuration for wethinkcode.web.WebService in the web module.
5. Run it and open http://localhost:8088.
Service ports
Service	Default port	Purpose
Web application	8088	Browser interface
Place-name service	7000	Place/province/town data
Stage service	7001	Current stage REST API
Schedule service	7002	Town schedule REST API
ActiveMQ	61616	Messaging broker


WebService starts the place-name, schedule, and stage services during its initialization. Avoid starting duplicate instances on the same ports.
REST API quick reference
Current load-shedding stage
Read the current stage:
GET http://localhost:7001/stage
Set the stage by posting a JSON body:
POST http://localhost:7001/stage
Content-Type: application/json

{"stage": 2}
A non-negative stage is accepted by the stage service. The schedule endpoint validates explicitly requested stages to the range 0 through 8.
Schedule lookup
Get a schedule using an explicit stage:
GET http://localhost:7002/{province}/{town}/{stage}
Get a schedule using the stage received through messaging (or the default stage):
GET http://localhost:7002/{province}/{town}
Replace the path placeholders with the province and town names. A matching schedule is returned as JSON; a missing schedule returns HTTP 404. The current implementation returns mock schedule data for most locations and an empty result for the province name Mars.
Place names
The place-name service provides endpoints for province, town, and neighbourhood data. Consult places/src/main/java/wethinkcode/places/PlaceNameService.java for the registered routes and supported command-line configuration options.
Configuration and data
The place-name service supports command-line options for a configuration file, data directory, CSV data file, and port. The CSV data is loaded into an in-memory data structure when the service initializes. Review the source code and CSV headers if you need to change the data source or configuration.
Tests
Tests are located under each module's src/test/java/ directory. Run all tests from the project root:
mvn test
The Makefile contains a grade target intended for the exercise's grading environment. It uses Docker and rsync; it is not required for normal local development.
Troubleshooting
- Port already in use: Stop the other process using ports 8088, 7000, 7001, or 7002.
- Messaging connection errors: Confirm ActiveMQ is running and listening on tcp://localhost:61616; verify broker credentials if you changed them.
- Missing place data: Check that the CSV file exists at the path configured for the place-name service.
- Maven dependency/build errors: Run commands from the repository root and confirm JDK 17 is selected.
- No live schedule accuracy: The schedule module currently uses mock data; it is not connected to a live load-shedding data provider.
Current limitations and development notes
- Schedule results are mocked rather than sourced from a live utility or municipal feed.
- Place data is held in memory; no persistent database is configured in the inspected project.
- Messaging assumes a local ActiveMQ broker and development credentials.
- The source includes thread shutdown calls using deprecated Thread.stop(); this should be replaced with a safer shutdown mechanism before production use.
- The project should be reviewed for configuration, error handling, and security before deployment.
License
No license information was identified in the project files. Add a license if you intend to distribute or reuse this project publicly.
