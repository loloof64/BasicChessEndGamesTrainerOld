package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

fun positionGenerator(init: PositionConstraints.() -> Unit) = PositionConstraints().apply(init)

class PositionConstraints {
    private var kingsIndividualConstraintInstance: KingsIndividualConstraint.() -> Unit = { true }
    private var kingsMutualConstraintInstance: KingsMutualConstraint.() -> Boolean = { true }
    private var otherPiecesCountConstraintInstance: OtherPiecesCountConstraints.() -> Unit = {}
    private var otherPiecesGlobalConstraintInstance: OtherPiecesGlobalConstraint.() -> Unit = {}
    private var otherPiecesMutualConstraintInstance: OtherPiecesMutualConstraint.() -> Unit = {}
    private var otherPiecesIndexedConstraintInstance: OtherPiecesIndexedConstraint.() -> Unit = {}

    fun checkPlayerKingConstraint(kingLocation: BoardCoordinate, playerHasWhite: Boolean): Boolean {
        return with(KingsIndividualConstraint()){
            this.checkPlayerKingConstraint(kingLocation, playerHasWhite)
        }
    }

    fun checkComputerKingConstraint(kingLocation: BoardCoordinate, playerHasWhite: Boolean) : Boolean {
        return with(KingsIndividualConstraint()){
            this.checkComputerKingConstraint(kingLocation, playerHasWhite)
        }
    }

    fun checkKingsMutualConstraint(playerKingLocation: BoardCoordinate, computerKingLocation: BoardCoordinate, playerHasWhite: Boolean) : Boolean {
        return KingsMutualConstraint(playerKingLocation, computerKingLocation, playerHasWhite).kingsMutualConstraintInstance()
    }

    val otherPiecesCountsConstraint : Iterator<PieceKindCount>
        get() = otherPiecesCountsConstraint.iterator()

    fun checkOtherPieceGlobalConstraint(pieceKind: PieceKind,
                                        location: BoardCoordinate,
                                        playerHasWhite: Boolean,
                                        playerKingLocation : BoardCoordinate,
                                        computerKingLocation: BoardCoordinate) : Boolean {
        return OtherPiecesGlobalConstraint(location, playerHasWhite, playerKingLocation, computerKingLocation).checkConstraint(pieceKind)
    }

    fun checkOtherPieceMutualConstraint(pieceKind: PieceKind,
                                        firstPieceLocation: BoardCoordinate,
                                        secondPieceLocation: BoardCoordinate,
                                        playerHasWhite: Boolean) : Boolean {
        return OtherPiecesMutualConstraint(firstPieceLocation, secondPieceLocation, playerHasWhite).checkConstraint(pieceKind)
    }

    fun checkOtherPieceIndexedConstraint(pieceKind: PieceKind,
                                         apparitionIndex: Int,
                                         location: BoardCoordinate,
                                         playerHasWhite: Boolean) : Boolean {
        return OtherPiecesIndexedConstraint(apparitionIndex, location, playerHasWhite).checkConstraint(pieceKind)
    }

    fun kingsIndividualConstraints(constraint: KingsIndividualConstraint.() -> Unit){
        kingsIndividualConstraintInstance = constraint
    }


    fun kingsMutualConstraint(constraint: KingsMutualConstraint.() -> Boolean){
        kingsMutualConstraintInstance = constraint
    }

    fun otherPiecesCount(constraint: OtherPiecesCountConstraints.() -> Unit) {
        otherPiecesCountConstraintInstance = constraint
    }

    fun otherPiecesGlobalConstraint(constraint: OtherPiecesGlobalConstraint.() -> Unit) {
        otherPiecesGlobalConstraintInstance = constraint
    }

    fun otherPiecesMutualConstraint(constraint: OtherPiecesMutualConstraint.() -> Unit) {
        otherPiecesMutualConstraintInstance = constraint
    }

    fun otherPiecesIndexedConstraint(constraint: OtherPiecesIndexedConstraint.() -> Unit) {
        otherPiecesIndexedConstraintInstance = constraint
    }
}

/*
 * Constraint based on the both kings coordinates
 */
