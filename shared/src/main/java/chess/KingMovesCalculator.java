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

        // check valid moves for a WHITE king
        if (pieceColor == ChessGame.TeamColor.WHITE) {

            if (board.getPiece(left) == null || board.getPiece(left).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, left, null);
                al.add(m);
            }
            if (board.getPiece(right) == null || board.getPiece(left).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, right, null);
                al.add(m);
            }
            if (board.getPiece(up) == null || board.getPiece(left).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, up, null);
                al.add(m);
            }
            if (board.getPiece(down) == null || board.getPiece(left).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, down, null);
                al.add(m);
            }


            if (board.getPiece(leftUp) == null || board.getPiece(left).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, leftUp, null);
                al.add(m);
            }
            if (board.getPiece(leftDown) == null || board.getPiece(left).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, leftDown, null);
                al.add(m);
            }
            if (board.getPiece(rightUp) == null || board.getPiece(left).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, rightUp, null);
                al.add(m);
            }
            if (board.getPiece(rightDown) == null || board.getPiece(left).getTeamColor() != pieceColor) {
                ChessMove m = new ChessMove(position, rightDown, null);
                al.add(m);
            }
        }
        // check valid moves for a BLACK king
        else {

        }

        return al;
    }
}
