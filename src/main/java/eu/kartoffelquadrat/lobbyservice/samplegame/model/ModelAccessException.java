package eu.kartoffelquadrat.lobbyservice.samplegame.model;

/**
 * Custom RuntimeException that is fired whenever model modifications are requested that would lead to an inconsistent
 * state.
 *
 * @Author: Maximilian Schiedermeier
 * @Date: December 2020
 */
public class ModelAccessException extends RuntimeException {
    public ModelAccessException(String cause) {
        super(cause);
    }
}
