package com.loloof64.android.basicchessendgamestrainer.karballo_chess_implementation

import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.IPosition
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.IMove
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.Square
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.Piece
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.Color
import karballo.Board as KBoard
import karballo.Piece as KPiece
import karballo.Square as KSquare
import karballo.Move as KMove

fun squareToLong(coords: Square) : Long {
    return 1L shl ((7-coords.file) + (8*coords.rank))
}

fun longToSquare(squareIndex: Long) : Square {
    val powerOf2 = (Math.log(squareIndex.toDouble()) / Math.log(2.0)).toInt()
    return Square(7-(powerOf2%8), powerOf2/8)
}

class Position(positionFen: String = IPosition.STANDARD_POSITION_FEN) : IPosition {
    private val internalBoard = KBoard()

    init {
        internalBoard.fen = positionFen
    }

    override fun getPieceAtSquare(square: Square) : Piece? {
        val internalBoardSquare = squareToLong(square)
        val pieceFromInternalBoard = internalBoard.getPieceAt(internalBoardSquare)
        return Piece.fromFen(pieceFromInternalBoard)
    }

    override fun setPieceAtSquare(square: Square, piece: Piece?) {
        val internalBoardSquare = squareToLong(square)
        internalBoard.setPieceAt(internalBoardSquare, piece?.toFen() ?: '.')
    }
    
    override fun toFen() : String {
        return internalBoard.fen
    }

    override fun getPlayerTurn() : Color {
        return if (internalBoard.turn) Color.WHITE else Color.BLACK
    }

    override fun setPlayerTurn(playerToPlay: Color) {
        internalBoard.turn = playerToPlay == Color.WHITE
    }

    override fun canWhiteDoShortCastle(): Boolean {
        return internalBoard.whiteKingsideCastling
    }

    override fun canWhiteDoLongCastle(): Boolean {
        return internalBoard.whiteQueensideCastling
    }
    
    override fun canBlackDoShortCastle(): Boolean {
        return internalBoard.blackKingsideCastling
    }
    
    override fun canBlackDoLongCastle(): Boolean {
        return internalBoard.blackKingsideCastling
    }

    override fun setWhiteShortCastlePossibility(canDo: Boolean) {
        //missing for now
    }

    override fun setWhiteLongCastlePossibility(canDo: Boolean) {
        //missing for now
    }

    override fun setBlackShortCastlePossibility(canDo: Boolean) {
        //missing for now
    }

    override fun setBlackLongCastlePossibility(canDo: Boolean) {
        //missing for now
    }

    override fun getEnPassantSquare(): Square? {
        throw UnsupportedOperationException()
    }

    override fun setEnPassantSquare(square: Square?) {
        throw UnsupportedOperationException()
    }

    override fun getDrawHalfMovesCount(): Int {
        return internalBoard.fiftyMovesRule
    }

    override fun setDrawHalfMovesCount(count: Int) {
        internalBoard.fiftyMovesRule = count
    }

    override fun getMoveNumber(): Int {
        return internalBoard.moveNumber / 2
    }

    override fun setMoveNumber(move: Int) {
        internalBoard.moveNumber = 2*move + (if (internalBoard.turn) 1 else 0)
    }

    override fun kingInChess(side: Color): Boolean {
        val boardClone = KBoard()
        boardClone.fen = internalBoard.fen
        boardClone.turn = side == Color.WHITE
        boardClone.setCheckFlags()
        return boardClone.check
    }

    override fun isLegalMove(move: IMove): Boolean {
        return getMatchingIntMovesFrom(move, internalBoard).isNotEmpty()
    }

    override fun isPromotionMove(move: IMove): Boolean {
        val matchingMoves = getMatchingIntMovesFrom(move, internalBoard)
        if (matchingMoves.isEmpty()) throw RuntimeException("Illegal move")
        
        return KMove.isPromotion(matchingMoves[0])
    }

    override fun getMoveSAN(move: IMove): String {
        val matchingMoves = getMatchingIntMovesFrom(move, internalBoard)
        if (matchingMoves.isEmpty()) throw RuntimeException("Illegal move")

        return KMove.toSan(internalBoard, matchingMoves[0])
    }

    companion object {
        val MAX_LEGAL_POSITIONS_COUNT = 250

        fun getMatchingIntMovesFrom(move: IMove, karballoPosition: KBoard): Array<Int> {
            val legalMovesStore = IntArray(MAX_LEGAL_POSITIONS_COUNT)
            karballoPosition.getLegalMoves(legalMovesStore)
            return legalMovesStore.filter { currMatchingMoveInt ->
                val currMatchingMove = Move.getMoveFromIndex(currMatchingMoveInt)
                (move.getFrom() == currMatchingMove.getFrom()) &&
                (move.getTo() == currMatchingMove.getTo()) &&
                (move.getPromotionPieceType() == currMatchingMove.getPromotionPieceType())
            }.toTypedArray()
        }
    }
}