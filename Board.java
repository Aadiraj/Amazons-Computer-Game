package amazons;

import java.util.Collections;
import java.util.Iterator;


import static amazons.Piece.*;



/**
 * The state of an Amazons Game.
 *
 * @author Aadiraj Batlaw
 */
class Board {

    /**
     * The number of squares on a side of the board.
     */
    static final int SIZE = 10;


    /**
     * Initializes a game board with SIZE squares on a side in the
     * initial position.
     */
    Board() {
        init();
    }

    /**
     * Initializes a copy of MODEL.
     */
    Board(Board model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Board model) {
        _pieces = new Piece[model._pieces.length][model._pieces.length];
        for (int i = 0; i < _pieces.length; i++) {
            for (int j = 0; j < _pieces.length; j++) {
                _pieces[i][j] = model._pieces[i][j];
            }
        }
        _turn = model._turn;
        _numMoves = model._numMoves;
        _size = model._size;
        _winner = model._winner;
        _turn = model._turn;

    }

    /**
     * Clears the board to the initial position.
     */
    void init() {

        _size = SIZE;
        _turn = WHITE;
        _winner = EMPTY;
        _pieces = new Piece[_size][_size];
        for (int row = 0; row < _pieces.length; row++) {
            for (int col = 0; col < _pieces[0].length; col++) {
                _pieces[row][col] = EMPTY;
            }
        }
        _pieces[0][3] = WHITE;
        _pieces[0][6] = WHITE;
        _pieces[3][0] = WHITE;
        _pieces[3][9] = WHITE;
        _pieces[6][0] = BLACK;
        _pieces[6][9] = BLACK;
        _pieces[9][3] = BLACK;
        _pieces[9][6] = BLACK;

        _spears = new boolean[_size][_size];

        _squares = new Square[_size * _size];
    }

    /**
     * Return the Piece whose move it is (WHITE or BLACK).
     */
    Piece turn() {
        return _turn;
    }

    /**
     * Return the number of moves (that have not been undone) for this
     * board.
     */
    int numMoves() {
        return _numMoves;
    }

    /**
     * Return the winner in the current position, or null if the game is
     * not yet finished.
     */
    Piece winner() {
        if (_winner != EMPTY) {

            return _winner;
        }
        return null;
    }

    /**
     * Return the contents the square at S.
     */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW <= 9.
     */
    final Piece get(int col, int row) {
        return _pieces[row][col];
    }

    /**
     * Return the contents of the square at COL ROW.
     */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /**
     * Set square S to P.
     */
    final void put(Piece p, Square s) {
        put(p, s.col(), s.row());
    }

    /**
     * Set square (COL, ROW) to P.
     */
    final void put(Piece p, int col, int row) {

        _pieces[row][col] = p;
    }

    /**
     * Set square COL ROW to P.
     */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /**
     * Return true iff FROM - TO is an unblocked queen move on the current
     * board, ignoring the contents of ASEMPTY, if it is encountered.
     * For this to be true, FROM-TO must be a queen move and the
     * squares along it, other than FROM and ASEMPTY, must be
     * empty. ASEMPTY may be null, in which case it has no effect.
     */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        int distance = from.distance(from, to);
        for (int i = 1; i < distance + 1; i++) {
            if (from.queenMove(from.direction(to), i) == null ||
                    (get(from.queenMove(from.direction(to), i)) != EMPTY
                            && from.queenMove(from.direction(to), i) != asEmpty)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true iff FROM is a valid starting square for a move.
     */
    boolean isLegal(Square from) {
        if (get(from) != null) {
            return true;
        }
        return false;
    }

    /**
     * Return true iff FROM-TO is a valid first part of move, ignoring
     * spear throwing.
     */
    boolean isLegal(Square from, Square to) {
        if (isLegal(from) && isUnblockedMove(from, to, from)) {
            return true;
        }
        return false;
    }


    /**
     * Return true iff FROM-TO(SPEAR) is a legal move in the current
     * position.
     */
    boolean isLegal(Square from, Square to, Square spear) {
        if (isLegal(from, to) && isUnblockedMove(to, spear, to)) {
            return true;
        }
        return false;

    }

    /**
     * Return true iff MOVE is a legal move in the current
     * position.
     */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /**
     * Move FROM-TO(SPEAR), assuming this is a legal move.
     */
    void makeMove(Square from, Square to, Square spear) {
        Move.mv(from, to, spear);
        put(get(from), to);
        put(SPEAR, spear);
        put(EMPTY, from);
        _numMoves++;

        if (!legalMoves(_turn.opponent()).hasNext()) {
            _winner = _turn;
        }
        _turn = _turn.opponent();


    }

    /**
     * Move according to MOVE, assuming it is a legal move.
     */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());

    }

    /**
     * Undo one move.  Has no effect on the initial board.
     */
    void undo() {
        Move[][][] moves = Move.getMoves();
        for (int i = moves.length - 1; i >= 0; i--) {
            if (moves[0][0][i] != null) {
                put(null, moves[0][0][i].spear());
                put(get(moves[0][0][i].to()), moves[0][0][i].from());
                moves[0][0][i] = null;
                break;
            }
        }
        _turn = _turn.opponent();


    }

