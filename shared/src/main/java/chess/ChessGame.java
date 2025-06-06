package chess;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Math.abs;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor currentTeamColor = TeamColor.WHITE;
    ChessBoard currentBoard = new ChessBoard();
    public boolean isOver = false;
    boolean wKingMoved = false;
    boolean wLRookMoved = false;
    boolean wRRookMoved = false;

    boolean bKingMoved = false;
    boolean bLRookMoved = false;
    boolean bRRookMoved = false;

    // en passant
    ChessMove lastMove;
    boolean lastMoveWasDouble = false;

    public ChessGame() {
        currentBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (currentBoard.getPiece(startPosition) == null) {
            return null;
        }

        ChessPiece piece = currentBoard.getPiece(startPosition);
        TeamColor color = piece.getTeamColor();
        Collection<ChessMove> currentMoves = piece.pieceMoves(currentBoard, startPosition);
        Collection<ChessMove> valid = new ArrayList<>();

        for (ChessMove move : currentMoves) {
            ChessBoard newBoard = currentBoard.clone();
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();

            ChessBoard realBoard = currentBoard;

            currentBoard = newBoard;

            currentBoard.addPiece(start, null);
            currentBoard.addPiece(end, piece);

            if (!isInCheck(color)) {
                valid.add(move);
            }

            currentBoard = realBoard;

        }

        // castling
        ChessMove castleLeft;
        ChessMove castleRight;

        CastlingPackage castling = new CastlingPackage(currentBoard, wKingMoved, wLRookMoved, wRRookMoved, bKingMoved, bLRookMoved, bRRookMoved);
        if (color == TeamColor.WHITE) {
            castleLeft = castling.castleCheckerWhiteLeft(color, piece, startPosition);
            castleRight = castling.castleCheckerWhiteRight(color, piece, startPosition);
        }
        else {
            castleLeft = castling.castleCheckerBlackLeft(color, piece, startPosition);
            castleRight = castling.castleCheckerBlackRight(color, piece, startPosition);
        }

        if (castleLeft != null) {
            valid.add(castleLeft);
        }
        if (castleRight != null) {
            valid.add(castleRight);
        }

        // en passant
        ChessMove enPassantLeft;
        ChessMove enPassantRight;

        EnPassantPackage enPassant = new EnPassantPackage(currentBoard, lastMove, lastMoveWasDouble);
        if (color == TeamColor.WHITE) {
            enPassantLeft = enPassant.enPassantLeftWhiteCheck(piece, startPosition);
            enPassantRight = enPassant.enPassantRightWhiteCheck(piece, startPosition);
        }
        else {
            enPassantLeft = enPassant.enPassantLeftBlackCheck(piece, startPosition);
            enPassantRight = enPassant.enPassantRightBlackCheck(piece, startPosition);
        }

        if (enPassantLeft != null) {
            valid.add(enPassantLeft);
        }
        if (enPassantRight != null) {
            valid.add(enPassantRight);
        }


        return valid;
    }

    private void makeMoveValidityCheck(ChessPiece pieceAtMove, ChessMove m, ChessPosition s) throws InvalidMoveException{
        if (pieceAtMove == null) {
            throw new InvalidMoveException();
        }
        else if (pieceAtMove.getTeamColor() != currentTeamColor) {
            throw new InvalidMoveException();
        }

        // check for validity of move
        Collection<ChessMove> validMoves = validMoves(s);//pieceAtMove.pieceMoves(currentBoard, start);//validMoves(start);
        boolean matchFound = false;

        for (ChessMove validMove : validMoves) {
            if (validMove.equals(m)) {
                matchFound = true;
                break;
            }
        }

        if (!matchFound) {
            throw new InvalidMoveException();
        }
    }

    private void pawnEnPassant(ChessMove move, ChessPiece pieceAtMove, ChessPosition start, ChessPosition end) {
        if (pieceAtMove.getTeamColor() == TeamColor.WHITE) {
            if (start.getRow() == 5 && end.getColumn() != start.getColumn() && currentBoard.getPiece(end) == null) {
                currentBoard.addPiece(end, pieceAtMove);
                ChessPosition pieceToCapture = new ChessPosition(start.getRow(),end.getColumn());
                currentBoard.addPiece(pieceToCapture, null);
            }
            else {
                currentBoard.addPiece(end, pieceAtMove);
            }
        }
        else {
            if (start.getRow() == 4 && end.getColumn() != start.getColumn() && currentBoard.getPiece(end) == null) {
                currentBoard.addPiece(end, pieceAtMove);
                ChessPosition pieceToCapture = new ChessPosition(start.getRow(),end.getColumn());
                currentBoard.addPiece(pieceToCapture, null);
            }
            else {
                currentBoard.addPiece(end, pieceAtMove);
            }
        }
    }

    private void kingCastling(ChessMove move, ChessPiece pieceAtMove, ChessPosition start, ChessPosition end) {
        if (pieceAtMove.getTeamColor() == TeamColor.WHITE) {
            ChessPosition s = new ChessPosition(1,5);
            ChessPosition left = new ChessPosition(1,3);
            ChessPosition right = new ChessPosition(1,7);

            if (end.equals(left) && start.equals(s)) {
                currentBoard.addPiece(end, pieceAtMove);
                ChessPosition rookNewPos = new ChessPosition(1,4);
                ChessPosition rookOldPos = new ChessPosition(1,1);
                ChessPiece rook = new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK);

                currentBoard.addPiece(rookOldPos, null);
                currentBoard.addPiece(rookNewPos, rook);

            }
            else if (end.equals(right) && start.equals(s)) {
                currentBoard.addPiece(end, pieceAtMove);
                ChessPosition rookNewPos = new ChessPosition(1,6);
                ChessPosition rookOldPos = new ChessPosition(1,8);
                ChessPiece rook = new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK);

                currentBoard.addPiece(rookOldPos, null);
                currentBoard.addPiece(rookNewPos, rook);
            }
            else {
                currentBoard.addPiece(end, pieceAtMove);
            }

            if (!wKingMoved) {
                wKingMoved = true;
            }
        }
        else {
            ChessPosition s = new ChessPosition(8,5);
            ChessPosition left = new ChessPosition(8,3);
            ChessPosition right = new ChessPosition(8,7);

            if (end.equals(left) && start.equals(s)) {
                currentBoard.addPiece(end, pieceAtMove);
                ChessPosition rookNewPos = new ChessPosition(8,4);
                ChessPosition rookOldPos = new ChessPosition(8,1);
                ChessPiece rook = new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK);

                currentBoard.addPiece(rookOldPos, null);
                currentBoard.addPiece(rookNewPos, rook);

            }
            else if (end.equals(right) && start.equals(s)) {
                currentBoard.addPiece(end, pieceAtMove);
                ChessPosition rookNewPos = new ChessPosition(8,6);
                ChessPosition rookOldPos = new ChessPosition(8,8);
                ChessPiece rook = new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK);

                currentBoard.addPiece(rookOldPos, null);
                currentBoard.addPiece(rookNewPos, rook);
            }
            else {
                currentBoard.addPiece(end, pieceAtMove);
            }

            if (!bKingMoved) {
                bKingMoved = true;
            }
        }
    }

    private void rookCastling(ChessMove move, ChessPiece pieceAtMove, ChessPosition start, ChessPosition end) {
        if (pieceAtMove.getTeamColor() == TeamColor.WHITE) {
            ChessPosition rookLeftPos = new ChessPosition(1,1);
            ChessPosition rookRightPos = new ChessPosition(1,8);

            if (start.equals(rookRightPos) && !wRRookMoved) {
                wRRookMoved = true;
            }
            if (start.equals(rookLeftPos) && !wLRookMoved) {
                wLRookMoved = true;
            }
        }
        else {
            ChessPosition rookLeftPos = new ChessPosition(8,1);
            ChessPosition rookRightPos = new ChessPosition(8,8);

            if (start.equals(rookRightPos) && !bRRookMoved) {
                bRRookMoved = true;
            }
            if (start.equals(rookLeftPos) && !bLRookMoved) {
                bLRookMoved = true;
            }
        }
        currentBoard.addPiece(end, pieceAtMove);
    }

    private void changeTurn() {
        if (currentTeamColor == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        }
        else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    private void setEnPassantVars(ChessMove move, ChessPiece pieceAtMove) {
        lastMove = move;

        if (pieceAtMove.getPieceType() == ChessPiece.PieceType.PAWN) {
            lastMoveWasDouble = EnPassantPackage.pawnDoubleMoveCheck(move);
        }
        else {
            lastMoveWasDouble = false;
        }
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece pieceAtMove = currentBoard.getPiece(start);

        makeMoveValidityCheck(pieceAtMove, move, start);

        currentBoard.addPiece(start, null);

        // pawn promotion check
        if ((pieceAtMove.getPieceType() == ChessPiece.PieceType.PAWN) && move.getPromotionPiece() != null) {
            ChessPiece newPiece = new ChessPiece(pieceAtMove.getTeamColor(), move.getPromotionPiece());
            currentBoard.addPiece(end, newPiece);
        }
        // en passant check
        else if (pieceAtMove.getPieceType() == ChessPiece.PieceType.PAWN) {
            pawnEnPassant(move, pieceAtMove, start, end);
        }
        // castling check
        else if (pieceAtMove.getPieceType() == ChessPiece.PieceType.KING) {
            kingCastling(move, pieceAtMove, start, end);
        }
        else if (pieceAtMove.getPieceType() == ChessPiece.PieceType.ROOK) {
            rookCastling(move, pieceAtMove, start, end);
        }
        // normal case
        else {
            currentBoard.addPiece(end, pieceAtMove);
        }

        // change turn
        changeTurn();

        // en passant variables
        setEnPassantVars(move, pieceAtMove);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition king = findKing(teamColor);

        // iterate through opposing team pieces for a possible check
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (findAttackOnKing(i, j, king, teamColor)) {
                    return true;
                }
            }
        }
        // no check was found
        return false;
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                ChessPosition position = new ChessPosition(i+1, j+1);
                ChessPiece piece = currentBoard.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        return position;
                    }
                }
            }
        }
        return null;
    }

    private boolean findAttackOnKing(int i, int j, ChessPosition kingPos, TeamColor teamColor) {
        ChessPosition position = new ChessPosition(i+1, j+1);
        ChessPiece piece = currentBoard.getPiece(position);

        if (piece != null && piece.getTeamColor() != teamColor) {

            Collection<ChessMove> moves = piece.pieceMoves(currentBoard, position);

            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(kingPos)) {
                    return true;
                }
            }

        }
        return false;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return teamHasNoValidMoves(teamColor) && isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // king is not in immediate danger && there are no valid moves
        return !isInCheck(teamColor) && teamHasNoValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = board;
        wKingMoved = false;
        wLRookMoved = false;
        wRRookMoved = false;

        bKingMoved = false;
        bLRookMoved = false;
        bRRookMoved = false;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }

    /**
     * returns true if a team has no valid moves
     */
    private boolean teamHasNoValidMoves(TeamColor teamColor) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                ChessPosition pos = new ChessPosition(i+1,j+1);
                ChessPiece piece = currentBoard.getPiece(pos);

                if (piece != null && piece.getPieceType() != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);

                    if (!moves.isEmpty()) {
                        // if isEmpty is false, then there is a valid move
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
