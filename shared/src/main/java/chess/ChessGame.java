package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor currentTeamColor = TeamColor.WHITE;
    ChessBoard currentBoard;

    public ChessGame() {

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

            if (isInCheck(color) != true) {
                valid.add(move);
            }

            currentBoard = realBoard;

        }


        return valid;
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

        if (pieceAtMove == null) {
            throw new InvalidMoveException();
        }
        else if (pieceAtMove.getTeamColor() != currentTeamColor) {
            throw new InvalidMoveException();
        }

        // check for validity of move
        Collection<ChessMove> validMoves = pieceAtMove.pieceMoves(currentBoard, start);//validMoves(start);
        boolean matchFound = false;

        for (ChessMove validMove : validMoves) {
            if (validMove.equals(move)) {
                matchFound = true;
            }
        }

        if (!matchFound) {
            throw new InvalidMoveException();
        }

        // execute movement
        currentBoard.addPiece(start, null);

        if ((pieceAtMove.getPieceType() == ChessPiece.PieceType.PAWN) && move.getPromotionPiece() != null) {
            ChessPiece newPiece = new ChessPiece(pieceAtMove.getTeamColor(), move.getPromotionPiece());
            currentBoard.addPiece(end, newPiece);
        }
        else {
            currentBoard.addPiece(end, pieceAtMove);
        }

        // change turn
        if (currentTeamColor == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        }
        else {
            setTeamTurn(TeamColor.WHITE);
        }


    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition king = new ChessPosition(0,0);

        // find the king for the teamColor
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                ChessPosition position = new ChessPosition(i+1, j+1);
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

                ChessPosition position = new ChessPosition(i+1, j+1);
                ChessPiece piece = currentBoard.getPiece(position);

                if (piece != null && piece.getTeamColor() != teamColor) {

                    Collection<ChessMove> moves = piece.pieceMoves(currentBoard, position);

                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(king)) {
                            return true;
                        }
                    }

                }
            }
        }


        // no check was found
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }
}
