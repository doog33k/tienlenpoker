package com.sokaris.tienlenpoker.core;

import java.util.Iterator;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by sylvek on 26/01/15.
 */
public class Game {

    public static final int MAX_CARDS = 52;
    public static final int MAX_PLAYERS = 4;
    public static final long MASK_PLAYERS_POSITION = 0xF00000000000000L;
    public static final long MASK_PLAYERS_ROUND = 0xF0000000000000L;

    private final Player[] players;

    // bit mask of the last card played
    // [0000 - not used bits][xxxx  - player playing position][yyyy players in round][52 mask cards] = 64bits
    private long round;

    private Game(final Player[] players)
    {
        this.players = players;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MAX_PLAYERS; i++) {
            sb.append(players[i]).append("\r\n");
        }
        sb.append("round: ").append(Long.toBinaryString(this.round));
        return sb.toString();
    }

    /**
     * Create a new Game, shuffle cards, new round and set the first player to player1.
     *
     * @param player1 first player
     * @param player2 second player
     * @param player3 third player
     * @param player4 fourth player
     * @return an initialized game
     */
    public static Game newGame(final String player1, final String player2, final String player3, final String player4)
    {
        final Game game = new Game(new Player[]{new Player(0, player1), new Player(1, player2), new Player(2, player3), new Player(3, player4)});
        return game.shuffleCards().newRound(0);
    }

    /**
     * re-initialize the current round, all players are in.
     *
     * @param playerIndex player1 = 0; player2 = 1; player3 = 2; player4 = 3 - the first player of this round.
     * @return the current Game
     */
    public Game newRound(final int playerIndex)
    {
        round = MASK_PLAYERS_ROUND | (1L << (playerIndex + MAX_PLAYERS + MAX_CARDS));
        return this;
    }

    /**
     * Returns the current Player of the current round.
     *
     * @return the current player.
     */
    public Player currentPlayer()
    {
        long currentPlayer = (round >> (MAX_CARDS + MAX_PLAYERS)) & 0xF;

        int index = 0;
        while (currentPlayer != 1L) {
            currentPlayer >>= 1;
            index++;
        }

        return this.players[index];
    }

    /**
     * Set the next player of the current round.
     *
     * @return the next player, null if the round is done.
     */
    public Player nextPlayer()
    {
        long currentPlayer = (round >> (MAX_CARDS + MAX_PLAYERS)) & 0xF;
        long playersInRound = (round >> MAX_CARDS) & 0xF;

        long nextPlayer = (currentPlayer == 8L) ? 1L : currentPlayer << 1;
        while ((nextPlayer & playersInRound) == 0L && nextPlayer != currentPlayer) {
            nextPlayer = (nextPlayer == 8L) ? 1L : nextPlayer << 1;
        }

        if (nextPlayer == currentPlayer) {
            return null;
        }

        // reset previous player
        round &= ~(currentPlayer << (MAX_CARDS + MAX_PLAYERS));

        // set to next player
        round ^= nextPlayer << (MAX_CARDS + MAX_PLAYERS);

        int index = 0;
        while (nextPlayer != 1L) {
            nextPlayer >>= 1;
            index++;
        }

        return this.players[index];
    }

    private Game shuffleCards()
    {
        final Random random = new Random();

        // randomize MAX_CARDS and sorted it by TreeMap natural order.
        final SortedMap<Long, Integer> cardsRandomized = new TreeMap<Long, Integer>();
        for (int i = 0; i < MAX_CARDS; i++) {
            cardsRandomized.put(random.nextLong(), i);
        }

        // dispatching cards
        Iterator<Integer> card = cardsRandomized.values().iterator();
        for (int i = 0; i < MAX_CARDS; i++) {
            players[i%4].deck |= 1L << card.next();
        }

        return this;
    }

    /**
     * Update current round by adding the new card.
     *
     * @param card the new card in the round
     * @return the current game
     */
    public Game updateRound(long card)
    {
        // erasing previous card and keeping players in round and player round order
        round &= MASK_PLAYERS_ROUND | MASK_PLAYERS_POSITION;
        // replace the card in the round
        round ^= card;
        return this;
    }

    /**
     * Skips this player for this round
     *
     * @param player the player skiped
     * @return the current game
     */
    public Game skip(int player)
    {
        round &= ~(1L << (player + MAX_CARDS));
        return this;
    }

    /**
     * A playing game is a game with at least one player have at least one card on his deck.
     *
     * @return if the current game is playing
     */
    public boolean isPlayingGame()
    {
        return (players[0].deck | players[1].deck | players[2].deck | players[3].deck) > 0x0L;
    }

    /**
     * Allow to know if this card should be played on the current round.
     * A card should played if:
     * - it greater than the current round,
     * - or if current round contains a two, card must be a square or 3 successive peers
     * - etc…
     *
     * @param card the current card played by a player
     * @return true if ok, false otherwise.
     */
    public boolean shouldPlay(long card)
    {
        return true;
    }
}
