package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

@Suppress("UNUSED")
fun positionGenerator(init: PositionConstraints.() -> Unit) = PositionConstraints().apply(init)

@Suppress("UNUSED")
class PositionConstraints {
    private var playerKingIndividualConstraintInstance: SingleKingConstraint.() -> Boolean = { true }
    private var computerKingIndividualConstraintInstance: SingleKingConstraint.() -> Boolean = { true }
    private var kingsMutualConstraintInstance: KingsMutualConstraint.() -> Boolean = { true }
    private var otherPiecesCountConstraintInstance = OtherPiecesCountConstraints()
    private var otherPiecesGlobalConstraintInstance = OtherPiecesGlobalConstraint()
    private var otherPiecesMutualConstraintInstance = OtherPiecesMutualConstraint()
    private var otherPiecesIndexedConstraintInstance = OtherPiecesIndexedConstraint()

    fun checkPlayerKingConstraint(kingLocation: BoardCoordinate, playerHasWhite: Boolean): Boolean {
        return SingleKingConstraint(kingLocation, playerHasWhite).playerKingIndividualConstraintInstance()
    }

    fun checkComputerKingConstraint(kingLocation: BoardCoordinate, playerHasWhite: Boolean) : Boolean {
        return SingleKingConstraint(kingLocation, playerHasWhite).computerKingIndividualConstraintInstance()
    }

    fun checkKingsMutualConstraint(playerKingLocation: BoardCoordinate, computerKingLocation: BoardCoordinate, playerHasWhite: Boolean) : Boolean {
        return KingsMutualConstraint(playerKingLocation, computerKingLocation, playerHasWhite).kingsMutualConstraintInstance()
    }

    val otherPiecesCountsConstraint : Iterator<PieceKindCount>
        get() = otherPiecesCountConstraintInstance.iterator()

    fun checkOtherPieceGlobalConstraint(pieceKind: PieceKind,
                                        location: BoardCoordinate,
                                        playerHasWhite: Boolean,
                                        playerKingLocation : BoardCoordinate,
                                        computerKingLocation: BoardCoordinate) : Boolean {
        return otherPiecesGlobalConstraintInstance(location, playerHasWhite, playerKingLocation, computerKingLocation).checkConstraint(pieceKind)
    }

    fun checkOtherPieceMutualConstraint(pieceKind: PieceKind,
                                        firstPieceLocation: BoardCoordinate,
                                        secondPieceLocation: BoardCoordinate,
                                        playerHasWhite: Boolean) : Boolean {
        return otherPiecesMutualConstraintInstance(firstPieceLocation, secondPieceLocation, playerHasWhite).checkConstraint(pieceKind)
    }

    fun checkOtherPieceIndexedConstraint(pieceKind: PieceKind,
                                         apparitionIndex: Int,
                                         location: BoardCoordinate,
                                         playerHasWhite: Boolean) : Boolean {
        return otherPiecesIndexedConstraintInstance(apparitionIndex, location, playerHasWhite).checkConstraint(pieceKind)
    }

    fun playerKing(constraint: SingleKingConstraint.() -> Boolean) {
        playerKingIndividualConstraintInstance = constraint
    }

    fun computerKing(constraint: SingleKingConstraint.() -> Boolean) {
        computerKingIndividualConstraintInstance = constraint
    }


    fun kingsMutualConstraint(constraint: KingsMutualConstraint.() -> Boolean){
        kingsMutualConstraintInstance = constraint
    }

    fun otherPiecesCount(constraint: OtherPiecesCountConstraints.() -> Unit) {
        otherPiecesCountConstraintInstance.constraint()
    }

    fun otherPiecesGlobalConstraint(constraint: OtherPiecesGlobalConstraint.() -> Unit) {
        otherPiecesGlobalConstraintInstance.constraint()
    }

    fun otherPiecesMutualConstraint(constraint: OtherPiecesMutualConstraint.() -> Unit) {
        otherPiecesMutualConstraintInstance.constraint()
    }

    fun otherPiecesIndexedConstraint(constraint: OtherPiecesIndexedConstraint.() -> Unit) {
        otherPiecesIndexedConstraintInstance.constraint()
    }
}


/**
 * Constraint between both kings
 */
@Suppress("UNUSED")
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
@Suppress("UNUSED")
class OtherPiecesGlobalConstraint {
    private var allConstraints: MutableMap<PieceKind, OtherPiecesGlobalConstraint.() -> Boolean> = mutableMapOf()

