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

        // white team movement
        if (pieceColor == ChessGame.TeamColor.WHITE) {

            // start row
            if (row == 2) {
                ChessPosition up = new ChessPosition(row+1, column);
                ChessPosition doubleup = new ChessPosition(row+2, column);

                if (board.getPiece(up) == null) {
                    ChessMove m = new ChessMove(position, up, null);
                    al.add(m);

                    if (board.getPiece(doubleup) == null) {
                        ChessMove m2 = new ChessMove(position, doubleup, null);
                        al.add(m2);
                    }
                }
            }
            // end row
            else if (row == 7) {
                ChessPosition up = new ChessPosition(row+1, column);

                if (board.getPiece(up) == null) {
                    ChessMove m = new ChessMove(position, up, ChessPiece.PieceType.KNIGHT);
                    ChessMove m2 = new ChessMove(position, up, ChessPiece.PieceType.BISHOP);
                    ChessMove m3 = new ChessMove(position, up, ChessPiece.PieceType.QUEEN);
                    ChessMove m4 = new ChessMove(position, up, ChessPiece.PieceType.ROOK);

                    al.add(m);
                    al.add(m2);
                    al.add(m3);
                    al.add(m4);
                }
            }
            else {
                ChessPosition up = new ChessPosition(row+1, column);

                if (board.getPiece(up) == null) {
                    ChessMove m = new ChessMove(position, up, null);
                    al.add(m);
                }
            }

            // capture check
            if (row == 7) {
                ChessPosition leftUp = new ChessPosition(row+1, column-1);
                ChessPosition rightUp = new ChessPosition(row+1, column+1);

                if (checkForValidEnd(leftUp) && board.getPiece(leftUp) != null && board.getPiece(leftUp).getTeamColor() == ChessGame.TeamColor.BLACK) {
                    ChessMove m = new ChessMove(position, leftUp, ChessPiece.PieceType.KNIGHT);
                    ChessMove m2 = new ChessMove(position, leftUp, ChessPiece.PieceType.BISHOP);
                    ChessMove m3 = new ChessMove(position, leftUp, ChessPiece.PieceType.QUEEN);
                    ChessMove m4 = new ChessMove(position, leftUp, ChessPiece.PieceType.ROOK);

                    al.add(m);
                    al.add(m2);
                    al.add(m3);
                    al.add(m4);
                }
                if (checkForValidEnd(rightUp) && board.getPiece(rightUp) != null && board.getPiece(rightUp).getTeamColor() == ChessGame.TeamColor.BLACK) {
                    ChessMove m = new ChessMove(position, rightUp, ChessPiece.PieceType.KNIGHT);
                    ChessMove m2 = new ChessMove(position, rightUp, ChessPiece.PieceType.BISHOP);
                    ChessMove m3 = new ChessMove(position, rightUp, ChessPiece.PieceType.QUEEN);
                    ChessMove m4 = new ChessMove(position, rightUp, ChessPiece.PieceType.ROOK);

                    al.add(m);
                    al.add(m2);
                    al.add(m3);
                    al.add(m4);
                }
            }
            else {
                ChessPosition leftUp = new ChessPosition(row+1, column-1);
                ChessPosition rightUp = new ChessPosition(row+1, column+1);

                if (checkForValidEnd(leftUp) && board.getPiece(leftUp) != null && board.getPiece(leftUp).getTeamColor() == ChessGame.TeamColor.BLACK) {
                    ChessMove m = new ChessMove(position, leftUp, null);
                    al.add(m);
                }
                if (checkForValidEnd(rightUp) && board.getPiece(rightUp) != null && board.getPiece(rightUp).getTeamColor() == ChessGame.TeamColor.BLACK) {
                    ChessMove m = new ChessMove(position, rightUp, null);
                    al.add(m);
                }
            }
        }
        // BLACK team movement

        else {

            // start row
            if (row == 7) {
                ChessPosition down = new ChessPosition(row-1, column);
                ChessPosition doubleDown = new ChessPosition(row-2, column);

                if (board.getPiece(down) == null) {
                    ChessMove m = new ChessMove(position, down, null);
                    al.add(m);

                    if (board.getPiece(doubleDown) == null) {
                        ChessMove m2 = new ChessMove(position, doubleDown, null);
                        al.add(m2);
                    }
                }

            }
            // end row
            else if (row == 2) {
                ChessPosition down = new ChessPosition(row-1, column);

                if (board.getPiece(down) == null) {
                    ChessMove m = new ChessMove(position, down, ChessPiece.PieceType.KNIGHT);
                    ChessMove m2 = new ChessMove(position, down, ChessPiece.PieceType.BISHOP);
                    ChessMove m3 = new ChessMove(position, down, ChessPiece.PieceType.QUEEN);
                    ChessMove m4 = new ChessMove(position, down, ChessPiece.PieceType.ROOK);

                    al.add(m);
                    al.add(m2);
                    al.add(m3);
                    al.add(m4);
                }
            }
            else {
                ChessPosition down = new ChessPosition(row-1, column);

                if (board.getPiece(down) == null) {
                    ChessMove m = new ChessMove(position, down, null);
                    al.add(m);
                }
            }

            // capture check
            if (row == 2) {
                ChessPosition leftDown = new ChessPosition(row-1, column-1);
                ChessPosition rightDown = new ChessPosition(row-1, column+1);

                if (checkForValidEnd(leftDown) && board.getPiece(leftDown) != null && board.getPiece(leftDown).getTeamColor() == ChessGame.TeamColor.WHITE) {
                    ChessMove m = new ChessMove(position, leftDown, ChessPiece.PieceType.KNIGHT);
                    ChessMove m2 = new ChessMove(position, leftDown, ChessPiece.PieceType.BISHOP);
                    ChessMove m3 = new ChessMove(position, leftDown, ChessPiece.PieceType.QUEEN);
                    ChessMove m4 = new ChessMove(position, leftDown, ChessPiece.PieceType.ROOK);

                    al.add(m);
                    al.add(m2);
                    al.add(m3);
                    al.add(m4);
                }
                if (checkForValidEnd(rightDown) && board.getPiece(rightDown) != null && board.getPiece(rightDown).getTeamColor() == ChessGame.TeamColor.WHITE) {
                    ChessMove m = new ChessMove(position, rightDown, ChessPiece.PieceType.KNIGHT);
                    ChessMove m2 = new ChessMove(position, rightDown, ChessPiece.PieceType.BISHOP);
                    ChessMove m3 = new ChessMove(position, rightDown, ChessPiece.PieceType.QUEEN);
                    ChessMove m4 = new ChessMove(position, rightDown, ChessPiece.PieceType.ROOK);

                    al.add(m);
                    al.add(m2);
                    al.add(m3);
                    al.add(m4);
                }
            }
            else {
                ChessPosition leftDown = new ChessPosition(row-1, column-1);
                ChessPosition rightDown = new ChessPosition(row-1, column+1);

                if (checkForValidEnd(leftDown) && board.getPiece(leftDown) != null && board.getPiece(leftDown).getTeamColor() == ChessGame.TeamColor.WHITE) {
                    ChessMove m = new ChessMove(position, leftDown, null);
                    al.add(m);
                }
                if (checkForValidEnd(rightDown) && board.getPiece(rightDown) != null && board.getPiece(rightDown).getTeamColor() == ChessGame.TeamColor.WHITE) {
                    ChessMove m = new ChessMove(position, rightDown, null);
                    al.add(m);
                }
            }
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
