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
    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        teamColor = pieceColor;
        pieceType = type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }

    private boolean validPosition(ChessPosition position){
        if(position.getRow() > 8 || position.getRow()< 1){
            return false;
        }
        else if(position.getColumn() > 8 || position.getColumn()< 1){
            return false;
        }
        else return true;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor){
        Collection<ChessMove> pawnMoves = new ArrayList<>();
            ChessPosition forwardMove = null;
            ChessPosition twiceForwardMove = null;
            ChessPosition leftDiagonal = null;
            ChessPosition rightDiagonal =null;

            ChessPiece forwardFoundPiece = null;
            ChessPiece twiceForwardFoundPiece = null;
            ChessPiece leftDiagonalFoundPiece = null;
            ChessPiece rightDiagonalFoundPiece = null;

            if (teamColor == ChessGame.TeamColor.WHITE){
                forwardMove = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
                forwardFoundPiece = board.getPiece(forwardMove);
                twiceForwardMove  = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
                twiceForwardFoundPiece = board.getPiece(twiceForwardMove);
                rightDiagonal = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() +1);
                rightDiagonalFoundPiece = board.getPiece(rightDiagonal);
                leftDiagonal = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() -1);
                leftDiagonalFoundPiece = board.getPiece(leftDiagonal);

            }else if (teamColor == ChessGame.TeamColor.BLACK){
                forwardMove = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
                forwardFoundPiece = board.getPiece(forwardMove);
                twiceForwardMove  = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                twiceForwardFoundPiece = board.getPiece(twiceForwardMove);
                rightDiagonal = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
                rightDiagonalFoundPiece = board.getPiece(rightDiagonal);
                leftDiagonal = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
                leftDiagonalFoundPiece = board.getPiece(leftDiagonal);
            }
            //promotion
        //white specific return early
        if(teamColor == ChessGame.TeamColor.WHITE){
            //promotion
            if (forwardMove.getRow() == 8){
                if(leftDiagonalFoundPiece != null && leftDiagonalFoundPiece.teamColor == ChessGame.TeamColor.BLACK) {
                    pawnMoves.add(new ChessMove(myPosition, leftDiagonal, PieceType.QUEEN));
                    pawnMoves.add(new ChessMove(myPosition, leftDiagonal, PieceType.BISHOP));
                    pawnMoves.add(new ChessMove(myPosition, leftDiagonal, PieceType.ROOK));
                    pawnMoves.add(new ChessMove(myPosition, leftDiagonal, PieceType.KNIGHT));
                }
                if(rightDiagonalFoundPiece != null && rightDiagonalFoundPiece.teamColor == ChessGame.TeamColor.BLACK){
                    pawnMoves.add(new ChessMove(myPosition, rightDiagonal, PieceType.QUEEN));
                    pawnMoves.add(new ChessMove(myPosition, rightDiagonal, PieceType.BISHOP));
                    pawnMoves.add(new ChessMove(myPosition, rightDiagonal, PieceType.ROOK));
                    pawnMoves.add(new ChessMove(myPosition, rightDiagonal, PieceType.KNIGHT));
                }
                pawnMoves.add(new ChessMove(myPosition, forwardMove, PieceType.QUEEN));
                pawnMoves.add(new ChessMove(myPosition, forwardMove, PieceType.BISHOP));
                pawnMoves.add(new ChessMove(myPosition, forwardMove, PieceType.ROOK));
                pawnMoves.add(new ChessMove(myPosition, forwardMove, PieceType.KNIGHT));
                return pawnMoves;
            }

            //double forward
        }
        //black specific return early
        if(teamColor == ChessGame.TeamColor.BLACK){
            //promotion
            if (forwardMove.getRow() == 1){
                if(leftDiagonalFoundPiece != null && leftDiagonalFoundPiece.teamColor == ChessGame.TeamColor.WHITE) {
                    pawnMoves.add(new ChessMove(myPosition, leftDiagonal, PieceType.QUEEN));
                    pawnMoves.add(new ChessMove(myPosition, leftDiagonal, PieceType.BISHOP));
                    pawnMoves.add(new ChessMove(myPosition, leftDiagonal, PieceType.ROOK));
                    pawnMoves.add(new ChessMove(myPosition, leftDiagonal, PieceType.KNIGHT));
                }
                if(rightDiagonalFoundPiece != null && rightDiagonalFoundPiece.teamColor == ChessGame.TeamColor.WHITE){
                    pawnMoves.add(new ChessMove(myPosition, rightDiagonal, PieceType.QUEEN));
                    pawnMoves.add(new ChessMove(myPosition, rightDiagonal, PieceType.BISHOP));
                    pawnMoves.add(new ChessMove(myPosition, rightDiagonal, PieceType.ROOK));
                    pawnMoves.add(new ChessMove(myPosition, rightDiagonal, PieceType.KNIGHT));
                }
                pawnMoves.add(new ChessMove(myPosition, forwardMove, PieceType.QUEEN));
                pawnMoves.add(new ChessMove(myPosition, forwardMove, PieceType.BISHOP));
                pawnMoves.add(new ChessMove(myPosition, forwardMove, PieceType.ROOK));
                pawnMoves.add(new ChessMove(myPosition, forwardMove, PieceType.KNIGHT));
                return pawnMoves;
            }

            //double forward
        }

        if(teamColor == ChessGame.TeamColor.WHITE){
            if(myPosition.getRow()== 2 && (twiceForwardFoundPiece == null && forwardFoundPiece == null )){
                pawnMoves.add(new ChessMove(myPosition, twiceForwardMove, null));
            }
        }

        if(teamColor == ChessGame.TeamColor.BLACK){
            if(myPosition.getRow()== 7 && (twiceForwardFoundPiece == null && forwardFoundPiece == null )){
                pawnMoves.add(new ChessMove(myPosition, twiceForwardMove, null));
            }
        }

        if (forwardFoundPiece == null) {
                pawnMoves.add(new ChessMove(myPosition, forwardMove, null));
            }

            if(leftDiagonalFoundPiece != null && leftDiagonalFoundPiece.teamColor != teamColor) {
                pawnMoves.add(new ChessMove(myPosition, leftDiagonal, null));
            }
            if(rightDiagonalFoundPiece != null && rightDiagonalFoundPiece.teamColor != teamColor){
                pawnMoves.add(new ChessMove(myPosition, rightDiagonal, null));
            }
        return pawnMoves;
    }

    private  Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor){
        Collection<ChessMove> rookMoves = new ArrayList<>();
        //left
            for (int i = myPosition.getColumn(); i > 0; i--) {
                ChessPosition leftPosition = new ChessPosition(myPosition.getRow(), i);
                ChessPiece leftPiece = board.getPiece(leftPosition);
                if(leftPosition.getColumn() == myPosition.getColumn() && leftPosition.getRow() == myPosition.getRow()){
                    continue;
                }
                if(leftPiece != null && leftPiece.teamColor != teamColor){
                    rookMoves.add(new ChessMove(myPosition, leftPosition, null));
                    break;
                } else if (leftPiece != null && leftPiece.teamColor == teamColor) {
                    break;
                }
                System.out.println("adding possible move: "+ leftPosition.getRow() + "," +leftPosition.getColumn());
                rookMoves.add(new ChessMove(myPosition, leftPosition, null));
            }
            //right
            for (int i = myPosition.getColumn(); i < 9; i++) {
                ChessPosition rightPosition = new ChessPosition(myPosition.getRow(), i);
                ChessPiece rightPiece = board.getPiece(rightPosition);
                if(rightPosition.getColumn() == myPosition.getColumn() && rightPosition.getRow() == myPosition.getRow()){
                    continue;
                }
                if(rightPiece != null && rightPiece.teamColor != teamColor){
                    rookMoves.add(new ChessMove(myPosition, rightPosition, null));
                    break;
                } else if (rightPiece != null && rightPiece.teamColor == teamColor) {
                    break;
                }
                rookMoves.add(new ChessMove(myPosition, rightPosition, null));
                System.out.println("adding possible move: "+ rightPosition.getRow() + "," +rightPosition.getColumn());

            }
            //up (same logic as right)
            for (int i = myPosition.getRow(); i < 9; i++) {
                ChessPosition upPosition = new ChessPosition(i, myPosition.getColumn());
                ChessPiece upPiece = board.getPiece(upPosition);
                if(upPosition.getColumn() == myPosition.getColumn() && upPosition.getRow() == myPosition.getRow()){
                    continue;
                }
                if(upPiece != null && upPiece.teamColor != teamColor){
                    rookMoves.add(new ChessMove(myPosition, upPosition, null));
                    break;
                } else if (upPiece != null && upPiece.teamColor == teamColor ) {
                    break;
                }
                System.out.println("adding possible move: "+ upPosition.getRow() + "," +upPosition.getColumn());
                rookMoves.add(new ChessMove(myPosition, upPosition, null));
            }
            //down (same logic as left)
            for (int i = myPosition.getRow(); i > 0; i--) {
                ChessPosition downPosition = new ChessPosition(i, myPosition.getColumn());
                ChessPiece downPiece = board.getPiece(downPosition);
                if(downPosition.getColumn() == myPosition.getColumn() && downPosition.getRow() == myPosition.getRow()){
                    continue;
                }
                if(downPiece != null && downPiece.teamColor != teamColor){
                    rookMoves.add(new ChessMove(myPosition, downPosition, null));
                    break;
                } else if (downPiece != null && downPiece.teamColor == teamColor ) {
                    break;
                }
                System.out.println("adding possible move: "+ downPosition.getRow() + "," +downPosition.getColumn());

                rookMoves.add(new ChessMove(myPosition, downPosition, null));
            }

    return rookMoves;
    }

    private  Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor){
        Collection<ChessMove> knightMoves = new ArrayList<>();
            //up left
            ChessPosition upLeft = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() -1);
            ChessPiece upLeftPiece = board.getPiece(upLeft);
            if((upLeftPiece == null && validPosition(upLeft))){
                knightMoves.add(new ChessMove(myPosition, upLeft, null));
            } else if (upLeftPiece != null && upLeftPiece.teamColor != teamColor) {
                knightMoves.add(new ChessMove(myPosition, upLeft, null));
            }
            //up right
            ChessPosition upRight = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() +1);
            ChessPiece upRightPiece = board.getPiece(upRight);
            if((upRightPiece == null && validPosition(upRight))){
                knightMoves.add(new ChessMove(myPosition, upRight, null));
            } else if (upRightPiece != null && upRightPiece.teamColor != teamColor) {
                knightMoves.add(new ChessMove(myPosition, upRight, null));
            }
            //down left
            ChessPosition downLeft = new ChessPosition(myPosition.getRow() -2, myPosition.getColumn() -1);
            ChessPiece downLeftPiece = board.getPiece(downLeft);
            if((downLeftPiece == null && validPosition(downLeft))){
                knightMoves.add(new ChessMove(myPosition, downLeft, null));
            } else if (downLeftPiece != null && downLeftPiece.teamColor != teamColor) {
                knightMoves.add(new ChessMove(myPosition, downLeft, null));
            }
            //down right
            ChessPosition downRight = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() +1);
            ChessPiece downRightPiece = board.getPiece(downRight);
            if((downRightPiece == null && validPosition(downRight))){
                knightMoves.add(new ChessMove(myPosition, downRight, null));
            } else if (downRightPiece != null && downRightPiece.teamColor != teamColor) {
                knightMoves.add(new ChessMove(myPosition, downRight, null));
            }
            //left up
            ChessPosition leftUp = new ChessPosition(myPosition.getRow() +1, myPosition.getColumn() -2);
            ChessPiece leftUpPiece = board.getPiece(leftUp);
            if((leftUpPiece == null && validPosition(leftUp))){
                knightMoves.add(new ChessMove(myPosition, leftUp, null));
            } else if (leftUpPiece != null && leftUpPiece.teamColor != teamColor) {
                knightMoves.add(new ChessMove(myPosition, leftUp, null));
            }
            //left down
            ChessPosition leftDown = new ChessPosition(myPosition.getRow() -1, myPosition.getColumn() -2);
            ChessPiece leftDownPiece = board.getPiece(leftDown);
            if((leftDownPiece == null && validPosition(leftDown))){
                knightMoves.add(new ChessMove(myPosition, leftDown, null));
            } else if (leftDownPiece != null && leftDownPiece.teamColor != teamColor) {
                knightMoves.add(new ChessMove(myPosition, leftDown, null));
            }
            //right up
            ChessPosition rightUp = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn() +2);
            ChessPiece rightUpPiece = board.getPiece(rightUp);
            if((rightUpPiece == null && validPosition(rightUp))){
                knightMoves.add(new ChessMove(myPosition, rightUp, null));
            } else if (rightUpPiece != null && rightUpPiece.teamColor != teamColor) {
                knightMoves.add(new ChessMove(myPosition, rightUp, null));
            }
            //right down
            ChessPosition rightDown = new ChessPosition(myPosition.getRow()- 1, myPosition.getColumn() +2);
            ChessPiece rightDownPiece = board.getPiece(rightDown);
            if((rightDownPiece == null && validPosition(rightDown))){
                knightMoves.add(new ChessMove(myPosition, rightDown, null));
            } else if (rightDownPiece != null && rightDownPiece.teamColor != teamColor) {
                knightMoves.add(new ChessMove(myPosition, rightDown, null));
            }
    return knightMoves;
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



        return validMoves;
    }
}
