package me.aang099.pthcbot.commands;


import me.aang099.pthcbot.PTHCBot;
import me.aang099.pthcbot.handlers.GameConfigurationHandler;

public class NewGame extends Command {
    @Override
    public void executeMessage() {
        if (!msg.getChannel().getId().equals(PTHCBot.cmdChannel)) return;
        if (PTHCBot.serverRunning)
            msg.getMessage().reply("A PTHC is already running, please wait for it to end before creating a new PTHC").queue();
        new GameConfigurationHandler(msg.getAuthor().getIdLong(), msg.getChannel().getIdLong());
    }
}
