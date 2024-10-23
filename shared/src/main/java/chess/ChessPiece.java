package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor teamColor;
    private PieceType pieceType;
    private PieceMovesFunctions pieceMovesFunctions;

    @Override
    public boolean equals(Object o) {
        if (this == o){ return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        teamColor = pieceColor;
        pieceType = type;
        pieceMovesFunctions = new PieceMovesFunctions(pieceType, teamColor);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }
    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor){
        return pieceMovesFunctions.pawnMoves(board, myPosition, teamColor);
    }

    private  Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor){
        return pieceMovesFunctions.rookMoves(board, myPosition, teamColor);
    }

    private  Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor){
        return pieceMovesFunctions.knightMoves(board, myPosition, teamColor);
    }

    private  Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor){
        return pieceMovesFunctions.bishopMoves(board, myPosition, teamColor);
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor){
        return pieceMovesFunctions.kingMoves(board, myPosition, teamColor);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        //white
        if(pieceType == PieceType.PAWN && teamColor == ChessGame.TeamColor.WHITE) {
            validMoves.addAll(pawnMoves(board, myPosition, ChessGame.TeamColor.WHITE));
        }
        if(pieceType == PieceType.ROOK && teamColor == ChessGame.TeamColor.WHITE) {
            validMoves.addAll(rookMoves(board, myPosition, ChessGame.TeamColor.WHITE));
        }
        if(pieceType == PieceType.KNIGHT && teamColor == ChessGame.TeamColor.WHITE) {
            validMoves.addAll(knightMoves(board, myPosition, ChessGame.TeamColor.WHITE));
        }
        if(pieceType == PieceType.BISHOP && teamColor == ChessGame.TeamColor.WHITE){
            validMoves.addAll(bishopMoves(board, myPosition, ChessGame.TeamColor.WHITE));
        }
            //queen
        if(pieceType ==PieceType.QUEEN && teamColor == ChessGame.TeamColor.WHITE){
            validMoves.addAll(rookMoves(board,myPosition, ChessGame.TeamColor.WHITE));
        }
        if(pieceType ==PieceType.QUEEN && teamColor == ChessGame.TeamColor.WHITE){
            validMoves.addAll(bishopMoves(board,myPosition, ChessGame.TeamColor.WHITE));
        }
            //king
        if(pieceType ==PieceType.KING && teamColor == ChessGame.TeamColor.WHITE){
            validMoves.addAll(kingMoves(board,myPosition, ChessGame.TeamColor.WHITE));
        }

        //black
        if(pieceType == PieceType.ROOK && teamColor == ChessGame.TeamColor.BLACK) {
            validMoves.addAll(rookMoves(board, myPosition, ChessGame.TeamColor.BLACK));
        }
        if(pieceType == PieceType.PAWN && teamColor == ChessGame.TeamColor.BLACK) {
            validMoves.addAll(pawnMoves(board, myPosition, ChessGame.TeamColor.BLACK));
        }
        if(pieceType == PieceType.KNIGHT && teamColor == ChessGame.TeamColor.BLACK) {
            validMoves.addAll(knightMoves(board, myPosition, ChessGame.TeamColor.BLACK));
        }
        if(pieceType == PieceType.BISHOP && teamColor == ChessGame.TeamColor.BLACK){
            validMoves.addAll(bishopMoves(board, myPosition, ChessGame.TeamColor.BLACK));
        }

            //queen
        if(pieceType ==PieceType.QUEEN && teamColor == ChessGame.TeamColor.BLACK){
            validMoves.addAll(rookMoves(board,myPosition, ChessGame.TeamColor.BLACK));
        }
        if(pieceType ==PieceType.QUEEN && teamColor == ChessGame.TeamColor.BLACK){
            validMoves.addAll(bishopMoves(board,myPosition, ChessGame.TeamColor.BLACK));
        }
        //king
        if(pieceType ==PieceType.KING && teamColor == ChessGame.TeamColor.BLACK){
            validMoves.addAll(kingMoves(board,myPosition, ChessGame.TeamColor.BLACK));
        }
        return validMoves;
    }
}
