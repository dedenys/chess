package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMoveCalculator{

    private ChessBoard board;
    private ChessPosition position;
    private ChessGame.TeamColor pieceColor;

    KnightMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor) {
        this.board = board;
        this.position = myPosition;
        this.pieceColor = pieceColor;
    }

    @Override
    public Collection<ChessMove> pieceMoves() {

        Collection<ChessMove> al = new ArrayList<>();
        int row = position.getRow();
        int column = position.getColumn();

        ChessPosition left1 = new ChessPosition(row+1, column-2);
        ChessPosition left2 = new ChessPosition(row-1, column-2);

        ChessPosition right1 = new ChessPosition(row+1, column+2);
        ChessPosition right2 = new ChessPosition(row-1, column+2);

        ChessPosition up1 = new ChessPosition(row+2, column-1);
        ChessPosition up2 = new ChessPosition(row+2, column+1);

        ChessPosition down1 = new ChessPosition(row-2, column-1);
        ChessPosition down2 = new ChessPosition(row-2, column+1);

        if (checkForValidEnd(left1)) {
            ChessMove m = new ChessMove(position, left1, null);
            al.add(m);
        }
        if (checkForValidEnd(left2)) {
            ChessMove m = new ChessMove(position, left2, null);
            al.add(m);
        }
        if (checkForValidEnd(right1)) {
            ChessMove m = new ChessMove(position, right1, null);
            al.add(m);
        }
        if (checkForValidEnd(right2)) {
            ChessMove m = new ChessMove(position, right2, null);
            al.add(m);
        }
        if (checkForValidEnd(up1)) {
            ChessMove m = new ChessMove(position, up1, null);
            al.add(m);
        }
        if (checkForValidEnd(up2)) {
            ChessMove m = new ChessMove(position, up2, null);
            al.add(m);
        }
        if (checkForValidEnd(down1)) {
            ChessMove m = new ChessMove(position, down1, null);
            al.add(m);
        }
        if (checkForValidEnd(down2)) {
            ChessMove m = new ChessMove(position, down2, null);
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
