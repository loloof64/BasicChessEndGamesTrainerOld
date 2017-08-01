package com.loloof64.android.basicchessendgamestrainer.chess_abstraction

interface IEngine {
    fun evaluate(positionFen: String)
    fun setUciObserver(observer: SimpleUciObserver)
}

interface SimpleUciObserver {
    fun consumeMove(move: IMove)
    fun consumeScore(score: Int)
}