package eu.kartoffelquadrat.lobbyservice.samplegame.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Non-REST controller that maps GET requests at specific locations to the XOX landing web page.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
@Controller
public class WebController {

    @RequestMapping("/webui/games/{12345}")
    public String getLandingPage() {
        return "xox";
    }
}
