package com.javarush;

import java.util.*;

public class Model {
    private final static int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    int score;
    int maxTile;
    private boolean isSaveNeeded = true;
    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();

    public Model() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        score = 0;
        maxTile = 0;
        resetGameTiles();
    }

    public void resetGameTiles() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private void saveState(Tile[][] arr){

        if(arr == null || arr.length == 0) return;
        Tile[][] tArr = new Tile[arr.length][arr[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                tArr[i][j] = new Tile(arr[i][j].value);
            }
        }

        previousStates.push(tArr);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback(){
        if(!previousStates.isEmpty() && !previousScores.isEmpty()){
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }

    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public int getScore() {
        return score;
    }

    public boolean canMove(){
        if(!getEmptyTiles().isEmpty()) return true;

        boolean res = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH-1; j++) {
                res |= (gameTiles[i][j].value == gameTiles[i][j+1].value);
                res |= (gameTiles[j][i].value == gameTiles[j+1][i].value);
            }
        }

        return res;
    }

    public boolean hasBoardChanged(){
        if(gameTiles == null || previousStates.isEmpty())
            return false;

        Tile[][] stackArr = previousStates.peek();

        boolean changed = false;
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[0].length; j++) {
                if(gameTiles[i][j].value != stackArr[i][j].value){
                    changed = true;
                    break;
                }
            }
            if(changed)
                break;
        }

        return changed;
    }

    private MoveEfficiency getMoveEfficiency(Move move){
        MoveEfficiency moveEfficiency;
        move.move();
        if(hasBoardChanged()) {
            moveEfficiency = new MoveEfficiency(getNumberOfEmptyTiles(gameTiles), score, move);
        } else {
            moveEfficiency = new MoveEfficiency(-1, 0, move);
        }
        rollback();
        return moveEfficiency;
    }

    private int getNumberOfEmptyTiles(Tile[][] arr){
        int res = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                if(arr[i][j].isEmpty())
                    res++;
            }
        }
        return res;
    }


    public void  randomMove(){
        int n = ((int)(Math.random()*100))%4;
        switch (n){
            case 0:
                left();
                break;
            case 1:
                right();
                break;
            case 2:
                up();
                break;
            case 3:
                down();
                break;
        }
    }

    public void autoMove(){
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4, Collections.reverseOrder());
        priorityQueue.offer(getMoveEfficiency(this::left));
        priorityQueue.offer(getMoveEfficiency(this::right));
        priorityQueue.offer(getMoveEfficiency(this::up));
        priorityQueue.offer(getMoveEfficiency(this::down));
        priorityQueue.peek().getMove().move();
    }

    public void left(){
        saveState(gameTiles);
        boolean changed = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            changed |= compressTiles(gameTiles[i]);
            changed |= mergeTiles(gameTiles[i]);
        }

        if(changed){
            addTile();
        }
    }

    public void right(){
        saveState(gameTiles);
        boolean changed = false;
        Tile[] arr = new Tile[FIELD_WIDTH];

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                arr[j] = gameTiles[i][FIELD_WIDTH - j - 1];
            }
            changed |= compressTiles(arr);
            changed |= mergeTiles(arr);
        }

        if(changed){
            addTile();
        }
    }


    public void up(){
        saveState(gameTiles);
        boolean changed = false;
        Tile[] arr = new Tile[FIELD_WIDTH];

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                arr[j] = gameTiles[j][i];
            }
            changed |= compressTiles(arr);
            changed |= mergeTiles(arr);
        }

        if(changed){
            addTile();
        }
    }


    public void down(){
        saveState(gameTiles);
        boolean changed = false;
        Tile[] arr = new Tile[FIELD_WIDTH];

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                arr[j] = gameTiles[FIELD_WIDTH - j - 1][i];
            }
            changed |= compressTiles(arr);
            changed |= mergeTiles(arr);
        }

        if(changed){
            addTile();
        }
    }


    private List<Tile> getEmptyTiles() {
        List<Tile> result = new ArrayList<>();

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].isEmpty())
                    result.add(gameTiles[i][j]);
            }
        }
        return result;
    }


    private void addTile() {
        List<Tile> filds = getEmptyTiles();
        if (filds == null || filds.isEmpty()) return;

        int rndTile = (int) (Math.random() * filds.size());
        filds.get(rndTile).value = Math.random() < 0.9 ? 2 : 4;
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean res = false;
        int wri = 0;
        for (int i = 0; i < tiles.length; i++) {
            if (!tiles[i].isEmpty()) {
                if (wri != i) {
                    tiles[wri++].value = tiles[i].value;
                    res = true;
                } else {
                    wri++;
                }
            }
        }

        while (wri < tiles.length) {
            tiles[wri++].value = 0;
        }

        return res;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean res = false;
        for (int i = 0; i < tiles.length - 1; i++) {
            if (!tiles[i].isEmpty() && tiles[i].value == tiles[i + 1].value) {
                tiles[i].value *= 2;
                tiles[i + 1].value = 0;
                score += tiles[i].value;
                if (maxTile < tiles[i].value) maxTile = tiles[i].value;
                i++;
                res = true;
            }
        }

        if (res) {
            compressTiles(tiles);
            return true;
        } else {
            return false;
        }
    }

    public void testMethod() {
        Tile[] tiles = {
                new Tile(2),
                new Tile(2),
                new Tile(0),
                new Tile(0),
                new Tile(2),
                new Tile(2),
                new Tile(4),
        };

        boolean res = false;
        res = compressTiles(tiles);
        res = mergeTiles(tiles);
        res = mergeTiles(tiles);
        res = compressTiles(tiles);
        res = mergeTiles(tiles);

        res = false;
        for (int i = 0; i < 100; i++) {
            testMethodPrint();
            System.out.println("=========================================  " + canMove());

            switch (new Scanner(System.in).nextLine()) {
                case "w":
                    up();
                    break;
                case "s":
                    down();
                    break;
                case "a":
                    left();
                    break;
                case "d":
                    right();
                    break;
                default:
                    res = true;
            }
            if(res)break;
            addTile();
            addTile();
            addTile();
            addTile();
            addTile();
            addTile();
            addTile();
            addTile();
        }
    }

    public void testMethodPrint() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                System.out.print(gameTiles[i][j].value + " ");
            }
            System.out.println();
            System.out.println();
        }
    }
}