    var location: BoardCoordinate = BoardCoordinate(file = 0, rank = 0)
        private set

    var playerHasWhite: Boolean = true
        private set

    var playerKingLocation: BoardCoordinate = BoardCoordinate(file = 0, rank = 0)
        private set

    var computerKingLocation : BoardCoordinate = BoardCoordinate(file = 0, rank = 0)

    operator fun invoke(location: BoardCoordinate,
                        playerHasWhite: Boolean,
                        playerKingLocation : BoardCoordinate,
                        computerKingLocation: BoardCoordinate) : OtherPiecesGlobalConstraint {
        this.location = location
        this.playerHasWhite = playerHasWhite
        this.playerKingLocation = playerKingLocation
        this.computerKingLocation = computerKingLocation

        return this
    }

    fun add(pieceKind: PieceKind, constraint: OtherPiecesGlobalConstraint.() -> Boolean){
        allConstraints.put(pieceKind, constraint)
    }

    fun checkConstraint(pieceKind: PieceKind) : Boolean {
        // If no constraint available then condition is considered as met.
        return allConstraints[pieceKind]?.invoke(this) ?: true
    }
}

/*
 * Constraint based on 2 pieces of the same kind.
 */
@Suppress("UNUSED")
class OtherPiecesMutualConstraint {
    private var allConstraints: MutableMap<PieceKind, OtherPiecesMutualConstraint.() -> Boolean> = mutableMapOf()

    var firstPieceLocation: BoardCoordinate = BoardCoordinate(file = 0, rank = 0)
        private set

    var secondPieceLocation: BoardCoordinate = BoardCoordinate(file = 0, rank = 0)
        private set

    var playerHasWhite : Boolean = true
        private set

    fun add(pieceKind: PieceKind, constraint: OtherPiecesMutualConstraint.() -> Boolean){
        allConstraints.put(pieceKind, constraint)
    }

    operator fun invoke(firstPieceLocation: BoardCoordinate,
               secondPieceLocation: BoardCoordinate,
               playerHasWhite: Boolean) : OtherPiecesMutualConstraint {
        this.firstPieceLocation = firstPieceLocation
        this.secondPieceLocation = secondPieceLocation
        this.playerHasWhite = playerHasWhite
        return this
    }

    fun checkConstraint(pieceKind: PieceKind): Boolean {
        // If no constraint available then condition is considered as met.
        return allConstraints[pieceKind]?.invoke(this) ?: true
    }
}
/**
 * Constraint based on the piece kind, its generation index (is it the first, the second, ... ?)
 */
@Suppress("UNUSED")
class OtherPiecesIndexedConstraint {

    private var allConstraints: MutableMap<PieceKind, OtherPiecesIndexedConstraint.() -> Boolean> = mutableMapOf()

    var apparitionIndex:Int = 0
        private set

    var location: BoardCoordinate = BoardCoordinate(file = 0, rank = 0)
        private set

    var playerHasWhite: Boolean = true
        private set

    fun add(pieceKind: PieceKind, constraint: OtherPiecesIndexedConstraint.() -> Boolean){
        allConstraints.put(pieceKind, constraint)
    }

    operator fun invoke(apparitionIndex: Int,
                        location: BoardCoordinate,
                        playerHasWhite: Boolean) : OtherPiecesIndexedConstraint {
        this.apparitionIndex = apparitionIndex
        this.location = location
        this.playerHasWhite = playerHasWhite
        return this
    }

    fun checkConstraint(pieceKind: PieceKind): Boolean {
        // If no constraint available then condition is considered as met
        return allConstraints[pieceKind]?.invoke(this) ?: true
    }
}


@Suppress("UNUSED")
class IndexedConstraint(val apparitionIndex: Int,
                        val location: BoardCoordinate,
                        val playerHasWhite: Boolean)

/*
 Individual king global constraint
 */
data class SingleKingConstraint(val location: BoardCoordinate,
                                val playerHasWhite: Boolean)

@Suppress("UNUSED")
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

@Suppress("UNUSED")
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

@Suppress("UNUSED")
infix fun PieceType.belongingTo(owner: Side) = PieceKind(pieceType = this, side = owner)

@Suppress("UNUSED")
infix fun PieceKind.inCount(instances: Int) = PieceKindCount(pieceKind = this, count = instances)