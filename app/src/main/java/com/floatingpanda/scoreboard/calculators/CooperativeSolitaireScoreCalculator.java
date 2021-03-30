/*
ScoreBoard

Copyright © 2020 Adam Poole

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.floatingpanda.scoreboard.calculators;

/**
 * Calculates the score for a team or a single player in a cooperative or solitaire game. Scores for
 * these games are calculated based on whether the team/player won or lost the game. If the
 * team/player lost, they got 0 points. If the team/player won, they get the game's difficulty in
 * points.
 */
public class CooperativeSolitaireScoreCalculator {
    public CooperativeSolitaireScoreCalculator() {

    }

    /**
     * Takes a boolean representing whether the game was won, and takes the game's difficulty (an
     * int between 1 and 5 (inclusive)) and calculates the score based on whether the game was won
     * or lost. If the game was won, score = difficulty. If the game was lost, score = 0.
     * @param won whether game was won or lost
     * @param difficulty difficulty of game played (int between 1 and 5 (inclusive))
     * @return
     */
    public int calculateCooperativeSolitaireScore(boolean won, int difficulty) {
        if (won) {
            return difficulty;
        }

        return 0;
    }
}
