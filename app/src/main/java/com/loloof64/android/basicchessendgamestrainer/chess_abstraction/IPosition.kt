package com.loloof64.android.basicchessendgamestrainer.chess_abstraction

interface IPosition {
    fun getPieceAtSquare(square: Square) : Piece?
    fun setPieceAtSquare(square: Square, piece: Piece?)
    
    fun toFen() : String

    fun getPlayerTurn() : Color
    fun setPlayerTurn(playerToPlay: Color)

    fun canWhiteDoShortCastle(): Boolean
    fun canWhiteDoLongCastle(): Boolean
    fun canBlackDoShortCastle(): Boolean
    fun canBlackDoLongCastle(): Boolean

    fun setWhiteShortCastlePossibility(canDo: Boolean)
    fun setWhiteLongCastlePossibility(canDo: Boolean)
    fun setBlackShortCastlePossibility(canDo: Boolean)
    fun setBlackLongCastlePossibility(canDo: Boolean)

    fun getEnPassantSquare(): Square?
    fun setEnPassantSquare(square: Square?)

    fun getDrawHalfMovesCount(): Int
    fun setDrawHalfMovesCount(count: Int)

    fun getMoveNumber(): Int
    fun setMoveNumber(move: Int)

    fun kingInChess(side: Color): Boolean

    fun isLegalMove(move: IMove): Boolean

    fun isPromotionMove(move: IMove): Boolean

    fun getMoveSAN(move: IMove): String

    companion object {
        val STANDARD_POSITION_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    }
}