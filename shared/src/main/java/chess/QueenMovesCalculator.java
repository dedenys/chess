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

        striaghtMovement(row, column, al);
        diagonalMovement(row, column, al);

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

    private void checkPosition(ChessPosition pos, int row, int column, Collection<ChessMove> al, int rowDif, int colDif) {
        while (outOfBoundsCheck(pos)) {

            if (board.getPiece(pos) == null) {
                ChessMove m = new ChessMove(position, pos, null);
                al.add(m);

                pos = new ChessPosition(pos.getRow()+rowDif, pos.getColumn()+colDif);
            }
            else if (board.getPiece(pos) != null && board.getPiece(pos).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, pos, null);
                al.add(m);

                break;
            }
            else {
                break;
            }

        }
    }

    private void striaghtMovement(int row, int column, Collection<ChessMove> al) {
        // left movement
        ChessPosition left = new ChessPosition(row, column - 1);
        checkPosition(left, row, column, al, 0, -1);

        // right movement
        ChessPosition right = new ChessPosition(row, column + 1);
        checkPosition(right, row, column, al, 0, 1);

        // up movement

        ChessPosition up = new ChessPosition(row + 1, column);
        checkPosition(up, row, column, al, 1, 0);
        // down movement

        ChessPosition down = new ChessPosition(row - 1, column);
        checkPosition(down, row, column, al, -1, 0);
    }

    private void diagonalMovement(int row, int column, Collection<ChessMove> al) {
        // #### diagonal movement ####

        // left up movement
        ChessPosition leftUp = new ChessPosition(row+1, column-1);
        checkPosition(leftUp, row, column, al, 1, -1);

        // left down movement
        ChessPosition leftDown = new ChessPosition(row-1, column-1);
        checkPosition(leftDown, row, column, al, -1, -1);

        // right up movement
        ChessPosition rightUp = new ChessPosition(row+1, column+1);
        checkPosition(rightUp, row, column, al, 1, 1);

        // right down movement
        ChessPosition rightDown = new ChessPosition(row-1, column+1);
        checkPosition(rightDown, row, column, al, -1, 1);
    }
}
