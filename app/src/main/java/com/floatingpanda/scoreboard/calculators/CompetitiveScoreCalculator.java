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
 * Calculates scores for competitive games. A player or team's competitive score is calculated as
 *
 *      (n + 1 - pos) * dif
 *
 * where n is the number of players or teams in the game, pos is the current player or team's
 * finishing position in the game (e.g. 1st, 2nd, 3rd), and dif is the game's difficulty (an int
 * value between 1 and 5 (inclusive)).
 */
public class CompetitiveScoreCalculator {
    public CompetitiveScoreCalculator() {   }

    /**
     * Takes a player or team's position, the total number of positions in the game, and the game's
     * difficulty, and from this calculates and returns a score using the formula in the class
     * javadoc.
     * @param position current team or player's position.
     * @param numberOfPositions total number of positions (usually total number of players/teams)
     * @param difficulty difficulty of competitive game played
     * @return
     */
    public int calculateCompetitiveScore(int position, int numberOfPositions, int difficulty) {
        return difficulty * (numberOfPositions + 1 - position);
    }
}