class KingsIndividualConstraint {
    private var playerKingConstraint: SingleKingConstraint.() -> Boolean = { true }
    private var computerKingConstraint: SingleKingConstraint.() -> Boolean = { true }

    fun playerKing(constraint: SingleKingConstraint.() -> Boolean){
        playerKingConstraint = constraint
    }

    fun computerKing(constraint: SingleKingConstraint.() -> Boolean){
        computerKingConstraint = constraint
    }

    fun checkPlayerKingConstraint(kingLocation: BoardCoordinate, playerHasWhite: Boolean) =
            SingleKingConstraint(kingLocation, playerHasWhite).playerKingConstraint()

    fun checkComputerKingConstraint(kingLocation: BoardCoordinate, playerHasWhite: Boolean) =
            SingleKingConstraint(kingLocation, playerHasWhite).computerKingConstraint()
}

/**
 * Constraint between both kings
 */
class KingsMutualConstraint(val playerKingCoordinate: BoardCoordinate,
                            val computerKingCoordinate: BoardCoordinate,
                            val playerHasWhite: Boolean)

/**
 * Constraint based on the count of several piece kinds (piece type and side : computer or player).
 */
class OtherPiecesCountConstraints {
    private val allConstraints: MutableList<PieceKindCount> = mutableListOf()

    fun add(constraint: PieceKindCount)  {
        allConstraints += constraint
    }

    operator fun iterator() = allConstraints.iterator()
}

/*
 * Constraint based on the piece coordinate, and both kings positions
 */
class OtherPiecesGlobalConstraint(val location: BoardCoordinate,
                                  val playerHasWhite: Boolean,
                                  val playerKingLocation : BoardCoordinate,
                                  val computerKingLocation: BoardCoordinate) {
    var allConstraints: MutableMap<PieceKind, OtherPiecesGlobalConstraint.() -> Boolean> = mutableMapOf()
        private set

    fun add(pieceKind: PieceKind, constraint: OtherPiecesGlobalConstraint.() -> Boolean){
        allConstraints.put(pieceKind, constraint)
    }

    fun checkConstraint(pieceKind: PieceKind) : Boolean {
        // If no constraint available then condition is considered as met.
        return allConstraints[pieceKind]?.invoke(this) == true
    }
}

/*
 * Constraint based on 2 pieces of the same kind.
 */
class OtherPiecesMutualConstraint(val firstPieceLocation: BoardCoordinate,
                                  val secondPieceLocation: BoardCoordinate,
                                  val playerHasWhite: Boolean) {
    private var allConstraints: MutableMap<PieceKind, OtherPiecesMutualConstraint.() -> Boolean> = mutableMapOf()
        private set

    fun add(pieceKind: PieceKind, constraint: OtherPiecesMutualConstraint.() -> Boolean){
        allConstraints.put(pieceKind, constraint)
    }

    fun checkConstraint(pieceKind: PieceKind): Boolean {
        // If no constraint available then condition is considered as met.
        return allConstraints[pieceKind]?.invoke(this) == true
    }
}
/**
 * Constraint based on the piece kind, its generation index (is it the first, the second, ... ?)
 */
class OtherPiecesIndexedConstraint(val apparitionIndex: Int,
                                   val location: BoardCoordinate,
                                   val playerHasWhite: Boolean) {

    private var allConstraints: MutableMap<PieceKind, OtherPiecesIndexedConstraint.() -> Boolean> = mutableMapOf()
        private set

    fun add(pieceKind: PieceKind, constraint: OtherPiecesIndexedConstraint.() -> Boolean){
        allConstraints.put(pieceKind, constraint)
    }

    fun checkConstraint(pieceKind: PieceKind): Boolean {
        // If no constraint available then condition is considered as met.
        return allConstraints[pieceKind]?.invoke(this) == true
    }
}


class IndexedConstraint(val apparitionIndex: Int,
                        val location: BoardCoordinate,
                        val playerHasWhite: Boolean)

class SingleKingConstraint(val location: BoardCoordinate,
                           val playerHasWhite: Boolean)

class MutualConstraint(val firstPieceLocation: BoardCoordinate,
                       val secondPieceLocation: BoardCoordinate,
                       val playerHasWhite: Boolean)

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