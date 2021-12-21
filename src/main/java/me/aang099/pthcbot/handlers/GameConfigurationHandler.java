package me.aang099.pthcbot.handlers;

import me.aang099.pthcbot.PTHCBot;
import me.aang099.pthcbot.builders.GameConfigurationBuilder;
import me.aang099.pthcbot.configuration.GameConfiguration;
import me.aang099.pthcbot.configuration.WorldSize;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import me.aang099.pthcbot.listeners.GameConfigListener;
import me.aang099.pthcbot.listeners.ParticipationListener;

import java.util.Objects;

@SuppressWarnings("BusyWait")
public class GameConfigurationHandler {
    private final long channelId;
    private final GameConfigListener listener;

    private static final String startMessage = "If you don't respond within a minute the setup will cancel, this applies to every " +
            "stage of the setup. You can also say **Cancel** at any time cancel the setup.";

    private int stage = 0;

    private final Thread timeoutThread;
    private long lastStepTime;

    private final GameConfigurationBuilder gameConfigurationBuilder;

    public GameConfigurationHandler(long targetUserId, long channelId) {
        this.channelId = channelId;

        listener = new GameConfigListener(this, targetUserId, channelId);
        PTHCBot.jda.addEventListener(listener);

        updateLastStepTime();
        Objects.requireNonNull(PTHCBot.jda.getTextChannelById(channelId)).sendMessage("Starting setup...").queue();
        Objects.requireNonNull(PTHCBot.jda.getTextChannelById(channelId)).sendMessage(startMessage).queue();
        Objects.requireNonNull(PTHCBot.jda.getTextChannelById(channelId)).sendMessage("What world size would you like, Small (1), Medium (2), or Large (3)?").queue();
        gameConfigurationBuilder = new GameConfigurationBuilder();

        timeoutThread = new Thread(this::timeoutChecker);
        timeoutThread.start();
    }

    public void handle(MessageReceivedEvent event) {
        if(stage == -1) return;
        MessageChannel channel = event.getChannel();
        Message message = event.getMessage();
        String contentDisplay = message.getContentDisplay();
        String contentRaw = message.getContentRaw();

        if(contentDisplay.equalsIgnoreCase("cancel")) {
            channel.sendMessage("Cancelling setup...").queue();
            try {
                discard(0);
            } catch (InterruptedException e) {
                channel.sendMessage("Something went wrong").queue();
                e.printStackTrace();
            }
            return;
        }

        boolean valid = false;
        switch(stage) {
            case 0:
                valid = true;
                if(contentRaw.equalsIgnoreCase("small") || contentRaw.equals("1")) gameConfigurationBuilder.setWorldSize(WorldSize.SMALL);
                else if(contentRaw.equalsIgnoreCase("medium") || contentRaw.equals("2")) gameConfigurationBuilder.setWorldSize(WorldSize.MEDIUM);
                else if(contentRaw.equalsIgnoreCase("large") || contentRaw.equals("3")) gameConfigurationBuilder.setWorldSize(WorldSize.LARGE);
                else valid = false;
                if(valid) {
                    channel.sendMessage("How long do you want the grace time to be (in minutes)? (5-60)").queue();
                    gameConfigurationBuilder.setMaxPlayers(69);
                    stage++;
                }
                break;
            case 1:
                try {
                    int graceMinutes = Integer.parseUnsignedInt(message.getContentRaw());

                    if(graceMinutes >= 5 && graceMinutes <= 60) {
                        valid = true;
                        gameConfigurationBuilder.setGraceTime(graceMinutes);
                        channel.sendMessage("Setup is finishing...").queue();
                        GameConfiguration configuration = gameConfigurationBuilder.build();
                        channel.sendMessage("----------").queue();
                        channel.sendMessage("PTHC " + PTHCBot.pthcCount + " will have a maximum of `" + configuration.maxPlayers() + "` players and a grace period of `" + configuration.graceTime() + "` minutes").queue();
                        channel.sendMessage("----------").queue();
                        channel.sendMessage("The PTHC will start once there are 7 \uD83D\uDFE9 reactions").queue(reactionMessage -> {
                            reactionMessage.addReaction("U+1F7E9").queue();
                            PTHCBot.jda.addEventListener(new ParticipationListener(configuration, reactionMessage.getIdLong()));
                        });
                        discard(0);
                    } else {
                        channel.sendMessage("Please input a valid integer from 5 to 60").queue();
                    }
                } catch(NumberFormatException exception) {
                    channel.sendMessage("Please input a valid integer from 5 to 60").queue();
                } catch (InterruptedException e) {
                    channel.sendMessage("Something went wrong").queue();
                    e.printStackTrace();
                }

        }
        if(valid) updateLastStepTime();
        else event.getMessage().delete().queue();
    }

    private void timeoutChecker() {
        while(!timeoutThread.isInterrupted()) {
            try {
                Thread.sleep(50);
                if((System.currentTimeMillis() - lastStepTime) >= 60000) discard(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateLastStepTime() {
        lastStepTime = System.currentTimeMillis();
    }

    public void discard(int reason) throws InterruptedException {
        if(reason == 1) Objects.requireNonNull(PTHCBot.jda.getTextChannelById(channelId)).sendMessage("Your setup has been canceled because you took too long to respond").queue();
        stage = -1;
        timeoutThread.interrupt();
        timeoutThread.join();
        PTHCBot.jda.removeEventListener(listener);
    }
}