    /**
     * Return an Iterator over the Squares that are reachable by an
     * unblocked queen move from FROM. Does not pay attention to what
     * piece (if any) is on FROM, nor to whether the game is finished.
     * Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     * feature is useful when looking for Moves, because after moving a
     * piece, one wants to treat the Square it came from as empty for
     * purposes of spear throwing.)
     */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /**
     * Return an Iterator over all legal moves on the current board.
     */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /**
     * Return an Iterator over all legal moves on the current board for
     * SIDE (regardless of whose turn it is).
     */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /**
     * An iterator used by reachableFrom.
     */
    private class ReachableFromIterator implements Iterator<Square> {

        /**
         * Iterator of all squares reachable by queen move from FROM,
         * treating ASEMPTY as empty.
         */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = 0;
            _steps = 0;
            _asEmpty = asEmpty;
            _last = false;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
               /* toNext();
                if (_last) {
                    Square temp = _from.queenMove(_dir, _steps);
                    _dir++;
                    return temp;
                }
                    return _from.queenMove(_dir, _steps);
                    */
            Square temp = _from.queenMove(_dir, _steps);
            toNext();
            return temp;
        }

        /**
         * Advance _dir and _steps, so that the next valid Square is
         * _steps steps in direction _dir from _from.
         */
        private void toNext() {
            while (hasNext()) {
                   /* if (_dir == 7 && isUnblockedMove(_from, _from.queenMove(_dir, _steps + 1), _asEmpty) && !isUnblockedMove(_from, _from.queenMove(_dir, _steps + 2), _asEmpty)) {
                        _steps++;
                        _last = true;
                        break;
                        }
                        */
                if (_from.queenMove(_dir, _steps + 1) == null) {
                    _steps = 0;
                    _dir++;
                } else if (isUnblockedMove(_from, _from.queenMove(_dir, _steps + 1), _asEmpty)) {
                    _steps++;
                    break;
                } else {
                    _steps = 0;
                    _dir++;
                }

            }
        }

        /**
         * Starting square.
         */
        private Square _from;
        /**
         * Current direction.
         */
        private int _dir;
        /**
         * Current distance.
         */
        private int _steps;
        /**
         * Square treated as empty.
         */
        private Square _asEmpty;

        private boolean _last;
    }

    /**
     * An iterator used by legalMoves.
     */
    private class LegalMoveIterator implements Iterator<Move> {

        /**
         * All legal moves for SIDE (WHITE or BLACK).
         */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _spearThrows.hasNext();
        }

        @Override
        public Move next() {
            Move nextMove = Move.mv(_start, _nextSquare, _spearThrows.next());
            toNext();
            return nextMove;
        }

        /**
         * Advance so that the next valid Move is
         * _start-_nextSquare(sp), where sp is the next value of
         * _spearThrows.
         */
        private void toNext() {
            if (!_spearThrows.hasNext()) {
                if (_pieceMoves.hasNext()) {
                    _nextSquare = _pieceMoves.next();
                    _spearThrows = new ReachableFromIterator(_nextSquare, _start);
                } else {
                    if (_startingSquares.hasNext()) {
                        _start = _startingSquares.next();
                        while (!get(_start).equals(_fromPiece) && _startingSquares.hasNext()) {
                            _start = _startingSquares.next();
                        }
                        if (get(_start).equals(_fromPiece)) {
                            _pieceMoves = new ReachableFromIterator(_start, null);
                            _nextSquare = _pieceMoves.next();
                            _spearThrows = new ReachableFromIterator(_nextSquare, _start);
                        }
                    }
                }
            }


        }

        /**
         * Color of side whose moves we are iterating.
         */
        private Piece _fromPiece;
        /**
         * Current starting square.
         */
        private Square _start;
        /**
         * Remaining starting squares to consider.
         */
        private Iterator<Square> _startingSquares;
        /**
         * Current piece's new position.
         */
        private Square _nextSquare;
        /**
         * Remaining moves from _start to consider.
         */
        private Iterator<Square> _pieceMoves;
        /**
         * Remaining spear throws from _piece to consider.
         */
        private Iterator<Square> _spearThrows;

    }

    @Override
    public String toString() {
        String result = "  ";
        for (int row = _pieces.length - 1; row >= 0; row--) {
            for (int col = 0; col < _pieces[0].length; col++) {
                result += " " + _pieces[row][col].toString();
            }
            if (row == 0) {
                result += "\n";
            } else {
                result += "\n" + "  ";
            }
        }
        return result;
    }

    /**
     * An empty iterator for initialization.
     */
    private static final Iterator<Square> NO_SQUARES =
            Collections.emptyIterator();

    /**
     * Piece whose turn it is (BLACK or WHITE).
     */
    private Piece _turn;
    /**
     * Cached value of winner on this board, or EMPTY if it has not been
     * computed.
     */
    private Piece _winner;

    private int _size;

    private int _numMoves;

    private Piece[][] _pieces;

    private boolean[][] _spears;

    private Square[] _squares;
}
