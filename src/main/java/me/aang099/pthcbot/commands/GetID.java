package me.aang099.pthcbot.commands;

public class GetID extends Command {
    @Override
    public void executeMessage() {
        msg.getMessage().reply("Your id is `" + msg.getAuthor().getId() + "`").queue();
    }
}
