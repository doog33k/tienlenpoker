package com.sokaris.tienlenpoker.core;

/**
 * Created by sylvek on 27/01/15.
 */
public class Player {

    final String name;

    final int index;

    // bit mask for each card.
    // ... 0001 = 3pic
    // ... 0010 = 3trefle
    // ... 0100 = 3carreau
    // ... 1000 = 3coeur
    // ... 1001 = 3pic+3coeur
    long deck;

    public Player(final int index, final String name)
    {
        this.index = index;
        this.name = name;
    }

    public final boolean canPlayThisGame()
    {
        return this.deck > 0L;
    }

    public boolean cardIsOnDeck(long card)
    {
        return (this.deck & card) == card;
    }

    public boolean play(long card)
    {
        // check if card is in the player deck
        if (this.cardIsOnDeck(card)) {
            this.deck &= ~card;
            return true;
        }

        return false;
    }

    @Override
    public String toString()
    {
        return this.index + ": " + this.name + "[" + this.deck + "]: " + Long.toBinaryString(this.deck);
    }
}
