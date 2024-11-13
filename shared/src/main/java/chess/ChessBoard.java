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
    final static int SIZE = 8;
    private ChessPiece[][] boardState = new ChessPiece[SIZE][SIZE];
    public ChessBoard() {

    }

    public ChessBoard(ChessPiece[][] board){
        boardState = board;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        boardState[position.getRow()-1][position.getColumn()-1] = piece;
    }

    public void removePiece(ChessPosition position){
        boardState[position.getRow()-1][position.getColumn()-1] = null;
    }

    public void movePiece(ChessMove move){
        if(move.getPromotionPiece() != null){
            boardState[move.getEndPosition().getRow()-1][move.getEndPosition().getColumn()-1] =
                    new ChessPiece(getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece());
        }else{
            boardState[move.getEndPosition().getRow()-1][move.getEndPosition().getColumn()-1] =
                    boardState[move.getStartPosition().getRow()-1][move.getStartPosition().getColumn()-1];
        }
        boardState[move.getStartPosition().getRow()-1]
                [move.getStartPosition().getColumn()-1] = null;
    }

    public void castle(ChessMove move){
        //move king over to right spot
        getPiece(move.getStartPosition());
        boardState[move.getEndPosition().getRow()-1]
                [move.getEndPosition().getColumn()-1] =
                boardState[move.getStartPosition().getRow()-1]
                        [move.getStartPosition().getColumn()-1];

        boardState[move.getStartPosition().getRow()-1]
                [move.getStartPosition().getColumn()-1] = null;
        //move rook depending on left or right

        if(move.getEndPosition().getColumn() == 7){
            boardState[move.getEndPosition().getRow()-1][6-1] =
                    boardState[move.getEndPosition().getRow()-1][8-1];
            boardState[move.getEndPosition().getRow()-1][8-1] = null;
        }
        if(move.getEndPosition().getColumn() == 3){
            boardState[move.getEndPosition().getRow()-1][4-1] =
                    boardState[move.getEndPosition().getRow()-1][1-1];
            boardState[move.getEndPosition().getRow()-1][1-1] = null;
        }
    }

    public void enPassant(ChessMove move, ChessMove opposingMove){
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        boardState[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1]
                = boardState[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1];
        boardState[move.getStartPosition().getRow() - 1]
                [move.getStartPosition().getColumn() - 1] = null;
        boardState[opposingMove.getEndPosition().getRow() - 1]
                [opposingMove.getEndPosition().getColumn() - 1] = null;
    }
    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        if(position.getRow()<1|| position.getRow()>8){
            return null;
        }
        if(position.getColumn() <1 || position.getColumn()>8){
            return null;
        }
        return boardState[position.getRow()-1][position.getColumn()-1];
    }
    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int r = 1; r <= SIZE; r++) {
            for (int c = 1; c <= SIZE; c++) {

                if(r==1 && c ==1){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                }
                if (r==1 && c ==2){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                }
                if (r==1 && c ==3){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                }
                if (r==1 && c ==4){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                }
                if (r==1 && c ==5){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                }
                if (r==1 && c ==6){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                }
                if (r==1 && c ==7){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                }
                if (r==1 && c ==8){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                }
                if(r==8 && c ==1){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                }
                if (r==8 && c ==2) {
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                    ChessPosition newPosition = new ChessPosition(r, c);
                    addPiece(newPosition, newPiece);
                }
                if (r==8 && c ==3){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                }
                if (r==8 && c ==4) {
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
                    ChessPosition newPosition = new ChessPosition(r, c);
                    addPiece(newPosition, newPiece);
                }
                if (r==8 && c ==5){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                }
                if (r==8 && c ==6){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                }
                if (r==8 && c ==7){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                }
                if (r==8 && c ==8){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                }
                if(r == 2){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
                }
                if(r == 7){
                    ChessPiece newPiece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
                    ChessPosition newPosition = new ChessPosition(r,c);
                    addPiece(newPosition, newPiece);
               }

            }
        }
    }

    public ChessPiece[][] getBoard() {
        return boardState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()){ return false;}
        ChessBoard that = (ChessBoard) o;
        return SIZE == that.SIZE && Objects.deepEquals(boardState, that.boardState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(SIZE, Arrays.deepHashCode(boardState));
    }
}
