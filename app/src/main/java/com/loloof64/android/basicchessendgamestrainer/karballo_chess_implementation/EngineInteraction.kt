package com.loloof64.android.basicchessendgamestrainer.karballo_chess_implementation

import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.IMove
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.IEngine
import com.loloof64.android.basicchessendgamestrainer.chess_abstraction.SimpleUciObserver

import karballo.search.SearchObserver
import karballo.search.SearchStatusInfo
import karballo.search.SearchEngine
import karballo.search.SearchParameters
import karballo.Config

class EngineInteraction : IEngine, SearchObserver {

    init {
        engine.setObserver(this)
    }

    override fun bestMove(bestMove: Int, ponder: Int) {
        observer.consumeMove(Move.getMoveFromIndex(bestMove))
    }

    override fun info(info: SearchStatusInfo) {
        observer.consumeScore(info.score)
    }

    override fun evaluate(positionFen: String) {
        engine.init()
        engine.board.fen = positionFen
        engine.go(searchParameters)
    }

    override fun setUciObserver(observer: SimpleUciObserver) {
        this.observer = observer
    }

    private lateinit var observer: SimpleUciObserver


    companion object {
        private val config = Config()
        val engine = SearchEngine(config)
        val searchParameters = SearchParameters()

        init {
            config.ponder = false
            config.transpositionTableSize = 1
            searchParameters.isPonder = false
            searchParameters.depth = 5
            searchParameters.isInfinite = false
        }
    }
}