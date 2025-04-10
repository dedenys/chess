package client;

import chess.ChessPosition;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ClientUtilities {

    static int setHighlight(String color, ChessPosition selectedPiece, int boardRow, int boardCol, List<ChessPosition> endPositions) {
        int rowIndex;
        int colIndex;
        if (Objects.equals(color, "WHITE")) {
            rowIndex = 8-boardRow;
            colIndex = boardCol+1;
        }
        else {
            rowIndex = boardRow+1;
            colIndex = 8-boardCol;
        }

        int highlight = 0;
        if (selectedPiece != null) {
            if (selectedPiece.getRow() == (rowIndex) && selectedPiece.getColumn() == (colIndex)) {
                highlight = 2;
            }
        }

        if (endPositions != null) {
            for (ChessPosition p: endPositions) {
                if (p.getRow() == (rowIndex) && p.getColumn() == (colIndex)) {
                    highlight = 1;
                }
            }
        }
        return highlight;
    }

    static int letterToPositionMove(String letter) {
        int col = 0;
        switch (letter) {
            case "a" -> col = 8;
            case "b" -> col = 7;
            case "c" -> col = 6;
            case "d" -> col = 5;
            case "e" -> col = 4;
            case "f" -> col = 3;
            case "g" -> col = 2;
            case "h" -> col = 1;
        }
        return col;
    }

    static boolean validLetter(String letter, String[] lettersBlack) {
        List<String> letterList = Arrays.asList(lettersBlack);
        return letterList.contains(letter);
    }

    static boolean validNumber(String number, String[] numbersBlack) {
        List<String> numberList = Arrays.asList(numbersBlack);
        return numberList.contains(number);
    }

}
