package chess;

import static java.lang.Math.abs;

public class EnPassantPackage {

    ChessBoard currentBoard;
    ChessMove lastMove;
    boolean lastMoveWasDouble;

    public EnPassantPackage(ChessBoard board, ChessMove last, boolean wasDouble) {
        currentBoard = board;
        lastMove = last;
        lastMoveWasDouble = wasDouble;
    }

    /**
     * returns the difference in row positions of two moves
     * @param move1 starting move
     * @param move2 ending move
     * @return absolute value of difference in row
     */
    public static int calcRowDifference(ChessPosition move1, ChessPosition move2) {
        int startRow = move1.getRow();
        int endRow = move2.getRow();

        return abs(endRow - startRow);
    }

    /**
     * checks for a double move for a pawn
     * @param m move to check for double
     * @return true if a move is a double for a pawn
     */
    public static boolean pawnDoubleMoveCheck(ChessMove m) {
        ChessPosition start = m.getStartPosition();
        ChessPosition end = m.getEndPosition();

        int dif = calcRowDifference(start, end);

        return dif > 1;
    }

    /**
     * checks conditions for a valid en passant move
     * @return returns true if all conditions for en passant are met
     */
    private boolean enPassantConditionsMet(ChessPiece piece, int row, int col, boolean isWhiteTeam, boolean isLeft) {
        if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
            return false;
        }
        if (!lastMoveWasDouble) {
            return false;
        }
        if (isWhiteTeam) {
            if (row != 5) {
                return false;
            }
        }
        else {
            if (row != 4) {
                return false;
            }
        }
        if (isLeft) {
            if ((col-1) < 1) {
                return false;
            }
        }
        else {
            if ((col+1) > 8) {
                return false;
            }
        }
        return true;
    }

    /**
     * returns a valid en passant move
     * @return an en passant move
     */
    private ChessMove getEnPassantMove(int row, int col, ChessPosition startPosition, boolean isWhiteTeam, boolean isLeft) {
        ChessPosition sidePos;
        if (isLeft) {
            sidePos = new ChessPosition(row, col-1);
        }
        else {
            sidePos = new ChessPosition(row, col+1);
        }
        ChessPiece pieceSidePos = currentBoard.getPiece(sidePos);

        if (pieceSidePos != null && pieceSidePos.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (sidePos.equals(lastMove.getEndPosition())) {
                int rowFactor;
                int columnFactor;
                if (isWhiteTeam) {
                    rowFactor = 1;
                }
                else {
                    rowFactor = -1;
                }
                if (isLeft) {
                    columnFactor = -1;
                }
                else {
                    columnFactor = 1;
                }
                ChessPosition endPos = new ChessPosition(row+rowFactor, col+columnFactor);

                return new ChessMove(startPosition, endPos, null);
            }
        }
        return null;
    }

    /**
     * Checks for a valid left en passant move for white team
     */
    public ChessMove enPassantLeftWhiteCheck(ChessPiece piece, ChessPosition startPosition) {
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        if (!enPassantConditionsMet(piece, row, col, true, true)) {
            return  null;
        }
        return getEnPassantMove(row, col, startPosition, true, true);
    }

    /**
     * Checks for a valid right en passant move for white team
     */
    public ChessMove enPassantRightWhiteCheck(ChessPiece piece, ChessPosition startPosition) {
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        if (!enPassantConditionsMet(piece, row, col, true, false)) {
            return  null;
        }
        return getEnPassantMove(row, col, startPosition, true, false);
    }

    /**
     * Checks for a valid left en passant move for black team
     */
    public ChessMove enPassantLeftBlackCheck(ChessPiece piece, ChessPosition startPosition) {
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        if (!enPassantConditionsMet(piece, row, col, false, true)) {
            return  null;
        }
        return getEnPassantMove(row, col, startPosition, false, true);
    }

    /**
     * Checks for a valid right en passant move for black team
     */
    public ChessMove enPassantRightBlackCheck(ChessPiece piece, ChessPosition startPosition) {
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        if (!enPassantConditionsMet(piece, row, col, false, false)) {
            return  null;
        }
        return getEnPassantMove(row, col, startPosition, false, false);
    }
}
