package eu.kartoffelquadrat.lobbyservice.xox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Non-REST controller that maps GET requests at specific locations to the XOX landing web page.
 */
@Controller
public class WebController {

    @RequestMapping("/webui/games/{12345}")
    public String getLandingPage() {
        return "xox";
    }
}
