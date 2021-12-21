package me.aang099.pthcbot.listeners;

import me.aang099.pthcbot.PTHCBot;
import me.aang099.pthcbot.handlers.CommandHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class CommandListener extends ListenerAdapter {
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getAuthor().getIdLong() == PTHCBot.jda.getSelfUser().getIdLong()) return;
        if(event.getMessage().getChannel().getId().equals(PTHCBot.cmdChannel) && PTHCBot.serverRunning && !canBypass(event.getMessage().getContentRaw(), event.getMember())) {
            event.getMessage().delete().queue();
            event.getChannel().sendMessage("<@" + event.getAuthor().getId() + "> a game is currently running").queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }
        if(event.getMessage().getContentRaw().startsWith(PTHCBot.PREFIX)) CommandHandler.handle(event);
    }

    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        // Might implement later
        // CommandHandler.handle(event);
    }

    private boolean canBypass(String command, Member member) {
        return command.equalsIgnoreCase("!fstop") && member.hasPermission(Permission.ADMINISTRATOR);
    }
}
