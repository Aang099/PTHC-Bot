package me.aang099.pthcbot.builders;

import me.aang099.pthcbot.configuration.WorldSize;

public class RunScriptBuilder {
    private int port;

    private int worldSize;

    private int maxPlayers;

    public RunScriptBuilder() {}

    public RunScriptBuilder(int port, int worldSize, int maxPlayers) {
        this.port = port;
        this.worldSize = worldSize;
        this.maxPlayers = maxPlayers;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public RunScriptBuilder setWorldSize(WorldSize worldSize) {
        this.worldSize = worldSize.getNumberType();
        return this;
    }

    public RunScriptBuilder setWorldSize(int worldSize) {
        if(worldSize < 1 || worldSize > 3) throw new IllegalArgumentException();
        this.worldSize = worldSize;
        return this;
    }

    public RunScriptBuilder setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }

    public String build() {
        String worldPath = "./Worlds/PTHC.wld";
        String worldSelectPath = "./Worlds";
        return "TerrariaServer.exe -port " + port + " -worldselectpath \"" + worldSelectPath + "\" -world \"" + worldPath + "\" -autocreate " + worldSize + " -maxplayers " + maxPlayers;
    }
}
