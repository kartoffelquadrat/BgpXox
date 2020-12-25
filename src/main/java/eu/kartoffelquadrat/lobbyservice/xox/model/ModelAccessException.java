package eu.kartoffelquadrat.lobbyservice.xox.model;

/**
 * Custom RuntimeException that is fired whenever model modifications are requested that would lead to an inconsistent state.
 */
public class ModelAccessException extends RuntimeException {
    public ModelAccessException(String cause) {
        super(cause);
    }
}
