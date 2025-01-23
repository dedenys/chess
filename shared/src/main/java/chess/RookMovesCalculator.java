package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMoveCalculator{

    private ChessBoard board;
    private ChessPosition position;
    private ChessGame.TeamColor pieceColor;

    RookMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor) {
        this.board = board;
        this.position = myPosition;
        this.pieceColor = pieceColor;
    }

    @Override
    public Collection<ChessMove> pieceMoves() {

        Collection<ChessMove> al = new ArrayList<>();
        int row = position.getRow();
        int column = position.getColumn();

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
