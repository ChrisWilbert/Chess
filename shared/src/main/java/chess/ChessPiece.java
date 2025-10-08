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
    private ChessGame.TeamColor pieceColor;
    private PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
       Collection<ChessMove> allMoves = new ArrayList<>();
        ChessGame.TeamColor myColor = this.getTeamColor();
        switch (this.type) {
            case ROOK:
            case BISHOP:
            case QUEEN:
                int[][] directions;
                if (type == PieceType.ROOK) {
                    directions = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
                }
                else if (type == PieceType.BISHOP) {
                   directions = new int[][] {{1,1}, {1,-1}, {-1,1}, {-1,-1}};
                } else {
                    directions = new int[][]{{1, 1}, {1, 0}, {1, -1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}
                    };
                }
                for (int[] dir : directions) {
                    int r = myPosition.getRow() + dir[0];
                    int c = myPosition.getColumn()+ dir[1];
                    while(r >= 1 && c >= 1 && r <= 8 && c <= 8) {
                        ChessPosition newPosition = new ChessPosition(r, c);
                        ChessPiece target = board.getPiece(newPosition);

                        if (target == null) {
                            allMoves.add(new ChessMove(myPosition,newPosition, null));
                        }
                        else  {
                            if (target.getTeamColor() != myColor) {
                                allMoves.add(new ChessMove(myPosition,newPosition, null));
                            }
                            break;
                        }
                        r += dir[0];
                        c += dir[1];
                    }
                }

            case PieceType.KNIGHT:
                int[][] knightMoves = {{2,1}, {1,2}, {-1,2}, {1,-2}, {-2,1}, {-2,-1},{-1,-2},{2,-1}
                };
                for (int[] move : knightMoves) {
                    int r = myPosition.getRow() + move[0];
                    int c = myPosition.getColumn()+ move[1];
                    if (r >= 1 && c >= 1 && r <= 8 && c <= 8) {
                        ChessPosition newPosition = new ChessPosition(r, c);
                        ChessPiece target = board.getPiece(newPosition);
                        if (target == null || target.getTeamColor() != myColor) {
                            allMoves.add(new ChessMove(myPosition,newPosition, null));
                        }
                    }
                }
                break;

            case PieceType.KING:
                int[][] kingMoves = {{1,1},{1,0}, {1,-1},{0,1}, {-1,1},{-1,0} ,{-1,-1}, {0,-1}
                };
                for (int[] move : kingMoves) {
                    int r = myPosition.getRow() + move[0];
                    int c = myPosition.getColumn()+ move[1];
                    if (r >= 1 && c >= 1 && r <= 8 && c <= 8) {
                        ChessPosition newPosition = new ChessPosition(r, c);
                        ChessPiece target = board.getPiece(newPosition);
                        if (target == null || target.getTeamColor() != myColor) {
                            allMoves.add(new ChessMove(myPosition,newPosition, null));
                        }
                    }
                }
                break;
            case PieceType.PAWN:
                int pawnMoves = (myColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
                int startRow = (myColor == ChessGame.TeamColor.WHITE )? 2 : 7;
                ChessPosition forwardOne = new ChessPosition(myPosition.getRow()+ pawnMoves, myPosition.getColumn());
                if (myPosition.getRow()+ pawnMoves >= 1 && myPosition.getRow()+ pawnMoves <= 8 && board.getPiece(forwardOne) == null ) {
                    if (myPosition.getRow()+ pawnMoves == 8 || myPosition.getRow()+ pawnMoves == 1) {
                        addPawnPromotions(allMoves, myPosition, forwardOne);
                    } else{
                        allMoves.add(new ChessMove(myPosition,forwardOne, null));
                    }
                    if(myPosition.getRow() == startRow){
                        ChessPosition forwardTwo =  new ChessPosition(myPosition.getRow() + 2 * pawnMoves, myPosition.getColumn());
                        if(board.getPiece(forwardTwo) == null){
                            allMoves.add(new ChessMove(myPosition,forwardTwo, null));
                        }
                    }

                }
                int[] captureMoves = {-1,1};
                for (int move : captureMoves) {
                    int c =   myPosition.getColumn() + move;
                    int r = myPosition.getRow() + pawnMoves;
                    if (r >= 1 && c >= 1 && r <= 8 && c <= 8) {
                        ChessPosition capturePosition = new ChessPosition(r, c);
                        ChessPiece target = board.getPiece(capturePosition);
                        if (target == null && target.getTeamColor() != myColor) {
                            if (r == 8 || r == 1) {
                                addPawnPromotions(allMoves, myPosition, capturePosition);
                            }
                            else {
                                allMoves.add(new ChessMove(myPosition,capturePosition, null));
                            }
                        }
                    }
                }
                break;
        }
        return allMoves;
    }
    void addPawnPromotions(Collection<ChessMove>allMoves, ChessPosition start, ChessPosition end){
        allMoves.add(new ChessMove(start,end,PieceType.QUEEN));
        allMoves.add(new ChessMove(start,end,PieceType.ROOK));
        allMoves.add(new ChessMove(start,end,PieceType.BISHOP));
        allMoves.add(new ChessMove(start,end,PieceType.KNIGHT));
    }
}
