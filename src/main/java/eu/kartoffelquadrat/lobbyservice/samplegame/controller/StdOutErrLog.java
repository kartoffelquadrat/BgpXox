package eu.kartoffelquadrat.lobbyservice.samplegame.controller;


import java.io.PrintStream;
import org.apache.log4j.Logger;
/**
 * Custom redirect of standard out / error stream to logger.
 * See: https://stackoverflow.com/a/1370033
 *
 * Has to be invoked at program start: StdOutErrLog.tieSystemOutAndErrToLog();
 */
public class StdOutErrLog {

    private static final Logger logger = Logger.getLogger(StdOutErrLog.class);

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