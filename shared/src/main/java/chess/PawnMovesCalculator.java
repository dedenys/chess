package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class PawnMovesCalculator implements PieceMoveCalculator {

    private ChessBoard board;
    private ChessPosition position;
    private ChessGame.TeamColor pieceColor;

    PawnMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor) {
        this.board = board;
        this.position = myPosition;
        this.pieceColor = pieceColor;
    }

    @Override
    public Collection<ChessMove> pieceMoves() {
        Collection<ChessMove> al = new ArrayList<>();

        int row = position.getRow();
        int column = position.getColumn();

        // WHITE
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            forwardCheck(row, column, al, 1, 2, 7);
            captureCheck(row, column, al, ChessGame.TeamColor.BLACK, 1, 7);
        }
        // BLACK
        else {
            forwardCheck(row, column, al, -1, 7, 2);
            captureCheck(row, column, al, ChessGame.TeamColor.WHITE, -1, 2);
        }

        return al;
    }

    private void forwardCheck(int row, int col, Collection<ChessMove> al, int rowDif, int startRow, int endRow) {
        // start row
        if (row == startRow) {
            ChessPosition forward = new ChessPosition(row+rowDif, col);
            ChessPosition doubleForward = new ChessPosition(row+(rowDif*2), col);

            if (board.getPiece(forward) == null) {
                ChessMove m = new ChessMove(position, forward, null);
                al.add(m);

                if (board.getPiece(doubleForward) == null) {
                    ChessMove m2 = new ChessMove(position, doubleForward, null);
                    al.add(m2);
                }
            }
        }
        // end row
        else if (row == endRow) {
            ChessPosition up = new ChessPosition(row+rowDif, col);
            promotionForward(up, al);
        }
        // average case
        else {
            ChessPosition up = new ChessPosition(row+rowDif, col);

            if (board.getPiece(up) == null) {
                ChessMove m = new ChessMove(position, up, null);
                al.add(m);
            }
        }}

    private void captureCheck(int row, int col, Collection<ChessMove> al, ChessGame.TeamColor c, int rowDif, int endRow) {
        ChessPosition left = new ChessPosition(row+rowDif, col-1);
        ChessPosition right = new ChessPosition(row+rowDif, col+1);

        if (row == endRow) {
            promotionCapture(left, al, c);
            promotionCapture(right, al, c);
        }
        else {
            if (validPawnCheck(left, c)) {
                ChessMove m = new ChessMove(position, left, null);
                al.add(m);
            }
            if (validPawnCheck(right, c)) {
                ChessMove m = new ChessMove(position, right, null);
                al.add(m);
            }
        }
    }

    private boolean validPawnCheck(ChessPosition end, ChessGame.TeamColor color) {
        if (checkForValidEnd(end)) {
            ChessPiece p = board.getPiece(end);
            return p != null && p.getTeamColor() == color;
        }
        return false;
    }

    private void promotion(ChessPosition pos, Collection<ChessMove> al) {
        ChessMove m = new ChessMove(position, pos, ChessPiece.PieceType.KNIGHT);
        ChessMove m2 = new ChessMove(position, pos, ChessPiece.PieceType.BISHOP);
        ChessMove m3 = new ChessMove(position, pos, ChessPiece.PieceType.QUEEN);
        ChessMove m4 = new ChessMove(position, pos, ChessPiece.PieceType.ROOK);

        al.add(m);
        al.add(m2);
        al.add(m3);
        al.add(m4);
    }

    private void promotionForward(ChessPosition pos, Collection<ChessMove> al) {
        if (board.getPiece(pos) == null) {
            promotion(pos, al);
        }
    }

    private void promotionCapture(ChessPosition pos, Collection<ChessMove> al, ChessGame.TeamColor c) {
        if (validPawnCheck(pos, c)) {
            promotion(pos, al);
        }
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
