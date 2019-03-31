package com.javarush;

public class MoveEfficiency implements Comparable<MoveEfficiency> {
    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        int emptyTilesDiff = this.numberOfEmptyTiles - o.numberOfEmptyTiles;
        if (emptyTilesDiff != 0) {
            return emptyTilesDiff > 0 ? 1 : -1;
        } else {
//            return this.score > o.score ? 1 : this.score < o.score ? -1 : 0;
            return Integer.compare(this.score, o.score);
        }
    }
}
