package Tetris;

import java.util.Random;

public class Shape {
    private Tetrominoes pieceShape;                     //Current piece shape.
    private int[][] coords;                             //actual coordinates of a tetris piece.
    private int[][][] coordsTable;                      //holds all possible coordinate values of our tetris pieces.
    public enum Tetrominoes {                           //enum with all pieces.
        NoShape, ZShape, SShape, LineShape,
        TShape, SquareShape, LShape, MirroredLShape
    };

    public Shape() {                                    //To start game we set NoShape shape first.
        coords = new int[4][2];
        setShape(Tetrominoes.NoShape);
    }
    private void setX(int index, int x) {
        coords[index][0] = x;
    }
    private void setY(int index, int y) {
        coords[index][1] = y;
    }
    public void setShape(Tetrominoes shape) {           //Initialize Matrix with figures.
        coordsTable = new int[][][] {
                { { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },
                { { 0, -1 },  { 0, 0 },   { -1, 0 },  { -1, 1 } },
                { { 0, -1 },  { 0, 0 },   { 1, 0 },   { 1, 1 } },
                { { 0, -1 },  { 0, 0 },   { 0, 1 },   { 0, 2 } },
                { { -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } },
                { { 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } },
                { { -1, -1 }, { 0, -1 },  { 0, 0 },   { 0, 1 } },
                { { 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } }
        };
        //Find Shapes from matrix.
        for (int i = 0; i < 4 ; i++) {
            for (int j = 0; j < 2; ++j) {
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
            }
        }
        pieceShape = shape;

    }
    public Tetrominoes getShape()  {
        return pieceShape;
    }


    public int x(int index) {
        return coords[index][0];
    }
    //Get Coordinates x y
    public int y(int index) {
        return coords[index][1];
    }
    //Get Coordinates x y
    public void setRandomShape() {                      //set shape by Random
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7+1;
        Tetrominoes[] values = Tetrominoes.values();
        setShape(values[x]);
    }

    public Shape rotateShape() {
        if (pieceShape == Tetrominoes.SquareShape)      //if it is square no need to rotate
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {                   //Change coordinates
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }
        return result;
    }                        //Rotate the shape
    public int minY() {                                 //Get minimal free row
        int m = coords[0][1];
        for (int i=0; i < 4; i++) {
            m = Math.min(m, coords[i][1]);
        }
        return m;
    }
}
