package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

abstract class PositionGenerator {
    abstract fun generatePosition(): String
}

class KRRvK_PositionGenerator: PositionGenerator() {
    override fun generatePosition(): String {
        return "8/8/8/5k2/8/1R6/R7/4K3 w - - 0 1"
    }
}

class KQvK_PositionGenerator: PositionGenerator() {
    override fun generatePosition(): String {
        return "8/8/8/4k3/8/8/2Q5/4K3 w - - 0 1"
    }
}

class KBBvK_PositionGenerator: PositionGenerator() {
    override fun generatePosition(): String {
        return "8/8/8/8/4k3/8/8/2B1KB2 w - - 0 1"
    }
}
