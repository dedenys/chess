package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import model.GameData;
import server.ServerFacade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import static ui.EscapeSequences.*;

public class GameClient {
    private final String serverUrl;
    private final ServerFacade server;
    public GameData gameData;
    public ChessGame game;
    public String color = "WHITE";
    public static String[] numbers = {"8","7","6","5","4","3","2","1"};
    public static String[] letters = {"a","b","c","d","e","f","g","h"};
    public static ChessGame testGame;
    public static ChessBoard testBoard;

    // UI

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    // Padded characters.
    private static final String EMPTY = "   ";
    private static final String X = " X ";
    private static final String O = " O ";

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
                    - do something
                    """;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                case "do" -> test();
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String test() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeaders(out);

        drawBoard(out);

        setGray(out);
        return "";
    }

    private static void drawHeaders(PrintStream out) {

        setGray(out);

        String[] headers = { "1", "2", "3","4","5","6","7","8" };
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, letters[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
            }
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
       // int prefixLength = 2;//SQUARE_SIZE_IN_PADDED_CHARS / 2;
       // int suffixLength = 1;//SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

        //out.print(EMPTY.repeat(prefixLength));
        out.print("      ");
        printHeaderText(out, headerText);
        out.print("  ");
        //out.print(EMPTY.repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);

        setGray(out);
    }

    private static void drawBoard(PrintStream out) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

//            out.print(SET_BG_COLOR_BLACK);
//            out.print(SET_TEXT_COLOR_GREEN);
//
//            out.print(numbers[boardRow]);
            drawRowOfSquares(out, boardRow);

            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(SET_BG_COLOR_DARK_GREY);

                out.print(SET_TEXT_COLOR_GREEN);
                out.print("  ");
                // Draw horizontal row separator.
                drawHorizontalLine(out);
                setGray(out);
            }
        }
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow) {
        boolean on = false;
        boolean firstRow = true;

        int row = boardRow;
        int column;

        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            //on = !on;
            boolean squareColorWhite = true;

            out.print(SET_BG_COLOR_DARK_GREY);
            out.print(SET_TEXT_COLOR_GREEN);
            if (squareRow == 1) {
                out.print(numbers[boardRow]+" ");
                firstRow = false;
            }
            else {
                out.print("  ");
            }
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                column = boardCol;

                ChessPosition p = new ChessPosition(row, column);

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
                    printPiece(out, rand.nextBoolean() ? X : O, squareColorWhite);
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
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }
    private static void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }


    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPiece(PrintStream out, String player, boolean isWhite) {
//        out.print(SET_BG_COLOR_WHITE);
        if (isWhite) {
            out.print(SET_TEXT_COLOR_BLACK);
        }
        else {
            out.print(SET_TEXT_COLOR_WHITE);
        }

        out.print(player);

        //setWhite(out);
    }
}
