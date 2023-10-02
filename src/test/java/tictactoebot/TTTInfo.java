package tictactoebot;

import net.dv8tion.jda.api.entities.User;

public final class TTTInfo {

    public final User opponent;
    public final TTTLogic logic;
    public final long messageID;

    public TTTInfo(User opponent, TTTLogic logic, long messageID) {
        this.opponent = opponent;
        this.logic = logic;
        this.messageID = messageID;
    }
}
