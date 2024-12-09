package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Stack;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurnColor;
    private ChessBoard liveBoard;
    private Stack<ChessMove> completedMoves;
    private ChessGameFunctions chessGameFunction;
    private Boolean isGameOver = false;

    public ChessGame() {
        teamTurnColor = TeamColor.WHITE;
        liveBoard = new ChessBoard();
        liveBoard.resetBoard();
        completedMoves = new Stack<>();
        this.chessGameFunction = new ChessGameFunctions();
    }
    public void setGameOver(boolean gameOver) {
        this.isGameOver = gameOver;
    }

    public boolean isGameOver() {
        return isGameOver;
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
    private void changeTeamColor() {
        if (teamTurnColor == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
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
        if (liveBoard.getPiece(startPosition) == null) {
            return finalMoves;
        }
        Collection<ChessMove> initialMoves = liveBoard.getPiece(startPosition).pieceMoves(liveBoard, startPosition);
            for (ChessMove move : initialMoves) {
                if (validateMove(move, liveBoard.getPiece(startPosition))) {
                    finalMoves.add(move);
                }
        }
        if (!isInCheck(liveBoard.getPiece(startPosition).getTeamColor())){
            for(ChessMove move: castleMoves(startPosition)){
                if(validateCastleMove(move, liveBoard.getPiece(startPosition))){
                    finalMoves.add(move);
                }
            }
        }
        if(liveBoard.getPiece(startPosition).getPieceType() == ChessPiece.PieceType.PAWN){
            if(completedMoves.empty()){
                return finalMoves;
            }
            ChessMove lastMove = completedMoves.peek();
            if(liveBoard.getPiece(lastMove.getEndPosition()).getPieceType() == ChessPiece.PieceType.PAWN){
                if(opposingPawnMovedTwice(completedMoves.peek())){
                   finalMoves.addAll(peekFunction(startPosition, lastMove));
                }
            }
        }
        return finalMoves;
    }

    private Collection<ChessMove> peekFunction(ChessPosition startPosition, ChessMove lastMove){
        Collection<ChessMove> finalMoves = new ArrayList<>();
        if(opposingPawnOnLeft(startPosition, lastMove.getEndPosition())){
            if(liveBoard.getPiece(startPosition).getTeamColor() == TeamColor.WHITE){
                finalMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow() +1,
                        startPosition.getColumn() -1), null));
            }
            if(liveBoard.getPiece(startPosition).getTeamColor() == TeamColor.BLACK){
                finalMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow() -1,
                        startPosition.getColumn() -1), null));
            }
        }
        if(opposingPawnOnRight(startPosition, lastMove.getEndPosition())){
            if(liveBoard.getPiece(startPosition).getTeamColor() == TeamColor.WHITE){
                finalMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow() +1,
                        startPosition.getColumn() +1), null));
            }
            if(liveBoard.getPiece(startPosition).getTeamColor() == TeamColor.BLACK){
                finalMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow() -1,
                        startPosition.getColumn() +1), null));
            }
        }
        return finalMoves;
    }

    private Boolean opposingPawnOnLeft (ChessPosition currentPosition, ChessPosition opposingPawnPosition){
        return opposingPawnPosition.getRow() == currentPosition.getRow() && opposingPawnPosition.getColumn() == currentPosition.getColumn() - 1;
    }

    private Boolean opposingPawnOnRight (ChessPosition currentPosition, ChessPosition opposingPawnPosition){
        return opposingPawnPosition.getRow() == currentPosition.getRow() && opposingPawnPosition.getColumn() == currentPosition.getColumn() + 1;
    }

    private Boolean validateMove(ChessMove move, ChessPiece targetPiece) {
        boolean inCheck = isInCheck(targetPiece.getTeamColor());
        if (inCheck) {
            return removesCheck(move, targetPiece);
        }
        ChessPiece[][] currentBoard = liveBoard.getBoard();
        ChessPiece[][] boardCopy = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(currentBoard[i], 0, boardCopy[i], 0, 8);
        }
        ChessBoard futureBoard = new ChessBoard(boardCopy);
        futureBoard.removePiece(move.getStartPosition());
        futureBoard.addPiece(move.getEndPosition(), targetPiece);
        return !isInCheckInFuture(targetPiece.getTeamColor(), futureBoard);
    }

    private Boolean validateCastleMove(ChessMove move, ChessPiece targetPiece) {
        //set up future board
        ChessPiece[][] currentBoard = liveBoard.getBoard();
        ChessPiece[][] boardCopy = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(currentBoard[i], 0, boardCopy[i], 0, 8);
        }
        ChessBoard futureBoard = new ChessBoard(boardCopy);

        futureBoard.castle(move);

        if (isInCheckInFuture(targetPiece.getTeamColor(), futureBoard)) {
            return false;
        }
        return !rookInDangerInFuture(move, targetPiece.getTeamColor(), futureBoard);
    }

    private boolean rookInDangerInFuture(ChessMove move, TeamColor teamColor, ChessBoard futureBoard) {
        ChessPosition rookPosition = null;
        Collection<ChessMove> opposingMoves = new ArrayList<>();

        if(move.getEndPosition().getColumn() == 7){
            rookPosition = new ChessPosition(move.getEndPosition().getRow(), 6);
        }
        if(move.getEndPosition().getColumn() == 3){
            rookPosition = new ChessPosition(move.getEndPosition().getRow(), 4);
        }

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition currPosition = new ChessPosition(r, c);
                ChessPiece currPiece = futureBoard.getPiece(currPosition);
                if (currPiece == null) {
                    continue;
                }
                if (currPiece.getTeamColor() != teamColor) {
                    opposingMoves.addAll(currPiece.pieceMoves(futureBoard, currPosition));
                }
            }
        }

        Collection<ChessPosition> opposingEndPositionMoves = new ArrayList<>();


        for (ChessMove m : opposingMoves) {
            opposingEndPositionMoves.add(m.getEndPosition());
        }

        return opposingEndPositionMoves.contains(rookPosition);
    }

    private boolean checkHasMoved(ChessPosition currentPosition){
        Collection<ChessPosition> startPositions = new ArrayList<>();
        for(ChessMove move : completedMoves){
            startPositions.add(move.getStartPosition());
        }
        return startPositions.contains(currentPosition);
    }

    private Collection<ChessMove> castleMoves(ChessPosition currentPosition){
        Collection<ChessMove> castleMoves = new ArrayList<>();
        ChessPiece currPiece = liveBoard.getPiece(currentPosition);
        if (currPiece.getPieceType() != ChessPiece.PieceType.KING){
            return  castleMoves;
        }
       if(checkHasMoved(currentPosition)){
           return castleMoves;
       }
       if(currPiece.getTeamColor() == TeamColor.WHITE){
           //check if queenside rook has moved and is there
           if(liveBoard.getPiece(new ChessPosition(1,1)) != null && !checkHasMoved(new ChessPosition(1,1))) {
               //check if there is no one in between
               for (int c = 4; c >=1; c--) {
                   ChessPiece piece = liveBoard.getPiece(new ChessPosition(1, c));
                    if(piece != null && piece.getPieceType() != ChessPiece.PieceType.ROOK){
                        break;
                    }
                    if(c == 1){
                        castleMoves.add(new ChessMove(currentPosition, new ChessPosition(1,3), null));
                    }
               }
           }
           //check if kingside rook has moved and is there
           if(liveBoard.getPiece(new ChessPosition(1,8)) != null && !checkHasMoved(new ChessPosition(1,8))){
               //check if there is no one in between
               for (int c = 6; c <= 8; c++) {
                   ChessPiece piece = liveBoard.getPiece(new ChessPosition(1, c));
                   if(piece != null && piece.getPieceType() != ChessPiece.PieceType.ROOK){
                       break;
                   }
                   if(c == 8){
                       castleMoves.add(new ChessMove(currentPosition, new ChessPosition(1,7), null));
                   }
               }
           }
       }

        if(currPiece.getTeamColor() == TeamColor.BLACK){
            //check if queenside rook has moved and is there
            if(liveBoard.getPiece(new ChessPosition(8,1)) != null && !checkHasMoved(new ChessPosition(8,1))){
                //check if there is no one in between
                for (int c = 4; c >= 1; c--) {
                    ChessPiece piece = liveBoard.getPiece(new ChessPosition(8, c));
                    if(piece != null && piece.getPieceType() != ChessPiece.PieceType.ROOK){
                        break;
                    }
                    if(c == 1){
                        castleMoves.add(new ChessMove(currentPosition, new ChessPosition(8,3), null));
                    }
                }
            }
            //check if kingside rook has moved and is there
            if(liveBoard.getPiece(new ChessPosition(8,8)) != null && !checkHasMoved(new ChessPosition(8,8))){
                //check if there is no one in between
                for (int c = 6; c <= 8; c++) {
                    ChessPiece piece = liveBoard.getPiece(new ChessPosition(8, c));
                    if(piece != null && piece.getPieceType() != ChessPiece.PieceType.ROOK){
                        break;
                    }
                    if(c == 8){
                        castleMoves.add(new ChessMove(currentPosition, new ChessPosition(8,7), null));
                    }
                }
            }
        }

        return  castleMoves;
    }

    public void addCompletedMove(ChessMove move){
        completedMoves.add(move);
    }

    private boolean removesCheck(ChessMove move, ChessPiece piece){
        //ChessBoard futureBoard = new ChessBoard(liveBoard.getBoard());
        ChessPiece[][] currentBoard = liveBoard.getBoard();
        ChessPiece[][] boardCopy = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(currentBoard[i], 0, boardCopy[i], 0, 8);
        }
        ChessBoard futureBoard = new ChessBoard(boardCopy);

        futureBoard.removePiece(move.getStartPosition());
        futureBoard.addPiece(move.getEndPosition(), piece);

        return !isInCheckInFuture(piece.getTeamColor(), futureBoard);
    }

    private boolean isInCheckInFuture(TeamColor teamColor, ChessBoard futureBoard) {
        ChessPosition kingPosition = null;
        Collection<ChessMove> opposingMoves = new ArrayList<>();

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition currPosition = new ChessPosition(r, c);
                ChessPiece currPiece = futureBoard.getPiece(currPosition);
                if (currPiece == null) {
                    continue;
                }
                if (currPiece.getTeamColor() == teamColor && currPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = currPosition;
                }
                if (currPiece.getTeamColor() != teamColor) {
                    opposingMoves.addAll(currPiece.pieceMoves(futureBoard, currPosition));
                }
            }
        }

        Collection<ChessPosition> opposingEndPositionMoves = new ArrayList<>();


        for (ChessMove move : opposingMoves) {
            opposingEndPositionMoves.add(move.getEndPosition());
        }

        return opposingEndPositionMoves.contains(kingPosition);
    }

    private boolean checkMoveCastle(ChessMove move){
        return chessGameFunction.checkMoveCastle(move);
    }

    private boolean isDiagonalPawnMove(ChessMove move){
        return chessGameFunction.isDiagonalPawnMove(move);
    }

    private Boolean opposingPawnMovedTwice (ChessMove opposingPawnPosition){
        if(opposingPawnPosition.getStartPosition().getRow() - opposingPawnPosition.getEndPosition().getRow() == 2){
            return true;
        }
        return opposingPawnPosition.getEndPosition().getRow() - opposingPawnPosition.getStartPosition().getRow() == 2;
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */

    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves.contains(move)) {
            ChessPiece targetPiece = liveBoard.getPiece(move.getStartPosition());
            if(targetPiece.getTeamColor() != teamTurnColor){
                throw new InvalidMoveException("NOT YOUR TURN");
            }
            ChessPiece opponentPiece = liveBoard.getPiece(move.getEndPosition());
            if(targetPiece.getPieceType() == ChessPiece.PieceType.KING){
                if (checkMoveCastle(move)){
                    liveBoard.castle(move);
                    addCompletedMove(move);
                    if(move.getEndPosition().getColumn() == 7){
                        addCompletedMove(new ChessMove(new ChessPosition(move.getEndPosition().getRow(), 8),
                                new ChessPosition(move.getEndPosition().getRow(), 6), null));
                    }
                    if(move.getEndPosition().getColumn() == 3){
                        addCompletedMove(new ChessMove(new ChessPosition(move.getEndPosition().getRow(), 1),
                                new ChessPosition(move.getEndPosition().getRow(), 4), null));
                    }
                    changeTeamColor();
                    return;
                }
            }

            if(targetPiece.getPieceType() == ChessPiece.PieceType.PAWN && !completedMoves.empty()){
                //check if enPassantMove
                if(isDiagonalPawnMove(move) && liveBoard.getPiece(move.getEndPosition()) == null){
                    //make move
                    liveBoard.enPassant(move, completedMoves.peek());
                    addCompletedMove(move);
                    changeTeamColor();
                    return;
                }
            }

            if(opponentPiece != null){
                liveBoard.removePiece(move.getEndPosition());
            }
            liveBoard.movePiece(move);
            addCompletedMove(move);
            changeTeamColor();
        } else {throw new InvalidMoveException("NOT VALID MOVE");}

    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    public boolean isInCheck(TeamColor teamColor) {
       return chessGameFunction.isInCheck(teamColor, liveBoard);
    }

    private Collection<ChessMove> allValidMovesEver(TeamColor teamColor){
        Collection<ChessMove> allMovesEver = new ArrayList<>();
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition currPosition = new ChessPosition(r, c);
                ChessPiece currPiece = liveBoard.getPiece(currPosition);
                if (currPiece == null) {
                    continue;
                }
                if (currPiece.getTeamColor() == teamColor) {
                    allMovesEver.addAll(validMoves(currPosition));
                }
            }
        }
        return allMovesEver;
    }

    private Boolean isDefaultBoard () {
        ChessBoard defaultBoard = new ChessBoard();
        defaultBoard.resetBoard();
        ChessPiece [][] currBoard = liveBoard.getBoard();
        ChessPiece [][] defaultBoardArray = defaultBoard.getBoard();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if(defaultBoardArray[r][c] == null && currBoard[r][c] == null){
                    continue;
                }
                if(defaultBoardArray[r][c] != null && currBoard[r][c] == null){
                    return false;
                }
                if(defaultBoardArray[r][c] == null && currBoard[r][c] != null){
                    return false;
                }
                if(defaultBoardArray[r][c].getPieceType() != currBoard[r][c].getPieceType()){
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */

    public boolean isInCheckmate(TeamColor teamColor) {
        if(isDefaultBoard()){ return false;}
        return isInCheck(teamColor) && allValidMovesEver(teamColor).isEmpty();
    }
    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */

    public boolean isInStalemate(TeamColor teamColor) {
        if(isDefaultBoard()) {return false;}
        return !isInCheck(teamColor) && allValidMovesEver(teamColor).isEmpty();
    }
    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */

    public void setBoard(ChessBoard board) {
        liveBoard = board;
        completedMoves = new Stack<>();
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
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()){ return false;}
        ChessGame chessGame = (ChessGame) o;
        return teamTurnColor == chessGame.teamTurnColor && Objects.equals(liveBoard, chessGame.liveBoard);
    }
    @Override
    public int hashCode() {
        return Objects.hash(teamTurnColor, liveBoard);
    }
}
