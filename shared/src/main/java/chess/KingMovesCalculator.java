package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMoveCalculator {

    private ChessBoard board;
    private ChessPosition position;
    private ChessGame.TeamColor pieceColor;

    KingMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor) {
        this.board = board;
        this.position = myPosition;
        this.pieceColor = pieceColor;
    }

    public Collection<ChessMove> pieceMoves() {

        Collection<ChessMove> al = new ArrayList<>();
        int row = position.getRow();
        int column = position.getColumn();

        // possible end positions
        ChessPosition left = new ChessPosition(row, column-1);
        ChessPosition right = new ChessPosition(row, column+1);
        ChessPosition up = new ChessPosition(row+1, column);
        ChessPosition down = new ChessPosition(row-1, column);

        ChessPosition leftUp = new ChessPosition(row+1, column-1);
        ChessPosition leftDown = new ChessPosition(row-1, column-1);
        ChessPosition rightUp = new ChessPosition(row+1, column+1);
        ChessPosition rightDown = new ChessPosition(row-1, column+1);

        // check which end positions are valid

        if (checkForValidEnd(left)) {
            ChessMove m = new ChessMove(position, left, null);
            al.add(m);
        }
        if (checkForValidEnd(right)) {
            ChessMove m = new ChessMove(position, right, null);
            al.add(m);
        }
        if (checkForValidEnd(up)) {
            ChessMove m = new ChessMove(position, up, null);
            al.add(m);
        }
        if (checkForValidEnd(down)) {
            ChessMove m = new ChessMove(position, down, null);
            al.add(m);
        }


        if (checkForValidEnd(leftUp)) {
            ChessMove m = new ChessMove(position, leftUp, null);
            al.add(m);
        }
        if (checkForValidEnd(leftDown)) {
            ChessMove m = new ChessMove(position, leftDown, null);
            al.add(m);
        }
        if (checkForValidEnd(rightUp)) {
            ChessMove m = new ChessMove(position, rightUp, null);
            al.add(m);
        }
        if (checkForValidEnd(rightDown)) {
            ChessMove m = new ChessMove(position, rightDown, null);
            al.add(m);
        }

        return al;
    }

    private boolean checkForValidEnd(ChessPosition end) {
        int endRow = end.getRow();
        int endColumn = end.getColumn();

        // check for out of bounds
        if (endRow <= 0 || endRow >= 9 || endColumn <= 0 || endColumn >= 9 ) {
            return false;
        }
        if (board.getPiece(end) == null || board.getPiece(end).getTeamColor() != pieceColor) {
            return true;
        }

        return false;


    }
}
