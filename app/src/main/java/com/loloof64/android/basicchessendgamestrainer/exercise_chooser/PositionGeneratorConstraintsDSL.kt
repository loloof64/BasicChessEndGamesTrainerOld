package com.loloof64.android.basicchessendgamestrainer.exercise_chooser.generatordsl

open class PositionConstraint {
    private val children: MutableList<PositionConstraint> = mutableListOf()

    protected fun <T: PositionConstraint> doInit(child: T, init: T.() -> Unit) {
        child.init()
        children += child
    }
}

fun positionGenerator(init: Generator.() -> Unit) = Generator().apply(init)

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

class KingsIndividualConstraint : PositionConstraint() {
    fun playerKing(constraint: SingleKingConstraint.() -> Boolean){

    }

    fun computerKing(constraint: SingleKingConstraint.() -> Boolean){

    }
}

class KingsMutualConstraint(val playerKingCoordinate: BoardCoordinate,
                            val oppositeKingCoordinate: BoardCoordinate,
                            val playerHasWhite: Boolean) : PositionConstraint()

class OtherPiecesCountConstraint : PositionConstraint()

class OtherPiecesGlobalConstraint(val pieceKind: PieceKind,
                                  val constraint: GlobalConstraint.() -> Boolean) : PositionConstraint()
class GlobalConstraint(val location: BoardCoordinate,
                       val playerHasWhite: Boolean,
                       val playerKingCoord : BoardCoordinate,
                       val oppositeKingCoord: BoardCoordinate) : PositionConstraint()

class OtherPiecesMutualConstraint(val firstPieceLocation: BoardCoordinate,
                                  val secondPieceLocation: BoardCoordinate,
                                  val playerHasWhite: Boolean) : PositionConstraint()

class OtherPiecesIndexedConstraint(val pieceKind: PieceKind, val constraint: IndexedConstraint.() -> Boolean) : PositionConstraint()

class IndexedConstraint(val apparitionIndex: Int, val location: BoardCoordinate,
                        val playerHasWhite: Boolean) : PositionConstraint()

class SingleKingConstraint(val location: BoardCoordinate, val playerHasWhite: Boolean) : PositionConstraint()

enum class PieceType {
    pawn, knight, bishop, rook, queen, king
}

enum class Side {
    player, computer
}

data class PieceKind(val pieceType: PieceType, val side: Side)
data class PieceKindCount(val pieceKind: PieceKind, val count: Int)

data class BoardCoordinate(val file: Int, val rank: Int) {
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

infix fun PieceType.belongingTo(owner: Side) = PieceKind(pieceType = this, side = owner)

infix fun PieceKind.inCount(instances: Int) = PieceKindCount(pieceKind = this, count = instances)

infix fun PieceKind.constrainedBy(constraint: GlobalConstraint.() -> Boolean) =
        OtherPiecesGlobalConstraint(pieceKind = this, constraint = constraint)

infix fun PieceKind.constrainedByIndex(constraint: IndexedConstraint.() -> Boolean) =
        OtherPiecesIndexedConstraint(pieceKind = this, constraint = constraint)


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
        PieceType.pawn belongingTo Side.computer constrainedBy {
            (location.rank == if (playerHasWhite) BoardCoordinate.RANK_5 else BoardCoordinate.RANK_4)
                    && (location.file == playerKingCoord.file)
        }
    }

    otherPiecesIndexedConstraint {
        PieceType.pawn belongingTo  Side.player constrainedByIndex {
            location.file == apparitionIndex && location.rank == when(apparitionIndex){
                in 0..3 -> apparitionIndex + 1
                in 4..6 -> 7 - apparitionIndex
                in 7..8 -> BoardCoordinate.RANK_2
                else -> throw IllegalArgumentException()
            }
        }
    }

    otherPiecesMutualConstraint {
        val firstSquareIsBlack = (firstPieceLocation.file + firstPieceLocation.rank) % 2 > 0
        val secondSquareIsBlack = (secondPieceLocation.file + secondPieceLocation.rank) % 2 > 0
        firstSquareIsBlack != secondSquareIsBlack
    }
}