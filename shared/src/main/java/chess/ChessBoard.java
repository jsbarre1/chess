package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    final int SIZE = 8;
    private ChessPiece[][] boardState = new ChessPiece[SIZE][SIZE];
    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        //check if the position is within bounds

        boardState[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return boardState[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int r = 1; r <= SIZE; r++) {
            for (int c = 1; c <= SIZE; c++) {
//                |r|n|b|q|k|b|n|r|
//                |p|p|p|p|p|p|p|p|
//                | | | | | | | | |
//                | | | | | | | | |
//                | | | | | | | | |
//                | | | | | | | | |
//                |P|P|P|P|P|P|P|P|
//                |R|N|B|Q|K|B|N|R|
                //white side (bottom)
                if(r==1 && c ==1){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                if (r==1 && c ==2){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                if (r==1 && c ==3){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                if (r==1 && c ==4){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                if (r==1 && c ==5){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                if (r==1 && c ==6){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                if (r==1 && c ==7){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                if (r==1 && c ==8){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }

//                |r|n|b|q|k|b|n|r|
//                |p|p|p|p|p|p|p|p|
//                | | | | | | | | |
//                | | | | | | | | |
//                | | | | | | | | |
//                | | | | | | | | |
//                |P|P|P|P|P|P|P|P|
//                |R|N|B|Q|K|B|N|R|
                //black side (top)
                if(r==8 && c ==1){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                if (r==8 && c ==2){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                if (r==8 && c ==3){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                if (r==8 && c ==4){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                if (r==8 && c ==5){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                if (r==8 && c ==6){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                if (r==8 && c ==7){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                if (r==8 && c ==8){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                //pawns
                if(r == 2){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }
                if(r == 7){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                    System.out.print("adding piece at: " + "(" + r + ", " + c +")\n");
                }

            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return SIZE == that.SIZE && Objects.deepEquals(boardState, that.boardState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(SIZE, Arrays.deepHashCode(boardState));
    }
}
