package com.loloof64.android.basicchessendgamestrainer.karballo_chess_implementation

import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.IGame
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.IPosition
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.IMove

import karballo.Board as KBoard

class Game(startPositionFen: String = IPosition.STANDARD_POSITION_FEN) : IGame {
    private var internalBoard = KBoard()

    init {
        internalBoard.fen = startPositionFen
    }

    override fun getCurrentPosition() : IPosition {
        return Position(internalBoard.fen)
    }

    override fun doMove(move: IMove) {
        val matchingMoves = Position.getMatchingIntMovesFrom(move, internalBoard)
        if (matchingMoves.isEmpty()) throw RuntimeException("Illegal move")

        internalBoard.doMove(matchingMoves[0])
    }

    override fun resetFromFen(fen: String) {
        internalBoard = KBoard()
        internalBoard.fen = fen
    }

    override fun isMate(): Boolean {
        return internalBoard.isMate
    }

    override fun isDrawByStaleMate() : Boolean {
        return internalBoard.isDrawByStaleMate
    }

    override fun isDrawBy50MovesRule() : Boolean {
        return internalBoard.isDrawByFiftyMovesRule
    }

    override fun isDrawByThreeFoldsRepetition() : Boolean {
        return internalBoard.isDrawByThreeFoldsRepetition
    }

    override fun isDrawByMissingMatingMaterial() : Boolean {
        return internalBoard.isDrawByMissingMatingMaterial
    }
}