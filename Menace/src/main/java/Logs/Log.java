package Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;


public class Log {

    public static void gameLog(String message)
    {
        Logger logger = LoggerFactory.getLogger(Log.class);
        logger.info(message);
    }
}
