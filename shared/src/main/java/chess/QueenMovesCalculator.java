package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMoveCalculator{

    private ChessBoard board;
    private ChessPosition position;
    private ChessGame.TeamColor pieceColor;

    QueenMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor) {
        this.board = board;
        this.position = myPosition;
        this.pieceColor = pieceColor;
    }

    @Override
    public Collection<ChessMove> pieceMoves() {

        Collection<ChessMove> al = new ArrayList<>();
        int row = position.getRow();
        int column = position.getColumn();

        // #### straight movement ######

        // left movement
        ChessPosition left = new ChessPosition(row, column-1);
        while (outOfBoundsCheck(left)) {

            if (board.getPiece(left) == null) {
                ChessMove m = new ChessMove(position, left, null);
                al.add(m);

                left = new ChessPosition(row, left.getColumn()-1);
            }
            else if (board.getPiece(left) != null && board.getPiece(left).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, left, null);
                al.add(m);

                break;
            }
            else {
                break;
            }

        }

        // right movement
        ChessPosition right = new ChessPosition(row, column+1);
        while (outOfBoundsCheck(right)) {

            if (board.getPiece(right) == null) {
                ChessMove m = new ChessMove(position, right, null);
                al.add(m);

                right = new ChessPosition(row, right.getColumn()+1);
            }
            else if (board.getPiece(right) != null && board.getPiece(right).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, right, null);
                al.add(m);

                break;
            }
            else {
                break;
            }

        }

        // up movement

        ChessPosition up = new ChessPosition(row+1, column);
        while (outOfBoundsCheck(up)) {

            if (board.getPiece(up) == null) {
                ChessMove m = new ChessMove(position, up, null);
                al.add(m);

                up = new ChessPosition(up.getRow()+1, column);
            }
            else if (board.getPiece(up) != null && board.getPiece(up).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, up, null);
                al.add(m);

                break;
            }
            else {
                break;
            }

        }
        // down movement

        ChessPosition down = new ChessPosition(row-1, column);
        while (outOfBoundsCheck(down)) {

            if (board.getPiece(down) == null) {
                ChessMove m = new ChessMove(position, down, null);
                al.add(m);

                down = new ChessPosition(down.getRow()-1, column);
            }
            else if (board.getPiece(down) != null && board.getPiece(down).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, down, null);
                al.add(m);

                break;
            }
            else {
                break;
            }

        }

        // #### diagonal movement ####

        // left up movement
        ChessPosition leftUp = new ChessPosition(row+1, column-1);
        while (outOfBoundsCheck(leftUp)) {

            if (board.getPiece(leftUp) == null) {
                ChessMove m = new ChessMove(position, leftUp, null);
                al.add(m);

                leftUp = new ChessPosition(leftUp.getRow()+1, leftUp.getColumn()-1);
            }
            else if (board.getPiece(leftUp) != null && board.getPiece(leftUp).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, leftUp, null);
                al.add(m);

                break;
            }
            else {
                break;
            }

        }

        // left down movement
        ChessPosition leftDown = new ChessPosition(row-1, column-1);
        while (outOfBoundsCheck(leftDown)) {

            if (board.getPiece(leftDown) == null) {
                ChessMove m = new ChessMove(position, leftDown, null);
                al.add(m);

                leftDown = new ChessPosition(leftDown.getRow()-1, leftDown.getColumn()-1);
            }
            else if (board.getPiece(leftDown) != null && board.getPiece(leftDown).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, leftDown, null);
                al.add(m);

                break;
            }
            else {
                break;
            }

        }

        // right up movement
        ChessPosition rightUp = new ChessPosition(row+1, column+1);
        while (outOfBoundsCheck(rightUp)) {

            if (board.getPiece(rightUp) == null) {
                ChessMove m = new ChessMove(position, rightUp, null);
                al.add(m);

                rightUp = new ChessPosition(rightUp.getRow()+1, rightUp.getColumn()+1);
            }
            else if (board.getPiece(rightUp) != null && board.getPiece(rightUp).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, rightUp, null);
                al.add(m);

                break;
            }
            else {
                break;
            }

        }

        // right down movement
        ChessPosition rightDown = new ChessPosition(row-1, column+1);
        while (outOfBoundsCheck(rightDown)) {

            if (board.getPiece(rightDown) == null) {
                ChessMove m = new ChessMove(position, rightDown, null);
                al.add(m);

                rightDown = new ChessPosition(rightDown.getRow()-1, rightDown.getColumn()+1);
            }
            else if (board.getPiece(rightDown) != null && board.getPiece(rightDown).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, rightDown, null);
                al.add(m);

                break;
            }
            else {
                break;
            }

        }

        return al;
    }

    private boolean outOfBoundsCheck(ChessPosition end) {
        int endRow = end.getRow();
        int endColumn = end.getColumn();

        // check for out of bounds
        if (endRow <= 0 || endRow >= 9 || endColumn <= 0 || endColumn >= 9 ) {
            return false;
        }
        else {
            return true;
        }
    }
}
