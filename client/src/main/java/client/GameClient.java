package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import server.ServerFacade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import static ui.EscapeSequences.*;

public class GameClient {
    public static boolean isLeaving = false;
    private final String serverUrl;
    private final ServerFacade server;
    public GameData gameData;
    public ChessGame game;
    public static String color = "BLACK";
    public static String[] numbersWhite = {"8","7","6","5","4","3","2","1"};
    public static String[] lettersWhite = {"a","b","c","d","e","f","g","h"};
    public static String[] numbersBlack = {"1","2","3","4","5","6","7","8"};
    public static String[] lettersBlack = {"h","g","f","e","d","c","b","a"};
    public static ChessGame testGame;
    public static ChessBoard testBoard;


    // UI

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 0;

    // Padded characters.
    private static final String EMPTY = "   ";

    private static final String P = " P ";
    private static final String R = " R ";
    private static final String N = " N ";
    private static final String B = " B ";
    private static final String Q = " Q ";
    private static final String K = " K ";

    private static Random rand = new Random();



    public GameClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        testGame = new ChessGame();
        testBoard = testGame.getBoard();
    }

    public String help() {
        return """
                    - help
                    - view board
                    - leave game
                    """;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                case "leave" -> leave();
                case "view" -> draw();
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String leave() {
        isLeaving = true;

        return  String.format("You left the game");
    }

    public String draw() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        drawHeaders(out);
        drawBoard(out);
        drawHeaders(out);

        out.print("\u001B[49m");
        return "";
    }

    private static void drawHeaders(PrintStream out) {
        out.print(SET_BG_COLOR_BORDER);
        String[] letters;

        if (Objects.equals(color, "WHITE")) {
            letters = lettersWhite;
        }
        else {
            letters = lettersBlack;
        }

        out.print("   ");

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, letters[boardCol]);
        }

        out.print("   ");
        out.print("\u001B[49m");

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        printHeaderText(out, headerText);
        out.print("  ");
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_TEXT_COLOR_BLUE);
        out.print(player);
    }

    private static void drawBoard(PrintStream out) {
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            drawRowOfSquares(out, boardRow);

            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                out.print("\u001B[49m");

                out.print(SET_TEXT_COLOR_BLUE);
                // Draw horizontal row separator.
                drawHorizontalLine(out);
                setGray(out);
            }
        }
    }

    private static String getType(ChessPiece.PieceType type) {
        String pieceToPrint = EMPTY;
        switch (type) {
            case KING -> pieceToPrint= K;
            case QUEEN -> pieceToPrint = Q;
            case BISHOP -> pieceToPrint = B;
            case KNIGHT -> pieceToPrint = N;
            case ROOK -> pieceToPrint = R;
            case PAWN -> pieceToPrint = P;
        }
        return pieceToPrint;
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow) {
        int row = boardRow;
        int column;

        String[] numbers;

        if (Objects.equals(color, "WHITE")) {
            numbers = numbersWhite;
        }
        else {
            numbers = numbersBlack;
        }

        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            boolean squareColorWhite = true;

            out.print(SET_BG_COLOR_BORDER);
            out.print(SET_TEXT_COLOR_BLUE);

            out.print(" "+numbers[boardRow]+" ");

            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                String pieceToPrint = EMPTY;
                column = boardCol;
                ChessGame.TeamColor pieceColor = null;

                ChessPosition pos;

                if (Objects.equals(color, "WHITE")) {
                    pos = new ChessPosition(row+1, column+1);
                }
                else {
                    pos = new ChessPosition(8-row, 8-column);
                }
                ChessPiece piece = testBoard.getPiece(pos);

                if (piece != null) {
                    ChessPiece.PieceType type = piece.getPieceType();
                    pieceColor = piece.getTeamColor();
                    pieceToPrint = getType(type);
                }

                if (boardRow % 2 == 0 ) {
                    if (boardCol % 2 == 0) {
                        setWhite(out);
                        squareColorWhite = true;
                    }
                    else {
                        setBlack(out);
                        squareColorWhite = false;
                    }
                }
                else {
                    if (boardCol % 2 == 0) {
                        setBlack(out);
                        squareColorWhite = false;
                    }
                    else {
                        setWhite(out);
                        squareColorWhite = true;
                    }
                }

                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    //setBlack(out);
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));
                    printPiece(out, pieceToPrint, pieceColor);
                    out.print(EMPTY.repeat(suffixLength));
                }
                else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                }

                if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                    // Draw vertical column separator.
                    setGray(out);
                    out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
                }

                setGray(out);
            }

            out.print(SET_BG_COLOR_BORDER);
            out.print(SET_TEXT_COLOR_BLUE);
            out.print(" "+numbers[boardRow]+" ");
            out.print("\u001B[49m");
            out.println();
        }
    }

    private static void drawHorizontalLine(PrintStream out) {

        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS +
                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_PADDED_CHARS;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
            setGray(out);
            out.print(EMPTY.repeat(boardSizeInSpaces));

            setGray(out);
            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_SQUARE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }


    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_SQUARE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPiece(PrintStream out, String player, ChessGame.TeamColor team) {

        if (team == ChessGame.TeamColor.WHITE) {
            out.print(SET_TEXT_COLOR_WHITE);
        }
        else {
            out.print(SET_TEXT_COLOR_BLACK);
        }

        out.print(SET_TEXT_BOLD);
        out.print(player);
    }
}
