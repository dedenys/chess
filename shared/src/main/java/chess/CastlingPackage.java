package chess;

import java.util.Collection;

public class CastlingPackage {

    ChessBoard currentBoard;
    boolean whiteKingMoved = false;
    boolean whiteLeftRookMoved = false;
    boolean whiteRightRookMoved = false;

    boolean blackKingMoved = false;
    boolean blackLeftRookMoved = false;
    boolean blackRightRookMoved = false;

    public CastlingPackage(ChessBoard board, boolean wKing, boolean wLRook, boolean wRRook, boolean bKing, boolean bLRook, boolean bRRook) {
        currentBoard = board;
        whiteKingMoved = wKing;
        whiteLeftRookMoved = wLRook;
        whiteRightRookMoved = wRRook;

        blackKingMoved = bKing;
        blackLeftRookMoved = bLRook;
        blackRightRookMoved = bRRook;

    }

    private boolean isInCheck(ChessGame.TeamColor teamColor) {

        ChessPosition king = new ChessPosition(0, 0);

        // find the king for the teamColor
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                ChessPosition position = new ChessPosition(i + 1, j + 1);
                ChessPiece piece = currentBoard.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        king = position;
                    }
                }
            }
        }

        // iterate through opposing team pieces for a possible check
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isUnderAttack(i, j, teamColor, king)) {
                    return true;
                }
            }
        }
        // no check was found
        return false;
    }

    private boolean isUnderAttack(int i, int j, ChessGame.TeamColor teamColor, ChessPosition king) {
        ChessPosition position = new ChessPosition(i + 1, j + 1);
        ChessPiece piece = currentBoard.getPiece(position);

        if (piece != null && piece.getTeamColor() != teamColor) {

            Collection<ChessMove> moves = piece.pieceMoves(currentBoard, position);

            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(king)) {
                    return true;
                }
            }

        }
        return false;
    }

    private boolean leftCastleClear(ChessPosition one, ChessPosition two, ChessPosition three, boolean rookMoved) {
        ChessPiece oneP = currentBoard.getPiece(one);
        ChessPiece twoP = currentBoard.getPiece(two);
        ChessPiece threeP = currentBoard.getPiece(three);
        if (!rookMoved) {
            if  (oneP == null && twoP == null && threeP == null) {
                return true;
            }
        }
        return false;
    }

    private boolean rightCastleClear(ChessPosition one, ChessPosition two, boolean rookMoved) {
        ChessPiece oneP = currentBoard.getPiece(one);
        ChessPiece twoP = currentBoard.getPiece(two);
        if (!rookMoved) {
            if  (oneP == null && twoP == null) {
                return true;
            }
        }
        return false;
    }

    private boolean castlingInCheckChecker(ChessPosition m1, ChessPosition m2, ChessPosition start, ChessPiece p, ChessGame.TeamColor c) {
        boolean notInCheck = true;
        ChessBoard move = currentBoard.clone();
        ChessBoard moveDouble = currentBoard.clone();

        move.addPiece(m1, p);
        move.addPiece(start, null);
        moveDouble.addPiece(m2, p);
        moveDouble.addPiece(start, null);

        if (isInCheck(c)) {
            notInCheck = false;
        }
        currentBoard = move;
        if (isInCheck(c)) {
            notInCheck = false;
        }
        currentBoard = moveDouble;
        if (isInCheck(c)) {
            notInCheck = false;
        }
        return notInCheck;
    }
    /**
     * Checks for a valid castling move on the left side
     * @return null if no valid castle, or a ChessMove with valid castle
     */
    public ChessMove castleCheckerWhiteLeft(ChessGame.TeamColor color, ChessPiece piece, ChessPosition startPosition) {
        ChessMove whiteValid = null;

        if (piece.getPieceType() == ChessPiece.PieceType.KING && color == ChessGame.TeamColor.WHITE) {
            // for white team
            ChessPosition whiteStart = new ChessPosition(1,5);

            if (startPosition.equals(whiteStart) && !whiteKingMoved) {
                ChessPosition middleSpace1 = new ChessPosition(1, 2);
                ChessPosition middleSpace2 = new ChessPosition(1, 3);
                ChessPosition middleSpace3 = new ChessPosition(1, 4);

                if (leftCastleClear(middleSpace1, middleSpace2, middleSpace3, whiteLeftRookMoved)) {
                    ChessBoard realBoard = currentBoard;

                    if (castlingInCheckChecker(middleSpace3, middleSpace2, startPosition, piece, color)) {
                        whiteValid = new ChessMove(startPosition, middleSpace2, null);
                    }
                    currentBoard = realBoard;
                }
            }
        }
        return whiteValid;
    }

    public ChessMove castleCheckerBlackLeft(ChessGame.TeamColor color, ChessPiece piece, ChessPosition startPosition) {
        ChessMove blackValid = null;

        if (piece.getPieceType() == ChessPiece.PieceType.KING && color == ChessGame.TeamColor.BLACK) {
            // for black team
            ChessPosition whiteStart = new ChessPosition(8,5);

            if (startPosition.equals(whiteStart) && !whiteKingMoved) {
                ChessPosition middleSpace1 = new ChessPosition(8, 2);
                ChessPosition middleSpace2 = new ChessPosition(8, 3);
                ChessPosition middleSpace3 = new ChessPosition(8, 4);

                if (leftCastleClear(middleSpace1, middleSpace2, middleSpace3, blackLeftRookMoved)) {
                    ChessBoard realBoard = currentBoard;

                    if (castlingInCheckChecker(middleSpace3, middleSpace2, startPosition, piece, color)) {
                        blackValid = new ChessMove(startPosition, middleSpace2, null);
                    }
                    currentBoard = realBoard;
                }
            }
        }
        return blackValid;
    }

    /**
     * Checks for a valid castling move on the right side
     * @return null if no valid castle, or a ChessMove with valid castle
     */
    public ChessMove castleCheckerWhiteRight(ChessGame.TeamColor color, ChessPiece piece, ChessPosition startPosition) {
        ChessMove whiteValid = null;

        if (piece.getPieceType() == ChessPiece.PieceType.KING && color == ChessGame.TeamColor.WHITE) {
            // for white team
            ChessPosition whiteStart = new ChessPosition(1,5);

            if (startPosition.equals(whiteStart) && !whiteKingMoved) {
                ChessPosition middleSpace4 = new ChessPosition(1,6);
                ChessPosition middleSpace5 = new ChessPosition(1,7);

                if (rightCastleClear(middleSpace4, middleSpace5, whiteRightRookMoved)) {
                    ChessBoard realBoard = currentBoard;

                    if (castlingInCheckChecker(middleSpace5, middleSpace4, startPosition, piece, color)) {
                        whiteValid = new ChessMove(startPosition, middleSpace5, null);
                    }
                    currentBoard = realBoard;
                }
            }
        }
        return whiteValid;
    }

    public ChessMove castleCheckerBlackRight(ChessGame.TeamColor color, ChessPiece piece, ChessPosition startPosition) {
        ChessMove blackValid = null;

        if (piece.getPieceType() == ChessPiece.PieceType.KING && color == ChessGame.TeamColor.BLACK) {
            // for black team
            ChessPosition whiteStart = new ChessPosition(8,5);

            if (startPosition.equals(whiteStart) && !whiteKingMoved) {
                ChessPosition middleSpace4 = new ChessPosition(8,6);
                ChessPosition middleSpace5 = new ChessPosition(8,7);

                if (rightCastleClear(middleSpace4, middleSpace5, blackRightRookMoved)) {
                    ChessBoard realBoard = currentBoard;

                    if (castlingInCheckChecker(middleSpace5, middleSpace4, startPosition, piece, color)) {
                        blackValid = new ChessMove(startPosition, middleSpace5, null);
                    }
                    currentBoard = realBoard;
                }
            }
        }
        return blackValid;
    }
}
