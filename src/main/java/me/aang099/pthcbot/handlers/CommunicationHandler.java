package me.aang099.pthcbot.handlers;

import me.aang099.pthcbot.PTHCBot;
import me.aang099.pthcbot.Communicator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommunicationHandler {
    private static final List<Holder> pendingRequests = new ArrayList<>();

    public static void handleRequestUserApproval(String tag, int playerIndex) {
        try {
            User user = PTHCBot.jda.getUserByTag(tag);
            if(user == null) {
                System.out.println("Couldnt find user: " + tag);
                Communicator.sendUserApprovalResponse("null", playerIndex);
                return;
            }

            MessageBuilder messageBuilder = new MessageBuilder();

            messageBuilder.setContent("Authentication requested");
            messageBuilder.setActionRows(ActionRow.of(Button.success("allow_auth", "Allow"), Button.danger("disallow_auth", "Deny")));

            user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(messageBuilder.build()).queue((message) -> pendingRequests.add(new Holder(message.getId(), user.getId(), playerIndex))));
        } catch(Exception exception) {
            System.out.println("Couldn't find user: " + tag);
            Communicator.sendUserApprovalResponse("null", playerIndex);
        }
    }

    public static void handleAnnounceWinner(String discordId) {
        System.out.println("PTHC ended");
        MessageEmbed embed;
        if(discordId.equals("null")) {
            embed = new EmbedBuilder()
                    .setColor(0xc70c0c)
                    .setTitle("PTHC Ended")
                    .setDescription("PTHC " + PTHCBot.pthcCount + " has ended.")
                    .setFooter("If you have any issues or would like to dispute these results please contact Aang#4343 or TheCocowoGamer#6318")
                    .build();
        } else {
            embed = new EmbedBuilder()
                    .setColor(0xc70c0c)
                    .setTitle("PTHC Ended")
                    .setDescription("PTHC " + PTHCBot.pthcCount + " has ended. <@" + discordId + "> won.")
                    .setFooter("If you have any issues or would like to dispute these results please contact Aang#4343 or TheCocowoGamer#6318")
                    .build();
        }
        Objects.requireNonNull(PTHCBot.jda.getTextChannelById(PTHCBot.pthcAnnouncementsChannel)).sendMessageEmbeds(embed).queue();
        PTHCBot.serverManager.end(false);
        PTHCBot.pthcCount++;
        try {
            FileWriter writer = new FileWriter("./count.txt");
            writer.write(Integer.toString(PTHCBot.pthcCount));
            writer.close();
        } catch (IOException e) {
            System.out.println("Couldn't save PTHC count!");
            e.printStackTrace();
        }
    }

    public static Holder getInfo(String messageId) {
        for(Holder pendingRequest : pendingRequests) {
            if(pendingRequest.messageId.equals(messageId)) return pendingRequest;
        }

        return null;
    }

    public static void removeRequest(String messageId) {
        pendingRequests.removeIf(pendingRequest -> pendingRequest.messageId.equals(messageId));
    }

    public record Holder(String messageId, String userId, int playerIndex) {}
}
