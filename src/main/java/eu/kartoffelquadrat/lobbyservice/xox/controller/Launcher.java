/**
 * Launch this program with: "mvn spring-boot:run"
 *
 * Access to the UI is granted by the LobbyService WebUI through redirect.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
package eu.kartoffelquadrat.lobbyservice.xox.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class powers up Spring and ensures the annotated controllers are detected.
 */
@SpringBootApplication
public class Launcher {

    public static final String GAME_SERVICE_NAME = "Xox";

    public static void main(String[] args) {
        SpringApplication.run(Launcher.class, args);
    }
}

