package me.aang099.pthcbot.listeners;

import me.aang099.pthcbot.PTHCBot;
import me.aang099.pthcbot.handlers.GameConfigurationHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GameConfigListener extends ListenerAdapter {
    private final GameConfigurationHandler parent;
    private final long userId;
    private final long channelId;

    public GameConfigListener(GameConfigurationHandler parent, long userId, long channelId) {
        this.parent = parent;
        this.userId = userId;
        this.channelId = channelId;
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != channelId) return;
        if (event.getAuthor().getIdLong() != userId) {
            if (event.getAuthor().getIdLong() != PTHCBot.jda.getSelfUser().getIdLong())
                event.getMessage().delete().queue();
            return;
        }
        parent.handle(event);
    }
}
