package main.View.screen.gamescreen;

import java.util.ArrayList;
import java.util.List;

class GameScreenMessageHandler {
    private final List<String> messageHistory;
    private static final int MAX_MESSAGES = 20;

    public GameScreenMessageHandler() {
        this.messageHistory = new ArrayList<>();
    }

    public void addMessage(String message) {
        messageHistory.add(message);
        if (messageHistory.size() > MAX_MESSAGES) {
            messageHistory.remove(0);
        }
    }

    public List<String> getMessages() {
        return new ArrayList<>(messageHistory);
    }

    public void clearMessages() {
        messageHistory.clear();
    }

    public int getMessageCount() {
        return messageHistory.size();
    }
}

