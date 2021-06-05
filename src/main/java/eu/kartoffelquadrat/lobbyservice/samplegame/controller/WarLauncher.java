/**
 * Modified WAR launcher of the samplegame. Launch this program with: "mvn spring-boot:run"
 * <p>
 * Access to the UI is granted by the LobbyService WebUI through redirect.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
package eu.kartoffelquadrat.lobbyservice.samplegame.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * This class powers up Spring and ensures the annotated controllers are detected.
 */
@SpringBootApplication(scanBasePackages = {"eu.kartoffelquadrat.lobbyservice.samplegame"})
public class WarLauncher extends SpringBootServletInitializer {

    public static void main(String[] args) {

        // Power up Xox API backend
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Launcher.class, args);
        Registrator registrator = applicationContext.getBean(Registrator.class);

        // Redirect stdout / stderr to logger, so the application container shows traces. Has to occur after
        // application context has been created.
        StdOutErrLog.tieSystemOutAndErrToLog();
        Logger logger = LoggerFactory.getLogger(WarLauncher.class);
        logger.info("Xox application context created. Contacting Lobby-Service...");

        // Register Xox at LS-GameRegistry
        try {
            registrator.registerAtLobbyService();
        } catch (UnirestException ue) {
            throw new RuntimeException("LobbyService not reachable at provided location.");
        }
    }
}
