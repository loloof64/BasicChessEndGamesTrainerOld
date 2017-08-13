package com.loloof64.android.basicchessendgamestrainer.playing_activity

import karballo.Config
import karballo.search.SearchEngine
import karballo.search.SearchObserver
import karballo.search.SearchParameters
import karballo.search.SearchStatusInfo

class EngineInteraction(val observer: SimpleUciObserver) : SearchObserver {

    init {
        engine.setObserver(this)
    }

    override fun bestMove(bestMove: Int, ponder: Int) {
        observer.consumeMove(bestMove)
    }

    override fun info(info: SearchStatusInfo) {
        observer.consumeScore(info.score)
    }

    fun evaluate(positionFen: String) {
        engine.init()
        engine.board.fen = positionFen
        engine.go(searchParameters)
    }


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

interface SimpleUciObserver {
    fun consumeMove(move: Int)
    fun consumeScore(score: Int)
}