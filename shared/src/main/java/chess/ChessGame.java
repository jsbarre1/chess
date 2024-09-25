package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurnColor;
    private ChessBoard liveBoard;

    public ChessGame() {
        teamTurnColor = TeamColor.WHITE;
        liveBoard = new ChessBoard();
        liveBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurnColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurnColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> finalMoves = new ArrayList<>();
        if(liveBoard.getPiece(startPosition) == null){
            return finalMoves;
        }
        Collection<ChessMove> initialMoves = liveBoard.getPiece(startPosition).pieceMoves(liveBoard, startPosition);

        for (ChessMove move : initialMoves) {
            if (validateMove(move, liveBoard.getPiece(startPosition))) {
                finalMoves.add(move);
            }
        }

        return finalMoves;
    }

    private void changeTeamColor() {
        if (teamTurnColor == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    private boolean checkFutureKingCheck (ChessMove move){
        return false;
    }

    private Boolean validateMove(ChessMove move, ChessPiece targetPiece) {

        if (targetPiece == null) {
            return false;
        }

        boolean inCheck = isInCheck(targetPiece.getTeamColor());
        if (inCheck && targetPiece.getPieceType() != ChessPiece.PieceType.KING) {
            return false;
        }
        if (targetPiece.getPieceType() == ChessPiece.PieceType.KING) {
            return kingValidMoves(teamTurnColor).contains(move);
        }
        return targetPiece.getTeamColor() == teamTurnColor;
        //if it is a valid normal move return true;
    }

    private Collection<ChessMove> kingValidMoves(TeamColor teamColor) {
        Collection<ChessMove> kingInitialMoves = new ArrayList<>();
        Collection<ChessMove> kingFinalMoves = new ArrayList<>();
        Collection<ChessMove> opposingMoves = new ArrayList<>();

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition currPosition = new ChessPosition(r, c);
                ChessPiece currPiece = liveBoard.getPiece(currPosition);
                if (currPiece == null) {
                    continue;
                }
                if (currPiece.getTeamColor() == teamColor && currPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingInitialMoves.addAll(currPiece.pieceMoves(liveBoard, currPosition));
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

        for (ChessMove move : kingInitialMoves) {
            if (!opposingEndPositionMoves.contains(move.getEndPosition())) {
                kingFinalMoves.add(move);
            }
        }

        return kingFinalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        if(validMoves(move.getStartPosition()).contains(move)){
            ChessPiece targetPiece = liveBoard.getPiece(move.getStartPosition());
            if (move.getPromotionPiece() != null) {
                liveBoard.addPiece(move.getEndPosition(), new ChessPiece(teamTurnColor, move.getPromotionPiece()));
            } else {
                liveBoard.addPiece(move.getEndPosition(), targetPiece);
            }
            liveBoard.removePiece(move.getStartPosition());
            changeTeamColor();
        }else throw new InvalidMoveException("NOT VALID MOVE");

//        ChessPiece targetPiece = liveBoard.getPiece(move.getStartPosition());
//        if (targetPiece == null) {
//            throw new InvalidMoveException("NO PIECE FOUND");
//        }
//        boolean inCheck = isInCheck(targetPiece.getTeamColor());
//        if (inCheck && targetPiece.getPieceType() != ChessPiece.PieceType.KING) {
//            throw new InvalidMoveException("MUST MOVE KING");
//        }
//
//
//        if (targetPiece.getPieceType() == ChessPiece.PieceType.KING) {
//            if (!kingValidMoves(teamTurnColor).contains(move)) {
//                throw new InvalidMoveException("PUTS KING IN CHECK :(");
//            } else {
//                liveBoard.addPiece(move.getEndPosition(), targetPiece);
//                liveBoard.removePiece(move.getStartPosition());
//                changeTeamColor();
//                return;
//            }
//        }
//
//        Collection<ChessMove> possibleMoves = validMoves(move.getStartPosition());
//
//        if (targetPiece.getTeamColor() != teamTurnColor) {
//            throw new InvalidMoveException("NOT YOUR TURN");
//        }
//        if (possibleMoves.contains(move)) {
//            if (move.getPromotionPiece() != null) {
//                liveBoard.addPiece(move.getEndPosition(), new ChessPiece(targetPiece.getTeamColor(), move.getPromotionPiece()));
//                liveBoard.removePiece(move.getStartPosition());
//                changeTeamColor();
//            } else {
//                liveBoard.addPiece(move.getEndPosition(), targetPiece);
//                liveBoard.removePiece(move.getStartPosition());
//                changeTeamColor();
//            }
//        } else {
//            throw new InvalidMoveException("Not In Valid Moves");
//        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
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

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        liveBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return liveBoard;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return teamTurnColor == chessGame.teamTurnColor && Objects.equals(liveBoard, chessGame.liveBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurnColor, liveBoard);
    }
}
