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
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        //white team
        if(pieceType == PieceType.PAWN && teamColor == ChessGame.TeamColor.WHITE) {
            //+1 row, column
            ChessPosition up1Row = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
            ChessPiece up1RowPiece = board.getPiece(up1Row);
            //+1 row, +1 column
            ChessPosition upAndRight = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() +1);
            ChessPiece upAndRightPiece = board.getPiece(upAndRight);
            //+1 row, -1 column
            ChessPosition upAndLeft = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() -1);
            ChessPiece upAndLeftPiece = board.getPiece(upAndLeft);
            //+2 row, column
            ChessPosition up2Row = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
            ChessPiece up2RowPiece = board.getPiece(up2Row);
            //promotion
            if (up1Row.getRow() == 8){
                if(upAndLeftPiece != null && upAndLeftPiece.teamColor == ChessGame.TeamColor.BLACK) {
                    validMoves.add(new ChessMove(myPosition, upAndLeft, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, upAndLeft, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, upAndLeft, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, upAndLeft, ChessPiece.PieceType.KNIGHT));
                }
                if(upAndRightPiece != null && upAndRightPiece.teamColor == ChessGame.TeamColor.BLACK){
                    validMoves.add(new ChessMove(myPosition, upAndRight, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, upAndRight, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, upAndRight, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, upAndRight, ChessPiece.PieceType.KNIGHT));
                }
                validMoves.add(new ChessMove(myPosition, up1Row, ChessPiece.PieceType.QUEEN));
                validMoves.add(new ChessMove(myPosition, up1Row, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(myPosition, up1Row, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(myPosition, up1Row, ChessPiece.PieceType.KNIGHT));
                return validMoves;
            }
            if (up1RowPiece == null) {
                validMoves.add(new ChessMove(myPosition, up1Row, null));
            }

            if(myPosition.getRow()== 2 && (up2RowPiece == null && up1RowPiece == null )){
                validMoves.add(new ChessMove(myPosition, up2Row, null));
            }
            if(upAndLeftPiece != null && upAndLeftPiece.teamColor == ChessGame.TeamColor.BLACK) {
                validMoves.add(new ChessMove(myPosition, upAndLeft, null));
            }
            if(upAndRightPiece != null && upAndRightPiece.teamColor == ChessGame.TeamColor.BLACK){
                validMoves.add(new ChessMove(myPosition, upAndRight, null));
            }
        }
        if(pieceType == PieceType.ROOK && teamColor == ChessGame.TeamColor.WHITE) {
            //left
            for (int i = myPosition.getColumn(); i > 0; i--) {
                ChessPosition leftPosition = new ChessPosition(myPosition.getRow(), i);
                ChessPiece leftPiece = board.getPiece(leftPosition);
                if(leftPosition.getColumn() == myPosition.getColumn() && leftPosition.getRow() == myPosition.getRow()){
                    continue;
                }
                if(leftPiece != null && leftPiece.teamColor == ChessGame.TeamColor.BLACK){
                    validMoves.add(new ChessMove(myPosition, leftPosition, null));
                    break;
                } else if (leftPiece != null && leftPiece.teamColor == ChessGame.TeamColor.WHITE) {
                    break;
                }
                System.out.println("adding possible move: "+ leftPosition.getRow() + "," +leftPosition.getColumn());
                validMoves.add(new ChessMove(myPosition, leftPosition, null));
            }
            //right
            for (int i = myPosition.getColumn(); i < 9; i++) {
                ChessPosition rightPosition = new ChessPosition(myPosition.getRow(), i);
                ChessPiece rightPiece = board.getPiece(rightPosition);
                if(rightPosition.getColumn() == myPosition.getColumn() && rightPosition.getRow() == myPosition.getRow()){
                    continue;
                }
                if(rightPiece != null && rightPiece.teamColor == ChessGame.TeamColor.BLACK){
                    validMoves.add(new ChessMove(myPosition, rightPosition, null));
                    break;
                } else if (rightPiece != null && rightPiece.teamColor == ChessGame.TeamColor.WHITE) {
                    break;
                }
                validMoves.add(new ChessMove(myPosition, rightPosition, null));
                System.out.println("adding possible move: "+ rightPosition.getRow() + "," +rightPosition.getColumn());

            }
            //up (same logic as right)
            for (int i = myPosition.getRow(); i < 9; i++) {
                ChessPosition upPosition = new ChessPosition(i, myPosition.getColumn());
                ChessPiece upPiece = board.getPiece(upPosition);
                if(upPosition.getColumn() == myPosition.getColumn() && upPosition.getRow() == myPosition.getRow()){
                    continue;
                }
                if(upPiece != null && upPiece.teamColor == ChessGame.TeamColor.BLACK){
                    validMoves.add(new ChessMove(myPosition, upPosition, null));
                    break;
                } else if (upPiece != null && upPiece.teamColor == ChessGame.TeamColor.WHITE ) {
                    break;
                }
                System.out.println("adding possible move: "+ upPosition.getRow() + "," +upPosition.getColumn());
                validMoves.add(new ChessMove(myPosition, upPosition, null));
            }
            //down (same logic as left)
            for (int i = myPosition.getRow(); i > 0; i--) {
                ChessPosition downPosition = new ChessPosition(i, myPosition.getColumn());
                ChessPiece downPiece = board.getPiece(downPosition);
                if(downPosition.getColumn() == myPosition.getColumn() && downPosition.getRow() == myPosition.getRow()){
                    continue;
                }
                if(downPiece != null && downPiece.teamColor == ChessGame.TeamColor.BLACK){
                    validMoves.add(new ChessMove(myPosition, downPosition, null));
                    break;
                } else if (downPiece != null && downPiece.teamColor == ChessGame.TeamColor.WHITE ) {
                    break;
                }
                System.out.println("adding possible move: "+ downPosition.getRow() + "," +downPosition.getColumn());

                validMoves.add(new ChessMove(myPosition, downPosition, null));
            }
        }
        if(pieceType == PieceType.KNIGHT && teamColor == ChessGame.TeamColor.WHITE){
            //up left
            ChessPosition upLeft = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() -1);
            ChessPiece upLeftPiece = board.getPiece(upLeft);
            if((upLeftPiece == null && validPosition(upLeft))){
                validMoves.add(new ChessMove(myPosition, upLeft, null));
            } else if (upLeftPiece != null && upLeftPiece.teamColor == ChessGame.TeamColor.BLACK) {
                validMoves.add(new ChessMove(myPosition, upLeft, null));
            }
            //up right
            ChessPosition upRight = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() +1);
            ChessPiece upRightPiece = board.getPiece(upRight);
            if((upRightPiece == null && validPosition(upRight))){
                validMoves.add(new ChessMove(myPosition, upRight, null));
            } else if (upRightPiece != null && upRightPiece.teamColor == ChessGame.TeamColor.BLACK) {
                validMoves.add(new ChessMove(myPosition, upRight, null));
            }
            //down left
            ChessPosition downLeft = new ChessPosition(myPosition.getRow() -2, myPosition.getColumn() -1);
            ChessPiece downLeftPiece = board.getPiece(downLeft);
            if((downLeftPiece == null && validPosition(downLeft))){
                validMoves.add(new ChessMove(myPosition, downLeft, null));
            } else if (downLeftPiece != null && downLeftPiece.teamColor == ChessGame.TeamColor.BLACK) {
                validMoves.add(new ChessMove(myPosition, downLeft, null));
            }
            //down right
            ChessPosition downRight = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() +1);
            ChessPiece downRightPiece = board.getPiece(downRight);
            if((downRightPiece == null && validPosition(downRight))){
                validMoves.add(new ChessMove(myPosition, downRight, null));
            } else if (downRightPiece != null && downRightPiece.teamColor == ChessGame.TeamColor.BLACK) {
                validMoves.add(new ChessMove(myPosition, downRight, null));
            }
            //left up
            ChessPosition leftUp = new ChessPosition(myPosition.getRow() +1, myPosition.getColumn() -2);
            ChessPiece leftUpPiece = board.getPiece(leftUp);
            if((leftUpPiece == null && validPosition(leftUp))){
                validMoves.add(new ChessMove(myPosition, leftUp, null));
            } else if (leftUpPiece != null && leftUpPiece.teamColor == ChessGame.TeamColor.BLACK) {
                validMoves.add(new ChessMove(myPosition, leftUp, null));
            }
            //left down
            ChessPosition leftDown = new ChessPosition(myPosition.getRow() -1, myPosition.getColumn() -2);
            ChessPiece leftDownPiece = board.getPiece(leftDown);
            if((leftDownPiece == null && validPosition(leftDown))){
                validMoves.add(new ChessMove(myPosition, leftDown, null));
            } else if (leftDownPiece != null && leftDownPiece.teamColor == ChessGame.TeamColor.BLACK) {
                validMoves.add(new ChessMove(myPosition, leftDown, null));
            }
            //right up
            ChessPosition rightUp = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn() +2);
            ChessPiece rightUpPiece = board.getPiece(rightUp);
            if((rightUpPiece == null && validPosition(rightUp))){
                validMoves.add(new ChessMove(myPosition, rightUp, null));
            } else if (rightUpPiece != null && rightUpPiece.teamColor == ChessGame.TeamColor.BLACK) {
                validMoves.add(new ChessMove(myPosition, rightUp, null));
            }
            //right down
            ChessPosition rightDown = new ChessPosition(myPosition.getRow()- 1, myPosition.getColumn() +2);
            ChessPiece rightDownPiece = board.getPiece(rightDown);
            if((rightDownPiece == null && validPosition(rightDown))){
                validMoves.add(new ChessMove(myPosition, rightDown, null));
            } else if (rightDownPiece != null && rightDownPiece.teamColor == ChessGame.TeamColor.BLACK) {
                validMoves.add(new ChessMove(myPosition, rightDown, null));
            }
        }

        //black team
        if(pieceType == PieceType.PAWN && teamColor == ChessGame.TeamColor.BLACK) {
            //-1 row, column
            ChessPosition down1Row = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
            ChessPiece down1RowPiece = board.getPiece(down1Row);
            //-2 row, column
            ChessPosition down2Row = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
            ChessPiece down2RowPiece = board.getPiece(down2Row);
            //-1 row, -1 column
            ChessPosition downAndLeft = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() -1);
            ChessPiece downAndLeftPiece = board.getPiece(downAndLeft);
            //-1 row, +1 column
            ChessPosition downAndRight = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() +1);
            ChessPiece downAndRightPiece = board.getPiece(downAndRight);
            //promotion
            if (down1Row.getRow() == 1){
                if(downAndLeftPiece != null && downAndLeftPiece.teamColor == ChessGame.TeamColor.WHITE) {
                    validMoves.add(new ChessMove(myPosition, downAndLeft, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, downAndLeft, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, downAndLeft, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, downAndLeft, ChessPiece.PieceType.KNIGHT));
                }
                if(downAndRightPiece != null && downAndRightPiece.teamColor == ChessGame.TeamColor.WHITE){
                    validMoves.add(new ChessMove(myPosition, downAndRight, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, downAndRight, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, downAndRight, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(myPosition, downAndRight, ChessPiece.PieceType.KNIGHT));
                }
                validMoves.add(new ChessMove(myPosition, down1Row, ChessPiece.PieceType.QUEEN));
                validMoves.add(new ChessMove(myPosition, down1Row, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(myPosition, down1Row, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(myPosition, down1Row, ChessPiece.PieceType.KNIGHT));
                return validMoves;
            }
            if (down1RowPiece == null) {
                validMoves.add(new ChessMove(myPosition, down1Row, null));
            }

            if(myPosition.getRow()== 7 && (down2RowPiece == null && down1RowPiece == null )){
                validMoves.add(new ChessMove(myPosition, down2Row, null));
            }

            if(downAndLeftPiece != null && downAndLeftPiece.teamColor == ChessGame.TeamColor.WHITE){
                validMoves.add(new ChessMove(myPosition, downAndLeft, null));
            }

            if(downAndRightPiece != null && downAndRightPiece.teamColor == ChessGame.TeamColor.WHITE){
                validMoves.add(new ChessMove(myPosition, downAndRight, null));
            }
        }
        if(pieceType == PieceType.ROOK && teamColor == ChessGame.TeamColor.BLACK) {
            //left
            for (int i = myPosition.getColumn(); i > 0; i--) {
                ChessPosition leftPosition = new ChessPosition(myPosition.getRow(), i);
                ChessPiece leftPiece = board.getPiece(leftPosition);
                if(leftPosition.getColumn() == myPosition.getColumn() && leftPosition.getRow() == myPosition.getRow()){
                    continue;
                }
                if(leftPiece != null && leftPiece.teamColor == ChessGame.TeamColor.WHITE){
                    validMoves.add(new ChessMove(myPosition, leftPosition, null));
                    break;
                } else if (leftPiece != null && leftPiece.teamColor == ChessGame.TeamColor.BLACK) {
                    break;
                }
                System.out.println("adding possible move: "+ leftPosition.getRow() + "," +leftPosition.getColumn());
                validMoves.add(new ChessMove(myPosition, leftPosition, null));
            }
            //right
            for (int i = myPosition.getColumn(); i < 9; i++) {
                ChessPosition rightPosition = new ChessPosition(myPosition.getRow(), i);
                ChessPiece rightPiece = board.getPiece(rightPosition);
                if(rightPosition.getColumn() == myPosition.getColumn() && rightPosition.getRow() == myPosition.getRow()){
                    continue;
                }
                if(rightPiece != null && rightPiece.teamColor == ChessGame.TeamColor.WHITE){
                    validMoves.add(new ChessMove(myPosition, rightPosition, null));
                    break;
                } else if (rightPiece != null && rightPiece.teamColor == ChessGame.TeamColor.BLACK) {
                    break;
                }
                validMoves.add(new ChessMove(myPosition, rightPosition, null));
                System.out.println("adding possible move: "+ rightPosition.getRow() + "," +rightPosition.getColumn());

            }
            //up (same logic as right)
            for (int i = myPosition.getRow(); i < 9; i++) {
                ChessPosition upPosition = new ChessPosition(i, myPosition.getColumn());
                ChessPiece upPiece = board.getPiece(upPosition);
                if(upPosition.getColumn() == myPosition.getColumn() && upPosition.getRow() == myPosition.getRow()){
                    continue;
                }
                if(upPiece != null && upPiece.teamColor == ChessGame.TeamColor.WHITE){
                    validMoves.add(new ChessMove(myPosition, upPosition, null));
                    break;
                } else if (upPiece != null && upPiece.teamColor == ChessGame.TeamColor.BLACK ) {
                    break;
                }
                System.out.println("adding possible move: "+ upPosition.getRow() + "," +upPosition.getColumn());
                validMoves.add(new ChessMove(myPosition, upPosition, null));
            }
            //down (same logic as left)
            for (int i = myPosition.getRow(); i > 0; i--) {
                ChessPosition downPosition = new ChessPosition(i, myPosition.getColumn());
                ChessPiece downPiece = board.getPiece(downPosition);
                if(downPosition.getColumn() == myPosition.getColumn() && downPosition.getRow() == myPosition.getRow()){
                    continue;
                }
                if(downPiece != null && downPiece.teamColor == ChessGame.TeamColor.WHITE){
                    validMoves.add(new ChessMove(myPosition, downPosition, null));
                    break;
                } else if (downPiece != null && downPiece.teamColor == ChessGame.TeamColor.BLACK) {
                    break;
                }
                System.out.println("adding possible move: "+ downPosition.getRow() + "," +downPosition.getColumn());

                validMoves.add(new ChessMove(myPosition, downPosition, null));
            }
        }
        if(pieceType == PieceType.KNIGHT && teamColor == ChessGame.TeamColor.BLACK){
            //up left
            ChessPosition upLeft = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() -1);
            ChessPiece upLeftPiece = board.getPiece(upLeft);
            if((upLeftPiece == null && validPosition(upLeft))){
                validMoves.add(new ChessMove(myPosition, upLeft, null));
            } else if (upLeftPiece != null && upLeftPiece.teamColor == ChessGame.TeamColor.WHITE) {
                validMoves.add(new ChessMove(myPosition, upLeft, null));
            }
            //up right
            ChessPosition upRight = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() +1);
            ChessPiece upRightPiece = board.getPiece(upRight);
            if((upRightPiece == null && validPosition(upRight))){
                validMoves.add(new ChessMove(myPosition, upRight, null));
            } else if (upRightPiece != null && upRightPiece.teamColor == ChessGame.TeamColor.WHITE) {
                validMoves.add(new ChessMove(myPosition, upRight, null));
            }
            //down left
            ChessPosition downLeft = new ChessPosition(myPosition.getRow() -2, myPosition.getColumn() -1);
            ChessPiece downLeftPiece = board.getPiece(downLeft);
            if((downLeftPiece == null && validPosition(downLeft))){
                validMoves.add(new ChessMove(myPosition, downLeft, null));
            } else if (downLeftPiece != null && downLeftPiece.teamColor == ChessGame.TeamColor.WHITE) {
                validMoves.add(new ChessMove(myPosition, downLeft, null));
            }
            //down right
            ChessPosition downRight = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() +1);
            ChessPiece downRightPiece = board.getPiece(downRight);
            if((downRightPiece == null && validPosition(downRight))){
                validMoves.add(new ChessMove(myPosition, downRight, null));
            } else if (downRightPiece != null && downRightPiece.teamColor == ChessGame.TeamColor.WHITE) {
                validMoves.add(new ChessMove(myPosition, downRight, null));
            }
            //left up
            ChessPosition leftUp = new ChessPosition(myPosition.getRow() +1, myPosition.getColumn() -2);
            ChessPiece leftUpPiece = board.getPiece(leftUp);
            if((leftUpPiece == null && validPosition(leftUp))){
                validMoves.add(new ChessMove(myPosition, leftUp, null));
            } else if (leftUpPiece != null && leftUpPiece.teamColor == ChessGame.TeamColor.WHITE) {
                validMoves.add(new ChessMove(myPosition, leftUp, null));
            }
            //left down
            ChessPosition leftDown = new ChessPosition(myPosition.getRow() -1, myPosition.getColumn() -2);
            ChessPiece leftDownPiece = board.getPiece(leftDown);
            if((leftDownPiece == null && validPosition(leftDown))){
                validMoves.add(new ChessMove(myPosition, leftDown, null));
            } else if (leftDownPiece != null && leftDownPiece.teamColor == ChessGame.TeamColor.WHITE) {
                validMoves.add(new ChessMove(myPosition, leftDown, null));
            }
            //right up
            ChessPosition rightUp = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn() +2);
            ChessPiece rightUpPiece = board.getPiece(rightUp);
            if((rightUpPiece == null && validPosition(rightUp))){
                validMoves.add(new ChessMove(myPosition, rightUp, null));
            } else if (rightUpPiece != null && rightUpPiece.teamColor == ChessGame.TeamColor.WHITE) {
                validMoves.add(new ChessMove(myPosition, rightUp, null));
            }
            //right down
            ChessPosition rightDown = new ChessPosition(myPosition.getRow()- 1, myPosition.getColumn() +2);
            ChessPiece rightDownPiece = board.getPiece(rightDown);
            if((rightDownPiece == null && validPosition(rightDown))){
                validMoves.add(new ChessMove(myPosition, rightDown, null));
            } else if (rightDownPiece != null && rightDownPiece.teamColor == ChessGame.TeamColor.WHITE) {
                validMoves.add(new ChessMove(myPosition, rightDown, null));
            }
        }

        return validMoves;
    }
}
