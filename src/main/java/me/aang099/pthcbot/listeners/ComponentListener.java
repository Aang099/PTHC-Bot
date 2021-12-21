package me.aang099.pthcbot.listeners;

import me.aang099.pthcbot.Communicator;
import me.aang099.pthcbot.handlers.CommunicationHandler;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ComponentListener extends ListenerAdapter {
    public void onButtonClick(ButtonClickEvent event) {
        CommunicationHandler.Holder request = CommunicationHandler.getInfo(event.getMessageId());

        if(request == null) throw new IllegalStateException();

        if(event.getComponentId().equals("allow_auth")) {
            Communicator.sendUserApprovalResponse(request.userId(), request.playerIndex());
            CommunicationHandler.removeRequest(request.messageId());
            event.reply("Allowing...").queue();
        } else if(event.getComponentId().equals("disallow_auth")) {
            Communicator.sendUserApprovalResponse("null", request.playerIndex());
            CommunicationHandler.removeRequest(request.messageId());
            event.reply("Denying...").queue();
        }
    }
}
