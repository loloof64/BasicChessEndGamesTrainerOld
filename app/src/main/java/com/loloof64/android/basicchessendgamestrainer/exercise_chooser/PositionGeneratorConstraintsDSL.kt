package com.loloof64.android.basicchessendgamestrainer.exercise_chooser.generatordsl

open class PositionConstraint {
    private val children: MutableList<PositionConstraint> = mutableListOf()

    protected fun <T: PositionConstraint> doInit(child: T, init: T.() -> Unit) {
        child.init()
        children += child
    }
}

fun positionGenerator(init: Generator.() -> Unit) = Generator().apply(init)

@Suppress("UNUSED")
class Generator : PositionConstraint() {
    fun kingsIndividualConstraints(constraint: KingsIndividualConstraint.()->Unit){

    }


    fun kingsMutualConstraint(constraint: KingsMutualConstraint.() -> Unit){

    }

    fun otherPiecesCount(constraint: OtherPiecesCountConstraint.() -> Unit) {

    }

    fun otherPiecesGlobalConstraint(constraint: OtherPiecesGlobalConstraint.()->Unit) {

    }

    fun otherPiecesMutualConstraint(constraint: OtherPiecesMutualConstraint.() -> Unit) {

    }

    fun otherPiecesIndexedConstraint(constraint: OtherPiecesIndexedConstraint.() -> Unit) {

    }
}

@Suppress("UNUSED")
class KingsIndividualConstraint : PositionConstraint() {
    fun playerKing(constraint: SingleKingConstraint.() -> Boolean){

    }

    fun computerKing(constraint: SingleKingConstraint.() -> Boolean){

    }
}

@Suppress("UNUSED")
class KingsMutualConstraint(val playerKingCoordinate: BoardCoordinate,
                            val oppositeKingCoordinate: BoardCoordinate,
                            val playerHasWhite: Boolean) : PositionConstraint()

@Suppress("UNUSED")
class OtherPiecesCountConstraint : PositionConstraint()

@Suppress("UNUSED")
class OtherPiecesGlobalConstraint(val coord: BoardCoordinate,
                                  val playerHasWhite: Boolean,
                                  val playerKingCoord : BoardCoordinate,
                                  val oppositeKingCoord: BoardCoordinate) : PositionConstraint()

class OtherPiecesMutualConstraint : PositionConstraint()

class OtherPiecesIndexedConstraint : PositionConstraint()

@Suppress("UNUSED")
class SingleKingConstraint(val location: BoardCoordinate, val playerHasWhite: Boolean) : PositionConstraint()

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

val exercice_1 = positionGenerator {
    kingsIndividualConstraints {
        computerKing {
            location.file in BoardCoordinate.FILE_C..BoardCoordinate.FILE_F
            && location.rank in BoardCoordinate.RANK_3..BoardCoordinate.RANK_6
        }
    }

    kingsMutualConstraint {
        playerKingCoordinate.file - oppositeKingCoordinate.file == 2
    }

    otherPiecesCount {
        PieceType.rook belongingTo Side.player inCount 2
        PieceType.bishop belongingTo Side.computer inCount 1
    }

    otherPiecesGlobalConstraint {

    }

    otherPiecesIndexedConstraint {

    }

    otherPiecesMutualConstraint {

    }
}