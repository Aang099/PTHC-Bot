package me.aang099.pthcbot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import me.aang099.pthcbot.PTHCBot;

public abstract class Command {

    protected static Permission[] requiredPermissions = new Permission[0];

    public abstract void executeMessage();

    protected MessageReceivedEvent msg;

    public void parse(MessageReceivedEvent msg) {
        this.msg = msg;
    }

    public void execute() {
        if(passesPermissionsCheck(msg.getMember())) {
            executeMessage();
        } else PTHCBot.sendMessage(msg.getChannel(), "You do not have sufficient permission to do this!");
    }

    public boolean passesPermissionsCheck(Member member) {
        for(Permission permission : requiredPermissions) {
            if(!member.hasPermission(permission)) return false;
        }
        return true;
    }
}
