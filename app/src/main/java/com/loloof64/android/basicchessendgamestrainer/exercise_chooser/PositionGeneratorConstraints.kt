package com.loloof64.android.basicchessendgamestrainer.exercise_chooser.generatordsl

open class PositionConstraint {
    private val children: MutableList<PositionConstraint> = mutableListOf()

    protected fun <T: PositionConstraint> doInit(child: T, init: T.() -> Unit) {
        child.init()
        children += child
    }
}

class SingleKingConstraint : PositionConstraint() {

}

fun positionGenerator(init: PositionConstraint.() -> Unit) = Generator().apply(init)

class Generator : PositionConstraint() {
    fun kingsIndividualConstraints(init: KingsIndividualConstraint.()->Unit){

    }
}

@Suppress("UNUSED")
class KingsIndividualConstraint : PositionConstraint() {
    private var playerKingConstraint: (BoardCoordinate, Boolean) -> Boolean = { _, _ -> true }
    private var computerKingConstraint: (BoardCoordinate, Boolean) -> Boolean = { _, _ -> true }

    fun playerKing(constraint: (location: BoardCoordinate, playerHasWhite: Boolean) -> Boolean){
        playerKingConstraint = constraint
    }

    fun computerKing(constraint: (location: BoardCoordinate, playerHasWhite: Boolean) -> Boolean){
        computerKingConstraint = constraint
    }
}

enum class PieceType {
    pawn, knight, bishop, rook, queen, king
}

enum class Side {
    player, computer
}

@Suppress("UNUSED") data class PieceKind(val pieceType: PieceType, val side: Side)
@Suppress("UNUSED") data class PieceKindCount(val pieceKind: PieceKind, val count: Int)

@Suppress("UNUSED") data class BoardCoordinate(val file: Int, val rank: Int) {
    companion object {
        val FILE_A = 0
        val FILE_B = 1
        val FILE_C = 2
        val FILE_D = 3
        val FILE_E = 4
        val FILE_F = 5
        val FILE_G = 6
        val FILE_H = 7

        val RANK_1 = 0
        val RANK_2 = 1
        val RANK_3 = 2
        val RANK_4 = 3
        val RANK_5 = 4
        val RANK_6 = 5
        val RANK_7 = 6
        val RANK_8 = 7
    }
}

@Suppress("UNUSED") infix fun PieceType.belongingTo(owner: Side) = PieceKind(pieceType = this, side = owner)
@Suppress("UNUSED") infix fun PieceKind.inCount(instances: Int) = PieceKindCount(pieceKind = this, count = instances)