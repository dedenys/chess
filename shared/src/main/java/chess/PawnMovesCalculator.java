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
            ChessPosition end = new ChessPosition(row+1, column);

            // single forward movement
            if (board.getPiece(end) == null) {
                ChessMove newMove;

                if (end.getRow() == 7) {
                    newMove = new ChessMove(position, end, null);
                }
                else {
                    newMove = new ChessMove(position, end, null);
                }
                al.add(newMove);
            }

            // starting double movement
            if (position.getRow() == 2) {
                ChessPosition end2 = new ChessPosition(row+1, column);

                if (board.getPiece(end2) == null) {
                    ChessMove newMove = new ChessMove(position, end2, null);

                    al.add(newMove);
                }

            }

            //
        }
        // black team movement
        else {
            ChessPosition end = new ChessPosition(row-1, column);
        }

        return al;
    }
}
