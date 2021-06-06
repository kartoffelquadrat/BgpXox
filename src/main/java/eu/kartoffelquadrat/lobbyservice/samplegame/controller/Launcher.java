/**
 * Modified WAR launcher of the samplegame. Launch this program with: "mvn spring-boot:run"
 * <p>
 * Access to the UI is granted by the LobbyService WebUI through redirect.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
package eu.kartoffelquadrat.lobbyservice.samplegame.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * This class powers up Spring and ensures the annotated controllers are detected.
 */
@SpringBootApplication(scanBasePackages = {"eu.kartoffelquadrat.lobbyservice.samplegame"})
public class Launcher extends SpringBootServletInitializer {

    /**
     * Traditional launcher to power up spring. This one is only invoked when the application is launched as standalone
     * (as JAR). In WAR mode the extended initializer takes care of that.
     *
     * @param args launch arguments.
     */
    public static void main(String[] args) {

        // Power up Xox API backend - note: no call to registrator is required, the registrator has a postinit annot.
        SpringApplication.run(Launcher.class, args);
    }

    /**
     * Bootstraps the application. No call to Registrator is required (auto executed by bean on post init).
     *
     * @param builder SpringApplicationBuilder used for boostrapping
     * @return SpringApplicationBuilder the received builder
     */
    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(Launcher.class);
    }
}
