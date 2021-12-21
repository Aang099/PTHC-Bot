package me.aang099.pthcbot.configuration;

public enum WorldSize {
    SMALL(1),
    MEDIUM(2),
    LARGE(3);

    private final int numberType;

    WorldSize(int numberType) {
        this.numberType = numberType;
    }

    public int getNumberType() {
        return numberType;
    }
}
