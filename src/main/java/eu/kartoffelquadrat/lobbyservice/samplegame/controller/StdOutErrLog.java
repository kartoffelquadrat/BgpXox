package eu.kartoffelquadrat.lobbyservice.samplegame.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

/**
 * Custom redirect of standard out / error stream to logger.
 * See: https://stackoverflow.com/a/1370033
 *
 * Has to be invoked at program start: StdOutErrLog.tieSystemOutAndErrToLog();
 */
public class StdOutErrLog {

    private static final Logger logger = LoggerFactory.getLogger(StdOutErrLog.class);

    public static void tieSystemOutAndErrToLog() {
        System.setOut(createLoggingProxy(System.out));
        System.setErr(createLoggingProxy(System.err));
    }

    public static PrintStream createLoggingProxy(final PrintStream realPrintStream) {
        return new PrintStream(realPrintStream) {
            public void print(final String string) {
                realPrintStream.print(string);
                logger.info(string);
            }
        };
    }
}