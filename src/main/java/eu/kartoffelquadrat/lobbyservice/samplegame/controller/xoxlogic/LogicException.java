package eu.kartoffelquadrat.lobbyservice.samplegame.controller.xoxlogic;

/**
 * Custom RuntimeException that is fired whenever the logic is instructed to handle parameters that semantically are not
 * applicable.
 */
public class LogicException extends RuntimeException {

    public LogicException(String cause) {
        super(cause);
    }
}
