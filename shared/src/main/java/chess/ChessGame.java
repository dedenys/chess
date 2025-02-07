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
    boolean whiteKingMoved = false;
    boolean whiteLeftRookMoved = false;
    boolean whiteRightRookMoved = false;

    boolean blackKingMoved = false;
    boolean blackLeftRookMoved = false;
    boolean blackRightRookMoved = false;

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
        if (color == TeamColor.WHITE) {
            castleLeft = castleCheckerWhiteLeft(color, piece, startPosition);
            castleRight = castleCheckerWhiteRight(color, piece, startPosition);
        }
        else {
            castleLeft = castleCheckerBlackLeft(color, piece, startPosition);
            castleRight = castleCheckerBlackRight(color, piece, startPosition);
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
        if (color == TeamColor.WHITE) {
            enPassantLeft = enPassantLeftWhiteCheck(piece, startPosition);
            enPassantRight = enPassantRightWhiteCheck(piece, startPosition);
        }
        else {
            enPassantLeft = enPassantLeftBlackCheck(piece, startPosition);
            enPassantRight = enPassantRightBlackCheck(piece, startPosition);
        }

        if (enPassantLeft != null) {
            valid.add(enPassantLeft);
        }
        if (enPassantRight != null) {
            valid.add(enPassantRight);
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
        Collection<ChessMove> validMoves = validMoves(start);//pieceAtMove.pieceMoves(currentBoard, start);//validMoves(start);
        boolean matchFound = false;

        for (ChessMove validMove : validMoves) {
            if (validMove.equals(move)) {
                matchFound = true;
                break;
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
        // en passant check
        else if (pieceAtMove.getPieceType() == ChessPiece.PieceType.PAWN) {
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
        // castling check
        else if (pieceAtMove.getPieceType() == ChessPiece.PieceType.KING) {
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

                if (!whiteKingMoved) {
                    whiteKingMoved = true;
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

                if (!blackKingMoved) {
                    blackKingMoved = true;
                }
            }

        }
        else if (pieceAtMove.getPieceType() == ChessPiece.PieceType.ROOK) {
            if (pieceAtMove.getTeamColor() == TeamColor.WHITE) {
                ChessPosition rookLeftPos = new ChessPosition(1,1);
                ChessPosition rookRightPos = new ChessPosition(1,8);

                if (start.equals(rookRightPos) && !whiteRightRookMoved) {
                    whiteRightRookMoved = true;
                }
                if (start.equals(rookLeftPos) && !whiteLeftRookMoved) {
                    whiteLeftRookMoved = true;
                }
            }
            else {
                ChessPosition rookLeftPos = new ChessPosition(8,1);
                ChessPosition rookRightPos = new ChessPosition(8,8);

                if (start.equals(rookRightPos) && !blackRightRookMoved) {
                    blackRightRookMoved = true;
                }
                if (start.equals(rookLeftPos) && !blackLeftRookMoved) {
                    blackLeftRookMoved = true;
                }
            }
            currentBoard.addPiece(end, pieceAtMove);
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

        // en passant variables
        lastMove = move;

        if (pieceAtMove.getPieceType() == ChessPiece.PieceType.PAWN) {
            lastMoveWasDouble = pawnDoubleMoveCheck(move);
        }
        else {
            lastMoveWasDouble = false;
        }


    }

    /**
     * returns the difference in row positions of two moves
     * @param move1 starting move
     * @param move2 ending move
     * @return absolute value of difference in row
     */
    private int calcRowDifference(ChessPosition move1, ChessPosition move2) {
        int startRow = move1.getRow();
        int endRow = move2.getRow();

        return abs(endRow - startRow);
    }

    /**
     * checks for a double move for a pawn
     * @param m move to check for double
     * @return true if a move is a double for a pawn
     */
    private boolean pawnDoubleMoveCheck(ChessMove m) {
        ChessPosition start = m.getStartPosition();
        ChessPosition end = m.getEndPosition();

        int dif = calcRowDifference(start, end);

        return dif > 1;
    }

    /**
     * checks conditions for a valid en passant move
     * @return returns true if all conditions for en passant are met
     */
    private boolean enPassantConditionsMet (ChessPiece piece, int row, int col, boolean isWhiteTeam, boolean isLeft) {
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

    private ChessMove enPassantLeftWhiteCheck(ChessPiece piece, ChessPosition startPosition) {
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        if (!enPassantConditionsMet(piece, row, col, true, true)) {
            return  null;
        }
        return getEnPassantMove(row, col, startPosition, true, true);
    }

    private ChessMove enPassantRightWhiteCheck(ChessPiece piece, ChessPosition startPosition) {
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        if (!enPassantConditionsMet(piece, row, col, true, false)) {
            return  null;
        }
        return getEnPassantMove(row, col, startPosition, true, false);
    }

    private ChessMove enPassantLeftBlackCheck(ChessPiece piece, ChessPosition startPosition) {
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        if (!enPassantConditionsMet(piece, row, col, false, true)) {
            return  null;
        }
        return getEnPassantMove(row, col, startPosition, false, true);
    }

    private ChessMove enPassantRightBlackCheck(ChessPiece piece, ChessPosition startPosition) {
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        if (!enPassantConditionsMet(piece, row, col, false, false)) {
            return  null;
        }
        return getEnPassantMove(row, col, startPosition, false, false);
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
     * Checks for a valid castling move on the left side
     * @return null if no valid castle, or a ChessMove with valid castle
     */
    private ChessMove castleCheckerWhiteLeft(TeamColor color, ChessPiece piece, ChessPosition startPosition) {
        ChessMove whiteValid = null;

        if (piece.getPieceType() == ChessPiece.PieceType.KING && color == TeamColor.WHITE) {
            // for white team
            ChessPosition whiteStart = new ChessPosition(1,5);

            if (startPosition.equals(whiteStart) && !whiteKingMoved) {
                ChessPosition middleSpace1 = new ChessPosition(1, 2);
                ChessPosition middleSpace2 = new ChessPosition(1, 3);
                ChessPosition middleSpace3 = new ChessPosition(1, 4);

                if (!whiteLeftRookMoved && currentBoard.getPiece(middleSpace1) == null && currentBoard.getPiece(middleSpace2) == null && currentBoard.getPiece(middleSpace3) == null) {
                    ChessBoard realBoard = currentBoard;
                    ChessBoard left = currentBoard.clone();
                    ChessBoard leftLeft = currentBoard.clone();

                    left.addPiece(middleSpace3, piece);
                    left.addPiece(startPosition, null);

                    leftLeft.addPiece(middleSpace2, piece);
                    leftLeft.addPiece(startPosition, null);

                    boolean notInCheck = true;

                    if (isInCheck(color)) {
                        notInCheck = false;
                    }
                    currentBoard = left;
                    if (isInCheck(color)) {
                        notInCheck = false;
                    }
                    currentBoard = leftLeft;
                    if (isInCheck(color)) {
                        notInCheck = false;
                    }
                    currentBoard = realBoard;

                    // all conditions are met
                    if (notInCheck) {
                        whiteValid = new ChessMove(startPosition, middleSpace2, null);
                    }
                }
            }
        }

        return whiteValid;
    }

    private ChessMove castleCheckerBlackLeft(TeamColor color, ChessPiece piece, ChessPosition startPosition) {
        ChessMove blackValid = null;

        if (piece.getPieceType() == ChessPiece.PieceType.KING && color == TeamColor.BLACK) {
            // for black team
            ChessPosition whiteStart = new ChessPosition(8,5);

            if (startPosition.equals(whiteStart) && !whiteKingMoved) {
                ChessPosition middleSpace1 = new ChessPosition(8, 2);
                ChessPosition middleSpace2 = new ChessPosition(8, 3);
                ChessPosition middleSpace3 = new ChessPosition(8, 4);

                if (!blackLeftRookMoved && currentBoard.getPiece(middleSpace1) == null && currentBoard.getPiece(middleSpace2) == null && currentBoard.getPiece(middleSpace3) == null) {
                    ChessBoard realBoard = currentBoard;
                    ChessBoard left = currentBoard.clone();
                    ChessBoard leftLeft = currentBoard.clone();

                    left.addPiece(middleSpace3, piece);
                    left.addPiece(startPosition, null);

                    leftLeft.addPiece(middleSpace2, piece);
                    leftLeft.addPiece(startPosition, null);

                    boolean notInCheck = true;

                    if (isInCheck(color)) {
                        notInCheck = false;
                    }
                    currentBoard = left;
                    if (isInCheck(color)) {
                        notInCheck = false;
                    }
                    currentBoard = leftLeft;
                    if (isInCheck(color)) {
                        notInCheck = false;
                    }
                    currentBoard = realBoard;

                    // all conditions are met
                    if (notInCheck) {
                        blackValid = new ChessMove(startPosition, middleSpace2, null);
                    }
                }
            }
        }

        return blackValid;
    }

    /**
     * Checks for a valid castling move on the right side
     * @return null if no valid castle, or a ChessMove with valid castle
     */
    private ChessMove castleCheckerWhiteRight(TeamColor color, ChessPiece piece, ChessPosition startPosition) {
        ChessMove whiteValid = null;

        if (piece.getPieceType() == ChessPiece.PieceType.KING && color == TeamColor.WHITE) {
            // for white team
            ChessPosition whiteStart = new ChessPosition(1,5);

            if (startPosition.equals(whiteStart) && !whiteKingMoved) {
                ChessPosition middleSpace4 = new ChessPosition(1,6);
                ChessPosition middleSpace5 = new ChessPosition(1,7);

                if (!whiteRightRookMoved && currentBoard.getPiece(middleSpace4) == null && currentBoard.getPiece(middleSpace5) == null) {
                    ChessBoard realBoard = currentBoard;
                    ChessBoard right = currentBoard.clone();
                    ChessBoard rightRight = currentBoard.clone();

                    right.addPiece(middleSpace4, piece);
                    rightRight.addPiece(startPosition, null);

                    rightRight.addPiece(middleSpace5, piece);
                    rightRight.addPiece(startPosition, null);

                    boolean notInCheck = true;

                    if (isInCheck(color)) {
                        notInCheck = false;
                    }
                    currentBoard = right;
                    if (isInCheck(color)) {
                        notInCheck = false;
                    }
                    currentBoard = rightRight;
                    if (isInCheck(color)) {
                        notInCheck = false;
                    }
                    currentBoard = realBoard;

                    // all conditions are met
                    if (notInCheck) {
                        whiteValid = new ChessMove(startPosition, middleSpace5, null);
                    }
                }

            }
        }
        return whiteValid;
    }

    private ChessMove castleCheckerBlackRight(TeamColor color, ChessPiece piece, ChessPosition startPosition) {
        ChessMove blackValid = null;

        if (piece.getPieceType() == ChessPiece.PieceType.KING && color == TeamColor.BLACK) {
            // for black team
            ChessPosition whiteStart = new ChessPosition(8,5);

            if (startPosition.equals(whiteStart) && !whiteKingMoved) {
                ChessPosition middleSpace4 = new ChessPosition(8,6);
                ChessPosition middleSpace5 = new ChessPosition(8,7);

                if (!blackRightRookMoved && currentBoard.getPiece(middleSpace4) == null && currentBoard.getPiece(middleSpace5) == null) {
                    ChessBoard realBoard = currentBoard;
                    ChessBoard right = currentBoard.clone();
                    ChessBoard rightRight = currentBoard.clone();

                    right.addPiece(middleSpace4, piece);
                    rightRight.addPiece(startPosition, null);

                    rightRight.addPiece(middleSpace5, piece);
                    rightRight.addPiece(startPosition, null);

                    boolean notInCheck = true;

                    if (isInCheck(color)) {
                        notInCheck = false;
                    }
                    currentBoard = right;
                    if (isInCheck(color)) {
                        notInCheck = false;
                    }
                    currentBoard = rightRight;
                    if (isInCheck(color)) {
                        notInCheck = false;
                    }
                    currentBoard = realBoard;

                    // all conditions are met
                    if (notInCheck) {
                        blackValid = new ChessMove(startPosition, middleSpace5, null);
                    }
                }

            }
        }
        return blackValid;
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
        whiteKingMoved = false;
        whiteLeftRookMoved = false;
        whiteRightRookMoved = false;

        blackKingMoved = false;
        blackLeftRookMoved = false;
        blackRightRookMoved = false;
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
