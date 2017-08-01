package com.loloof64.android.basicchessendgamestrainer.chess_abstraction

interface IGame {
    fun getCurrentPosition() : IPosition
    fun doMove(move: IMove)
    fun resetFromFen(fen: String)

    fun isMate(): Boolean
    fun isDrawByStaleMate() : Boolean
    fun isDrawBy50MovesRule() : Boolean
    fun isDrawByThreeFoldsRepetition() : Boolean
    fun isDrawByMissingMatingMaterial() : Boolean
}