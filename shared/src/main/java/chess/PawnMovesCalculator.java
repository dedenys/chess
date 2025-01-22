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

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            ChessPosition end = new ChessPosition(row+1, column);

            if (board.getPiece(end) == null) {
                ChessMove newMove = new ChessMove(position, end, null);
            }
        }

        return al;
    }
}
