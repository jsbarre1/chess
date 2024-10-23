package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessGameFunctions {

    public boolean isDiagonalPawnMove(ChessMove move){
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        if(endPosition.getRow() - startPosition.getRow() == 1 && endPosition.getColumn() -startPosition.getColumn() == -1){
            return true;
        }
        if(endPosition.getRow() - startPosition.getRow() == 1 && endPosition.getColumn() -startPosition.getColumn() == 1) {
            return true;
        }
        if(endPosition.getRow() - startPosition.getRow() == -1 && endPosition.getColumn() -startPosition.getColumn() == -1){
            return true;
        }
        return endPosition.getRow() - startPosition.getRow() == -1 && endPosition.getColumn() - startPosition.getColumn() == 1;
    }

    public boolean checkMoveCastle(ChessMove move){
        if(move.getStartPosition().getColumn() - move.getEndPosition().getColumn() ==2){
            return true;
        }else {return move.getEndPosition().getColumn() - move.getStartPosition().getColumn() == 2;}
    }

    public boolean isInCheck(ChessGame.TeamColor teamColor, ChessBoard liveBoard) {
        ChessPosition kingPosition = null;
        Collection<ChessMove> opposingMoves = new ArrayList<>();
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition currPosition = new ChessPosition(r, c);
                ChessPiece currPiece = liveBoard.getPiece(currPosition);
                if (currPiece == null) {
                    continue;
                }
                if (currPiece.getTeamColor() == teamColor && currPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = currPosition;
                }
                if (currPiece.getTeamColor() != teamColor) {
                    opposingMoves.addAll(currPiece.pieceMoves(liveBoard, currPosition));
                }
            }
        }

        Collection<ChessPosition> opposingEndPositionMoves = new ArrayList<>();
        for (ChessMove move : opposingMoves) {
            opposingEndPositionMoves.add(move.getEndPosition());
        }
        return opposingEndPositionMoves.contains(kingPosition);
    }

}
