package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesFunctions {
    private ChessPiece.PieceType pieceType;
    private ChessGame.TeamColor teamColor;

    public PieceMovesFunctions(ChessPiece.PieceType pieceType, ChessGame.TeamColor teamColor) {
        this.pieceType = pieceType;
        this.teamColor = teamColor;
    }

    private Collection<ChessMove> addDiagonalOrForwardMoves(ChessPosition myPosition, ChessPosition nextPosition){
        Collection<ChessMove> pawnMoves = new ArrayList<>();
        pawnMoves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.QUEEN));
        pawnMoves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.BISHOP));
        pawnMoves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.ROOK));
        pawnMoves.add(new ChessMove(myPosition, nextPosition, ChessPiece.PieceType.KNIGHT));
        return pawnMoves;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    private boolean validPosition(ChessPosition position){
        if(position.getRow() > 8 || position.getRow()< 1){
            return false;
        }
        else if(position.getColumn() > 8 || position.getColumn()< 1){
            return false;
        }
        else {return true;}
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor){
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
                if(leftDiagonalFoundPiece != null && leftDiagonalFoundPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    pawnMoves.addAll(addDiagonalOrForwardMoves(myPosition, leftDiagonal));
                }
                if(rightDiagonalFoundPiece != null && rightDiagonalFoundPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
                    pawnMoves.addAll(addDiagonalOrForwardMoves(myPosition, rightDiagonal));
                }
                pawnMoves.addAll(addDiagonalOrForwardMoves(myPosition, forwardMove));
                return pawnMoves;
            }
            //double forward
        }
        //black specific return early
        if(teamColor == ChessGame.TeamColor.BLACK){
            //promotion
            if (forwardMove.getRow() == 1){
                if(leftDiagonalFoundPiece != null && leftDiagonalFoundPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    pawnMoves.addAll(addDiagonalOrForwardMoves(myPosition, leftDiagonal));
                }
                if(rightDiagonalFoundPiece != null && rightDiagonalFoundPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    pawnMoves.addAll(addDiagonalOrForwardMoves(myPosition, rightDiagonal));
                }
                pawnMoves.addAll(addDiagonalOrForwardMoves(myPosition, forwardMove));
                return pawnMoves;
            }
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

        if(leftDiagonalFoundPiece != null && leftDiagonalFoundPiece.getTeamColor() != teamColor) {
            pawnMoves.add(new ChessMove(myPosition, leftDiagonal, null));
        }
        if(rightDiagonalFoundPiece != null && rightDiagonalFoundPiece.getTeamColor() != teamColor){
            pawnMoves.add(new ChessMove(myPosition, rightDiagonal, null));
        }
        return pawnMoves;
    }

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor){
        Collection<ChessMove> rookMoves = new ArrayList<>();
        //left
        for (int i = myPosition.getColumn(); i > 0; i--) {
            ChessPosition leftPosition = new ChessPosition(myPosition.getRow(), i);
            ChessPiece leftPiece = board.getPiece(leftPosition);
            if(leftPosition.getColumn() == myPosition.getColumn() && leftPosition.getRow() == myPosition.getRow()){
                continue;
            }
            if(leftPiece != null && leftPiece.getTeamColor() != teamColor){
                rookMoves.add(new ChessMove(myPosition, leftPosition, null));
                break;
            } else if (leftPiece != null && leftPiece.getTeamColor() == teamColor) {
                break;
            }
            //System.out.println("adding possible move: "+ leftPosition.getRow() + "," +leftPosition.getColumn());
            rookMoves.add(new ChessMove(myPosition, leftPosition, null));
        }
        //right
        for (int i = myPosition.getColumn(); i < 9; i++) {
            ChessPosition rightPosition = new ChessPosition(myPosition.getRow(), i);
            ChessPiece rightPiece = board.getPiece(rightPosition);
            if(rightPosition.getColumn() == myPosition.getColumn() && rightPosition.getRow() == myPosition.getRow()){
                continue;
            }
            if(rightPiece != null && rightPiece.getTeamColor() != teamColor){
                rookMoves.add(new ChessMove(myPosition, rightPosition, null));
                break;
            } else if (rightPiece != null && rightPiece.getTeamColor() == teamColor) {
                break;
            }
            rookMoves.add(new ChessMove(myPosition, rightPosition, null));
            //System.out.println("adding possible move: "+ rightPosition.getRow() + "," +rightPosition.getColumn());

        }
        //up (same logic as right)
        for (int i = myPosition.getRow(); i < 9; i++) {
            ChessPosition upPosition = new ChessPosition(i, myPosition.getColumn());
            ChessPiece upPiece = board.getPiece(upPosition);
            if(upPosition.getColumn() == myPosition.getColumn() && upPosition.getRow() == myPosition.getRow()){
                continue;
            }
            if(upPiece != null && upPiece.getTeamColor() != teamColor){
                rookMoves.add(new ChessMove(myPosition, upPosition, null));
                break;
            } else if (upPiece != null && upPiece.getTeamColor() == teamColor ) {
                break;
            }
            //System.out.println("adding possible move: "+ upPosition.getRow() + "," +upPosition.getColumn());
            rookMoves.add(new ChessMove(myPosition, upPosition, null));
        }
        //down (same logic as left)
        for (int i = myPosition.getRow(); i > 0; i--) {
            ChessPosition downPosition = new ChessPosition(i, myPosition.getColumn());
            ChessPiece downPiece = board.getPiece(downPosition);
            if(downPosition.getColumn() == myPosition.getColumn() && downPosition.getRow() == myPosition.getRow()){
                continue;
            }
            if(downPiece != null && downPiece.getTeamColor() != teamColor){
                rookMoves.add(new ChessMove(myPosition, downPosition, null));
                break;
            } else if (downPiece != null && downPiece.getTeamColor() == teamColor ) {
                break;
            }
            //System.out.println("adding possible move: "+ downPosition.getRow() + "," +downPosition.getColumn());

            rookMoves.add(new ChessMove(myPosition, downPosition, null));
        }

        return rookMoves;
    }

    public  Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor){
        Collection<ChessMove> knightMoves = new ArrayList<>();
        //up left
        ChessPosition upLeft = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() -1);
        ChessPiece upLeftPiece = board.getPiece(upLeft);
        if((upLeftPiece == null && validPosition(upLeft))){
            knightMoves.add(new ChessMove(myPosition, upLeft, null));
        } else if (upLeftPiece != null && upLeftPiece.getTeamColor() != teamColor) {
            knightMoves.add(new ChessMove(myPosition, upLeft, null));
        }
        //up right
        ChessPosition upRight = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() +1);
        ChessPiece upRightPiece = board.getPiece(upRight);
        if((upRightPiece == null && validPosition(upRight))){
            knightMoves.add(new ChessMove(myPosition, upRight, null));
        } else if (upRightPiece != null && upRightPiece.getTeamColor() != teamColor) {
            knightMoves.add(new ChessMove(myPosition, upRight, null));
        }
        //down left
        ChessPosition downLeft = new ChessPosition(myPosition.getRow() -2, myPosition.getColumn() -1);
        ChessPiece downLeftPiece = board.getPiece(downLeft);
        if((downLeftPiece == null && validPosition(downLeft))){
            knightMoves.add(new ChessMove(myPosition, downLeft, null));
        } else if (downLeftPiece != null && downLeftPiece.getTeamColor() != teamColor) {
            knightMoves.add(new ChessMove(myPosition, downLeft, null));
        }
        //down right
        ChessPosition downRight = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() +1);
        ChessPiece downRightPiece = board.getPiece(downRight);
        if((downRightPiece == null && validPosition(downRight))){
            knightMoves.add(new ChessMove(myPosition, downRight, null));
        } else if (downRightPiece != null && downRightPiece.getTeamColor() != teamColor) {
            knightMoves.add(new ChessMove(myPosition, downRight, null));
        }
        //left up
        ChessPosition leftUp = new ChessPosition(myPosition.getRow() +1, myPosition.getColumn() -2);
        ChessPiece leftUpPiece = board.getPiece(leftUp);
        if((leftUpPiece == null && validPosition(leftUp))){
            knightMoves.add(new ChessMove(myPosition, leftUp, null));
        } else if (leftUpPiece != null && leftUpPiece.getTeamColor() != teamColor) {
            knightMoves.add(new ChessMove(myPosition, leftUp, null));
        }
        //left down
        ChessPosition leftDown = new ChessPosition(myPosition.getRow() -1, myPosition.getColumn() -2);
        ChessPiece leftDownPiece = board.getPiece(leftDown);
        if((leftDownPiece == null && validPosition(leftDown))){
            knightMoves.add(new ChessMove(myPosition, leftDown, null));
        } else if (leftDownPiece != null && leftDownPiece.getTeamColor() != teamColor) {
            knightMoves.add(new ChessMove(myPosition, leftDown, null));
        }
        //right up
        ChessPosition rightUp = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn() +2);
        ChessPiece rightUpPiece = board.getPiece(rightUp);
        if((rightUpPiece == null && validPosition(rightUp))){
            knightMoves.add(new ChessMove(myPosition, rightUp, null));
        } else if (rightUpPiece != null && rightUpPiece.getTeamColor() != teamColor) {
            knightMoves.add(new ChessMove(myPosition, rightUp, null));
        }
        //right down
        ChessPosition rightDown = new ChessPosition(myPosition.getRow()- 1, myPosition.getColumn() +2);
        ChessPiece rightDownPiece = board.getPiece(rightDown);
        if((rightDownPiece == null && validPosition(rightDown))){
            knightMoves.add(new ChessMove(myPosition, rightDown, null));
        } else if (rightDownPiece != null && rightDownPiece.getTeamColor() != teamColor) {
            knightMoves.add(new ChessMove(myPosition, rightDown, null));
        }
        return knightMoves;
    }

    public   Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor){
        Collection<ChessMove> bishopMoves = new ArrayList<>();
        //calculate diagonal movements
        //upRight
        int upRightColumn = myPosition.getColumn();
        for (int r = myPosition.getRow(); r <= 8; r++) {
            ChessPosition upRight = new ChessPosition(r, upRightColumn);
            ChessPiece upRightPiece = board.getPiece(upRight);
            if(upRight.getColumn() == myPosition.getColumn() && upRight.getRow() == myPosition.getRow()){
                upRightColumn++;
                continue;
            }
            if(upRightPiece != null && upRightPiece.getTeamColor() != teamColor){
                //System.out.println("UP RIGHT BREAKING found opponent " + r + "," + upRightColumn);
                bishopMoves.add(new ChessMove(myPosition, upRight, null));
                break;
            }else if (upRightPiece != null && upRightPiece.getTeamColor() == teamColor){
                //System.out.println("UP RIGHT BREAKING found self " + r + "," + upRightColumn);
                break;
            }
            if(upRight.getRow() != -111 && upRight.getColumn() != -111){
                //System.out.println("UP RIGHT checking " + r + "," + upRightColumn);
                bishopMoves.add(new ChessMove(myPosition, upRight, null));
            }

            upRightColumn++;
        }
        //upLeft
        int upLeftColumn = myPosition.getColumn();
        for (int r = myPosition.getRow(); r <= 8; r++) {
            ChessPosition upLeft = new ChessPosition(r, upLeftColumn);
            ChessPiece upLeftPiece = board.getPiece(upLeft);
            if(upLeft.getColumn() == myPosition.getColumn() && upLeft.getRow() == myPosition.getRow()){
                upLeftColumn--;
                continue;
            }
            if(upLeftPiece != null && upLeftPiece.getTeamColor() != teamColor){
                //System.out.println("UP RIGHT BREAKING found opponent " + r + "," + upLeftColumn);
                bishopMoves.add(new ChessMove(myPosition, upLeft, null));
                break;
            }else if (upLeftPiece != null && upLeftPiece.getTeamColor() == teamColor){
                //System.out.println("UP RIGHT BREAKING found self " + r + "," + upLeftColumn);
                break;
            }
            if(upLeft.getRow() != -111 && upLeft.getColumn() != -111) {
                //System.out.println("UP LEFT checking " + r + "," + upLeftColumn);
                bishopMoves.add(new ChessMove(myPosition, upLeft, null));
            }
            upLeftColumn--;
        }
        //downRight
        int downRightColumn = myPosition.getColumn();
        for (int r = myPosition.getRow(); r >= 1; r--) {
            ChessPosition downRight = new ChessPosition(r, downRightColumn);
            ChessPiece downRightPiece = board.getPiece(downRight);
            if(downRight.getColumn() == myPosition.getColumn() && downRight.getRow() == myPosition.getRow()){
                downRightColumn++;
                continue;
            }
            if(downRightPiece != null && downRightPiece.getTeamColor() != teamColor){
                //System.out.println("UP RIGHT BREAKING found opponent " + r + "," + downRightColumn);
                bishopMoves.add(new ChessMove(myPosition, downRight, null));
                break;
            }else if (downRightPiece != null && downRightPiece.getTeamColor() == teamColor){
                //System.out.println("UP RIGHT BREAKING found self " + r + "," + downRightColumn);
                break;
            }
            if(downRight.getRow() != -111 && downRight.getColumn() != -111) {
                // System.out.println("DOWN RIGHT checking " + r + "," + downRightColumn);
                bishopMoves.add(new ChessMove(myPosition, downRight, null));
            }
            downRightColumn++;
        }
        //downLeft
        int downLeftColumn = myPosition.getColumn();
        for (int r = myPosition.getRow(); r >= 1; r--) {
            ChessPosition downLeft = new ChessPosition(r, downLeftColumn);
            ChessPiece downLeftPiece = board.getPiece(downLeft);
            if(downLeft.getColumn() == myPosition.getColumn() && downLeft.getRow() == myPosition.getRow()){
                downLeftColumn--;
                continue;
            }
            if(downLeftPiece != null && downLeftPiece.getTeamColor() != teamColor){
                // System.out.println("UP RIGHT BREAKING found opponent " + r + "," + downLeftColumn);
                bishopMoves.add(new ChessMove(myPosition, downLeft, null));
                break;
            }else if (downLeftPiece != null && downLeftPiece.getTeamColor() == teamColor){
                // System.out.println("UP RIGHT BREAKING found self " + r + "," + downLeftColumn);
                break;
            }
            if(downLeft.getRow() != -111 && downLeft.getColumn() != -111) {
                // System.out.println("DOWN LEFT checking " + r + "," + downLeftColumn);
                bishopMoves.add(new ChessMove(myPosition, downLeft, null));
            }
            downLeftColumn--;
        }
        return bishopMoves;
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor){
        Collection<ChessMove> kingMoves = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            ChessPosition currPosition = null;
            ChessPiece currPiece = null;
            if(i==1){
                //check bottom left
                currPosition = new ChessPosition(myPosition.getRow() -1, myPosition.getColumn()-1);
                currPiece = board.getPiece(currPosition);
            }
            if(i==2){
                //check bottom
                currPosition = new ChessPosition(myPosition.getRow() -1, myPosition.getColumn());
                currPiece = board.getPiece(currPosition);
            }
            if(i==3){
                //check bottom right
                currPosition = new ChessPosition(myPosition.getRow() -1, myPosition.getColumn()+1);
                currPiece = board.getPiece(currPosition);
            }
            if(i==4){
                //check left
                currPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1);
                currPiece = board.getPiece(currPosition);
            }
            if(i==5){
                //check right
                currPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1);
                currPiece = board.getPiece(currPosition);
            }
            if(i==6){
                //check top left
                currPosition = new ChessPosition(myPosition.getRow() +1, myPosition.getColumn()-1);
                currPiece = board.getPiece(currPosition);
            }
            if(i==7){
                //check top
                currPosition = new ChessPosition(myPosition.getRow() +1, myPosition.getColumn());
                currPiece = board.getPiece(currPosition);
            }
            if(i==8){
                //check top right
                currPosition = new ChessPosition(myPosition.getRow() +1, myPosition.getColumn()+1);
                currPiece = board.getPiece(currPosition);
            }
            if (currPiece != null && currPiece.getTeamColor() != teamColor){
                kingMoves.add(new ChessMove(myPosition, currPosition, null));
                continue;
            } else if (currPiece != null && currPiece.getTeamColor() == teamColor) {
                continue;
            }
            if(currPosition.getColumn() != -111 && currPosition.getRow() != -111){
                kingMoves.add(new ChessMove(myPosition, currPosition, null));
            }
        }
        return kingMoves;
    }
}
