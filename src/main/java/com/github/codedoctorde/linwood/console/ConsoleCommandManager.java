package com.github.codedoctorde.linwood.console;

import com.github.codedoctorde.linwood.Linwood;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * @author CodeDoctorDE
 */
public abstract class ConsoleCommandManager implements ConsoleCommand {
    public abstract ConsoleCommand[] commands();
    private static final Logger logger = LogManager.getLogger(Linwood.class);

    @Override
    public boolean onCommand(String label, String[] args) {
        for (ConsoleCommand command : commands())
            if (Arrays.asList(command.aliases()).contains(
                    (args.length > 0) ? args[0].toLowerCase() : "")) {
                if(!command.onCommand(
                        (args.length > 0) ? args[0] : "",
                        (args.length > 0) ? Arrays.copyOfRange(args, 1, args.length) : new String[0]))
                    logger.error(command.syntax());
                return true;
            }
        return false;
    }

    public ConsoleCommand getCommand(String... args){
        ConsoleCommand command = this;
        for (String arg:
             args) {
            for (ConsoleCommand current:
                 commands())
                if (Arrays.asList(current.aliases()).contains(arg)) {
                    if (current instanceof ConsoleCommandManager)
                        command = ((ConsoleCommandManager) current).getCommand(Arrays.copyOfRange(args, 1, args.length));
                    else command = current;
                    break;
                }else
                    return null;
        }
        return command;
    }
}
