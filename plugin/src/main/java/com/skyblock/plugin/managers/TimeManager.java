package com.skyblock.plugin.managers;

public final class TimeManager {

    public enum SkyblockTime {
        DAWN, MORNING, NOON, AFTERNOON, DUSK, EVENING, NIGHT, MIDNIGHT
    }

    private static final TimeManager INSTANCE = new TimeManager();

    private SkyblockTime currentTime = SkyblockTime.MORNING;

    private TimeManager() {}

    public static TimeManager getInstance() {
        return INSTANCE;
    }

    public SkyblockTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(SkyblockTime time) {
        this.currentTime = time;
    }
}
