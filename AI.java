package amazons;

// NOTICE:
// This file is a SUGGESTED skeleton.  NOTHING here or in any other source
// file is sacred.  If any of it confuses you, throw it out and do it your way.

import java.util.Iterator;

import static java.lang.Math.*;

import static amazons.Piece.*;
import static amazons.Utils.iterable;

/**
 * A Player that automatically generates moves.
 *
 * @author Aadiraj Batlawmak
 */
class AI extends Player {

    /**
     * A position magnitude indicating a win (for white if positive, black
     * if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();

        Square from = Square.sq(move.from().col(), move.from().row() + 1);
        Square to = Square.sq(move.to().col(), move.to().row() + 1);
        Square spear = Square.sq(move.spear().col(), move.spear().row() + 1);
        move = Move.mv(from, to, spear);

        _controller.reportMove(move);
        return move.toString();
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = new Board(board());

        //_whiteMoves = b.legalMoves(WHITE); //I put this here
        // _blackMoves = b.legalMoves(BLACK); //I put this here
        //_legalMoves = _whiteMoves;         //I put this here
        //_maximizer = false;                  //I put this here

        if (_myPiece == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }
        Move bestSoFar = null;
        int bestScore = 0;


        // return staticScore(board);
        if (sense == 1) {
            _legalMoves = board.legalMoves(WHITE);
            bestScore = Integer.MIN_VALUE;
            while (_legalMoves.hasNext()) {
                Move possibleMove = _legalMoves.next();
                board.makeMove(possibleMove);
                int possibleBest = findMove(board, depth - 1, false, sense * -1, alpha, beta);
                board.undo();
                //  System.out.println("possibleBestMaximizer: " + possibleBest);
                if (possibleBest >= bestScore) {
                    bestSoFar = possibleMove;
                    bestScore = possibleBest;
                    alpha = max(alpha, possibleBest);
                    if (beta <= alpha) {
                        break;
                    }
                }
                // return bestScore;
            }
        }
        if (sense == -1) {
            _legalMoves = board.legalMoves(BLACK);
            bestScore = Integer.MAX_VALUE;
            //  System.out.println("minimizer");
            while (_legalMoves.hasNext()) {
                Move possibleMove = _legalMoves.next();
                board.makeMove(possibleMove);
                int possibleBest = findMove(board, depth - 1, false, sense * -1, alpha, beta);
                board.undo();
                // System.out.println("possibleBestMinimizer: " + possibleBest);
                if (possibleBest <= bestScore) {
                    //  System.out.println("entered");
                    bestSoFar = possibleMove;
                    bestScore = possibleBest;
                    //   System.out.println("bestSoFar: " + bestSoFar.toString());
                    beta = min(beta, possibleBest);
                    if (beta <= alpha) {
                        break;
                    }
                }
                // return bestScore;
            }
        }
        if (saveMove) {
            _lastFoundMove = bestSoFar;
        }
        return bestScore;
    }

    /**
     * Return a heuristically determined maximum search depth
     * based on characteristics of BOARD.
     */
    private int maxDepth(Board board) {
        int N = board.numMoves();
//        if (N < 5) {
//            return 1;
//        }
//        else if (N < 20) {
//            return 2;
//        }
//        else if (N < 40) {
//            return 3;
//        }
//        else if (N < 80) {
//            return 4;
//        }
//        else {
//            return 5;
//        }
        return 1;

    }


    /**
     * Return a heuristic value for BOARD.
     */
    private int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == BLACK) {
//            _controller.reportNote("Black Wins.");
            return -WINNING_VALUE;
        } else if (winner == WHITE) {
//            _controller.reportNote("White Wins.");
            return WINNING_VALUE;
        }


        int legalWhiteMoves = 0;
        _whiteMoves = board.legalMoves(WHITE);
        while (_whiteMoves.hasNext()) {
            _whiteMoves.next();
            legalWhiteMoves++;
        }
        //  System.out.println("num white moves" + legalWhiteMoves);
        int legalBlackMoves = 0;
        _blackMoves = board.legalMoves(BLACK);
        while (_blackMoves.hasNext()) {
            _blackMoves.next();
            legalBlackMoves++;
        }
        // System.out.println("num black moves:" + legalBlackMoves);
        return legalWhiteMoves - legalBlackMoves;
    }

    private Iterator<Move> _whiteMoves;

    private Iterator<Move> _blackMoves;

    private Iterator<Move> _legalMoves;

    private boolean _maximizer;
}
