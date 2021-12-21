package me.aang099.pthcbot.commands;

import net.dv8tion.jda.api.Permission;

import me.aang099.pthcbot.PTHCBot;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ForceStop extends Command {
    @Override
    public void executeMessage() {
        if (!msg.getChannel().getId().equals(PTHCBot.cmdChannel) || !Objects.requireNonNull(msg.getMember()).hasPermission(Permission.ADMINISTRATOR)) {
            return;
        }
        System.out.println("Attempting to force stop server");

        if (PTHCBot.serverManager == null)
            msg.getMessage().reply("`ServerManager` is null").queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
        else PTHCBot.serverManager.end(true);
        if (PTHCBot.oldProcess == null)
            msg.getMessage().reply("`OldProcess` is null").queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
        else PTHCBot.oldProcess.destroyForcibly();


    }
}
