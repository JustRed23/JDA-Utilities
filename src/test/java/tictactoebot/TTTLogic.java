package tictactoebot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;
import java.util.Random;

public class TTTLogic {

    private static final String
            ICON_EMPTY = "<:ttt:848839803864088616>",
            ICON_X = "<:tttX:848839804065284106>",
            ICON_O = "<:tttO:848839803939848202>";

    private static final int[][] WINNING_COMBINATIONS = {
            {1, 2, 3}, {4, 5, 6}, {7, 8, 9}, // Horizontal
            {1, 4, 7}, {2, 5, 8}, {3, 6, 9}, // Vertical
            {1, 5, 9}, {3, 5, 7}             // Diagonal
    };

    private final User player;
    private final User opponent;
    private final EmbedBuilder builder;
    private final String[] board = new String[9];

    private final boolean playerHasX;
    private String endMsg;
    private int turn = 1;

    public TTTLogic(TTTComponent component) {
        this.builder = component.builder;
        this.player = component.player;
        this.opponent = component.opponent;
        this.playerHasX = new Random().nextBoolean();

        Arrays.fill(board, ICON_EMPTY);
    }

    public String generateDescription() {
        StringBuilder description = new StringBuilder();
        description.append(playerHasX ? ICON_X : ICON_O).append(" ").append(player.getEffectiveName()).append("\n");
        description.append(playerHasX ? ICON_O : ICON_X).append(" ").append(opponent.getEffectiveName()).append("\n");

        if (endMsg != null) description.append(endMsg).append("\n");
        else description.append(getTurnUser().getEffectiveName()).append("'s turn").append("\n");

        description.append("\n");

        for (int i = 0; i < board.length; i++) {
            description.append(board[i]);
            if (i == 2 || i == 5 || i == 8) description.append("\n");
            else description.append(" ");
        }

        return description.toString();
    }

    public void makeMove(int move, User user) {
        if (invalidMove(user)) return;
        if (move < 1 || move > 9) return;
        if (user.getIdLong() != getTurnUser().getIdLong()) return;
        if (!board[move - 1].equals(ICON_EMPTY)) return;

        boolean isPlayer = user.getIdLong() == player.getIdLong();
        String icon = playerHasX && isPlayer ? ICON_X : playerHasX ? ICON_O : isPlayer ? ICON_O : ICON_X;
        board[move - 1] = icon;
        turn++;
    }

    public boolean checkGameState(User user) {
        boolean isPlayer = user.getIdLong() == player.getIdLong();
        String icon = playerHasX && isPlayer ? ICON_X : playerHasX ? ICON_O : isPlayer ? ICON_O : ICON_X;

        for (int[] combination : WINNING_COMBINATIONS) {
            if (board[combination[0] - 1].equals(icon) && board[combination[1] - 1].equals(icon) && board[combination[2] - 1].equals(icon)) {
                endMsg = user.getEffectiveName() + " wins!";
                break;
            }
        }

        if (turn > 9)
            endMsg = "It's a tie!";

        builder.setDescription(generateDescription());

        return endMsg != null;
    }

    public boolean invalidMove(User user) {
        return isInvalidPlayer(user) || !isUserTurn(user);
    }

    public boolean isUserTurn(User user) {
        return user.getIdLong() == getTurnUser().getIdLong();
    }

    public boolean isInvalidPlayer(User user) {
        return user.getIdLong() != player.getIdLong() && user.getIdLong() != opponent.getIdLong();
    }

    private User getTurnUser() {
        return turn % 2 == 0 ? opponent : player;
    }
}
