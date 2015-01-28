package com.sokaris.tienlenpoker.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by sylvek on 28/01/15.
 */
public class FourHumansPlayer {

    public static void main(String... args)
    {
        System.out.println("tien len poker");
        System.out.println("value of cards. 0 = 3-pic / 51 = 2-coeur");

        for (int i = 0; i < Game.MAX_CARDS; i++) {
            final long cardValue = 1L << i;
            System.out.println(i + " => [" + cardValue + "] " + Long.toBinaryString(cardValue));
        }

        final Game game = Game.newGame("Pascal", "Florimon", "Sylvain", "Etienne");

        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (game.isPlayingGame()) {
            Player player = game.currentPlayer();
            while (player != null) {

                if (!player.canPlayThisGame()) {
                    player = game.nextPlayer();
                    continue;
                }

                System.out.println(game);
                System.out.print(player.name + " (-1 to quit, 0 to skip) : ");

                Long card = null;
                while (card == null) {
                    try {
                        card = Long.valueOf(br.readLine());
                    } catch (Exception e) {
                        System.out.println("error number is mandatory, retry.");
                    }
                }

                // -1 => leave.
                if (card == -1) {
                    System.out.println("Bye.");
                    System.exit(0);
                }

                // 0 => next player
                if (card == 0) {
                    player = game.skip(player.index).nextPlayer();
                    continue;
                }

                if (game.shouldPlay(card) && player.play(card)) {
                    player = game.updateRound(card).nextPlayer();
                } else {
                    System.out.println("card is not in the player deck or lower than round card, retry.");
                }
            }

            player = game.currentPlayer();
            game.newRound(player.index);
            System.out.println(player.name + " wins this round");
        }

        System.out.println("game is over");
    }
}
