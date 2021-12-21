package me.aang099.pthcbot.handlers;

import me.aang099.pthcbot.commands.Command;
import me.aang099.pthcbot.commands.ForceStop;
import me.aang099.pthcbot.commands.GetID;
import me.aang099.pthcbot.commands.NewGame;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/** This handles incoming commands and resolves them */
public class CommandHandler {

    public static void handle(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        String cmdName = args[0].substring(1).toLowerCase();

        Command command = findAndCreateCommand(cmdName);

        readyAndExecute(command, event);
    }

    private static void readyAndExecute(Command command, MessageReceivedEvent event) {
        if(command == null) {
            return;
        }

        command.parse(event);
        command.execute();
    }

    private static Command findAndCreateCommand(String name) {
        return switch(name) {
            case "new" -> new NewGame();
            case "fstop" -> new ForceStop();
            case "id" -> new GetID();
            default -> null;
        };
    }
}
