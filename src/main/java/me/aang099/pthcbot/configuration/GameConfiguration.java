package me.aang099.pthcbot.configuration;

import me.aang099.pthcbot.builders.RunScriptBuilder;


public record GameConfiguration(WorldSize worldSize, int maxPlayers, int graceTime) {

    public String getRunScript() {
        int port = 7777;
        return new RunScriptBuilder(port, worldSize.getNumberType(), maxPlayers).build();
    }
}
