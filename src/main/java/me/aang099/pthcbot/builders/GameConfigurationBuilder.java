package me.aang099.pthcbot.builders;

import me.aang099.pthcbot.configuration.GameConfiguration;
import me.aang099.pthcbot.configuration.WorldSize;

public class GameConfigurationBuilder {
    private WorldSize worldSize;

    private int maxPlayers;

    private int graceTime;

    public void setWorldSize(WorldSize worldSize) {
        this.worldSize = worldSize;
    }

    public void setWorldSize(int worldSize) {
        for(WorldSize worldSizeEnum : WorldSize.values()) {
            if(worldSizeEnum.getNumberType() == worldSize) this.worldSize = worldSizeEnum;
        }
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setGraceTime(int graceTime) {
        this.graceTime = graceTime;
    }

    public GameConfiguration build() {
        return new GameConfiguration(worldSize, maxPlayers, graceTime);
    }
}
