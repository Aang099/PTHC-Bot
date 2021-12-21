package me.aang099.pthcbot.listeners;

import me.aang099.pthcbot.PTHCBot;
import me.aang099.pthcbot.configuration.GameConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ParticipationListener extends ListenerAdapter {
    private final GameConfiguration configuration;
    private final long messageId;

    private final List<Long> reactedUsers = new ArrayList<>();
    private int reactedUsersCount = 0;

    public ParticipationListener(GameConfiguration configuration, long messageId) {
        this.configuration = configuration;
        this.messageId = messageId;
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(!event.getMessage().getChannel().getId().equals(PTHCBot.cmdChannel) || event.getMessage().getAuthor().getIdLong() == PTHCBot.jda.getSelfUser().getIdLong()) return;
        if(!event.getMessage().getContentRaw().equals("!fstart")) {
            event.getMessage().delete().queue();
            return;
        }
        if(!Objects.requireNonNull(event.getMessage().getMember()).hasPermission(Permission.ADMINISTRATOR)) return;
        event.getMessage().reply("Forcing game to start...").queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS, unused -> event.getMessage().delete().queue()));
        createGame(event.getChannel());
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if(event.getMessageIdLong() != messageId || !event.getReactionEmote().equals(MessageReaction.ReactionEmote.fromUnicode("\uD83D\uDFE9", PTHCBot.jda))) return;
        reactedUsers.add(Objects.requireNonNull(event.getUser()).getIdLong());
        reactedUsersCount++;

        if(reactedUsersCount < 7) return;
        createGame(event.getChannel());
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if(event.getMessageIdLong() != messageId || !event.getReactionEmote().equals(MessageReaction.ReactionEmote.fromUnicode("\uD83D\uDFE9", PTHCBot.jda))) return;
        reactedUsers.remove(Objects.requireNonNull(event.getUser()).getIdLong());
        reactedUsersCount--;
    }

    private void createGame(MessageChannel channel) {
        PTHCBot.jda.removeEventListener(this);

        channel.sendMessage("Starting game, join with ip PracticeTHC.epicgamer.org and port 7777").queue();

        MessageEmbed embed = new EmbedBuilder()
                .setColor(0x12db41)
                .setTitle("New PTHC")
                .setDescription("PTHC " + PTHCBot.pthcCount + " is starting, if you would like to join connect to PracticeTHC.epicgamer.org with port " + 7777 + ".")
                .setFooter("If you have any issues contact Aang#4343 or TheCocowoGamer#6318")
                .build();

        MessageChannel announcementChannel = PTHCBot.jda.getTextChannelById(PTHCBot.pthcAnnouncementsChannel);
        assert announcementChannel != null;
        announcementChannel.sendMessageEmbeds(embed).queue();
        announcementChannel.sendMessage("<@&863514161937383436>").queue();

        notifyUsers(reactedUsers.stream().mapToLong(i -> i).toArray());

        PTHCBot.createPTHC(configuration);
    }

    private void notifyUsers(long[] users) {
        Thread notifyThread = new Thread(() -> {
            for(long userId : users) {
                try {
                    User user = PTHCBot.jda.getUserById(userId);

                    assert user != null;
                    MessageChannel channel = user.openPrivateChannel().complete();

                    channel.sendMessage("<@" + userId + "> PTHC " + PTHCBot.pthcCount + " is starting").complete();
                } catch (Exception ignored) {
                    System.out.println("Couldn't find user to notify (what da fuck)");
                }
            }
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        notifyThread.start();
    }
}
