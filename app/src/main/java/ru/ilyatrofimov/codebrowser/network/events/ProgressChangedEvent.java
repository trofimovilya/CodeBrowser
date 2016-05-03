package ru.ilyatrofimov.codebrowser.network.events;

/**
 * Created by ILYATTR on 01/05/16.
 */
public class ProgressChangedEvent {
    private int progress;

    public ProgressChangedEvent(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }
}
