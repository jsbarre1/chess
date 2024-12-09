package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import static ui.EscapeSequences.*;


public class DrawBoard {
    ChessBoard board;
    private final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);


    public DrawBoard(ChessBoard board) {
        this.board = board;
    }

    public void printBoard(ChessGame.TeamColor teamColor, ChessGame chessGame, Collection<ChessMove> validMoves){
        Collection<ChessPosition> highlightThese = new ArrayList<>();

        if(validMoves != null){
            for (ChessMove validMove : validMoves){
                highlightThese.add(validMove.getEndPosition());
            }
        }

        if(teamColor == ChessGame.TeamColor.BLACK){
            printBlack(highlightThese);
        }else {
            printWhite(highlightThese);
        }
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);


    }

    private void printBlack(Collection<ChessPosition> highlighted){
        out.print(ERASE_SCREEN);
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_BOLD);
        out.print(SET_TEXT_COLOR_YELLOW);

        out.print("    h  g  f  e  d  c  b  a    ");
        out.print(RESET_BG_COLOR);
        out.print("\n");
        for (int row = 1; row <= 8; row++) {
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(SET_TEXT_COLOR_YELLOW);
            out.print(" " + row+ " ");
            for (int col = 1; col <= 8; col++) {
                checkSquareColor(row,col);
                printPiece(row,col, highlighted);
            }
            out.print(SET_TEXT_COLOR_YELLOW);
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(" " + row + " ");
            out.print(RESET_BG_COLOR);
            out.print("\n");
        }
        out.print(SET_TEXT_COLOR_YELLOW);
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print("    h  g  f  e  d  c  b  a    ");
        out.print(RESET_BG_COLOR);
        out.print("\n");
    }

    private void printWhite(Collection<ChessPosition> highlighted){
        out.print(ERASE_SCREEN);
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_BOLD);
        out.print(SET_TEXT_COLOR_YELLOW);
        out.print("    a  b  c  d  e  f  g  h    ");
        out.print(RESET_BG_COLOR);
        out.print("\n");
        for (int row = 8; row >= 1; row--) {
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(SET_TEXT_COLOR_YELLOW);
            out.print(" " + row+ " ");
            for (int col = 8; col >= 1; col--) {
                checkSquareColor(row,col);
                printPiece(row,col, highlighted);
            }
            out.print(SET_TEXT_COLOR_YELLOW);
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(" " + row+ " ");
            out.print(RESET_BG_COLOR);
            out.print("\n");
        }
        out.print(SET_TEXT_COLOR_YELLOW);
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print("    a  b  c  d  e  f  g  h    ");
        out.print(RESET_BG_COLOR);
        out.print("\n");

    }

    private void checkSquareColor(int row, int col){
        if(row  % 2 == 0){
            if(col  % 2 == 0){
                out.print(SET_BG_COLOR_MAGENTA);
            }else{
                out.print(SET_BG_COLOR_DARK_GREEN);
            }
        }else{
            if(col  % 2 == 1){
                out.print(SET_BG_COLOR_MAGENTA);
            }else{
                out.print(SET_BG_COLOR_DARK_GREEN);
            }
        }
    }

    private void printPiece(int row, int col, Collection<ChessPosition> highlighted){
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(position);
        if(highlighted.contains(position)){
            out.print(SET_BG_COLOR_YELLOW);
        }
        if (piece != null){
            switch(piece.getTeamColor()){
                case WHITE -> {
                    out.print(SET_TEXT_COLOR_WHITE);

                    switch (piece.getPieceType()) {
                        case KING -> out.print(WHITE_KING);
                        case ROOK -> out.print(WHITE_ROOK);
                        case BISHOP -> out.print(WHITE_BISHOP);
                        case QUEEN -> out.print(WHITE_QUEEN);
                        case KNIGHT -> out.print(WHITE_KNIGHT);
                        case PAWN -> out.print(WHITE_PAWN);
                    }
                }
                case BLACK -> {
                    out.print(SET_TEXT_COLOR_BLACK);
                    switch (piece.getPieceType()) {
                        case QUEEN -> out.print(BLACK_QUEEN);
                        case KING -> out.print(BLACK_KING);
                        case BISHOP -> out.print(BLACK_BISHOP);
                        case ROOK -> out.print(BLACK_ROOK);
                        case KNIGHT -> out.print(BLACK_KNIGHT);
                        case PAWN -> out.print(BLACK_PAWN);
                    }
                }
            }
        } else {
            out.print(EMPTY);
        }
    }
}
