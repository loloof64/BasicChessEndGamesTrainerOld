/*
 * Basic Chess Endgames : generates a position of the endgame you want, then play it against computer.
    Copyright (C) 2017-2018  Laurent Bernabe <laurent.bernabe@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    val FileA = 0
    val FileB = 1
    val FileC = 2
    val FileD = 3
    val FileE = 4
    val FileF = 5
    val FileG = 6
    val FileH = 7

    val Rank1 = 0
    val Rank2 = 1
    val Rank3 = 2
    val Rank4 = 3
    val Rank5 = 4
    val Rank6 = 5
    val Rank7 = 6
    val Rank8 = 7

    val Pawn = PieceType.pawn
    val Knight = PieceType.knight
    val Bishop = PieceType.bishop
    val Rook = PieceType.rook
    val Queen = PieceType.queen
    val King = PieceType.king

    val Player = Side.player
    val Computer = Side.computer

    fun checkPlayerKingConstraint(file: Int, rank: Int, playerHasWhite: Boolean): Boolean {
        return SingleKingConstraint(file, rank, playerHasWhite).playerKingIndividualConstraintInstance()
    }

    fun checkComputerKingConstraint(file: Int, rank: Int, playerHasWhite: Boolean) : Boolean {
        return SingleKingConstraint(file, rank, playerHasWhite).computerKingIndividualConstraintInstance()
    }

    fun checkKingsMutualConstraint(playerKingFile: Int, playerKingRank: Int,
                                   computerKingFile: Int, computerKingRank: Int, playerHasWhite: Boolean) : Boolean {
        return KingsMutualConstraint(playerKingFile, playerKingRank, computerKingFile, computerKingRank, playerHasWhite).kingsMutualConstraintInstance()
    }

    val otherPiecesCountsConstraint : Iterator<PieceKindCount>
        get() = otherPiecesCountConstraintInstance.iterator()

    fun checkOtherPieceGlobalConstraint(pieceKind: PieceKind,
                                        file: Int, rank: Int,
                                        playerHasWhite: Boolean,
                                        playerKingFile : Int, playerKingRank: Int,
                                        computerKingFile: Int, computerKingRank: Int) : Boolean {
        return otherPiecesGlobalConstraintInstance(file, rank, playerHasWhite, playerKingFile, playerKingRank, computerKingFile, computerKingRank).checkConstraint(pieceKind)
    }

    fun checkOtherPieceMutualConstraint(pieceKind: PieceKind,
                                        firstPieceFile: Int, firstPieceRank: Int,
                                        secondPieceFile: Int, secondPieceRank: Int,
                                        playerHasWhite: Boolean) : Boolean {
        return otherPiecesMutualConstraintInstance(firstPieceFile, firstPieceRank, secondPieceFile, secondPieceRank, playerHasWhite).checkConstraint(pieceKind)
    }

    fun checkOtherPieceIndexedConstraint(pieceKind: PieceKind,
                                         apparitionIndex: Int,
                                         file: Int, rank: Int,
                                         playerHasWhite: Boolean) : Boolean {
        return otherPiecesIndexedConstraintInstance(apparitionIndex, file, rank, playerHasWhite).checkConstraint(pieceKind)
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
class KingsMutualConstraint(val playerKingFile: Int, val playerKingRank: Int,
                            val computerKingFile: Int, val computerKingRank: Int,
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

    var file:Int = 0
        private set

    var rank:Int = 0
        private set

    var playerHasWhite: Boolean = true
        private set

    var playerKingFile: Int = 0
        private set

    var playerKingRank: Int = 0
        private set

    var computerKingFile : Int = 0
        private set

    var computerKingRank : Int = 0
        private set

    operator fun invoke(file: Int, rank: Int,
                        playerHasWhite: Boolean,
                        playerKingFile: Int, playerKingRank: Int,
                        computerKingFile: Int, computerKingRank: Int) : OtherPiecesGlobalConstraint {
        this.file = file
        this.rank = rank
        this.playerHasWhite = playerHasWhite
        this.playerKingFile = playerKingFile
        this.playerKingRank = playerKingRank
        this.computerKingFile = computerKingFile
        this.computerKingRank = computerKingRank

        return this
    }

    fun set(pieceKind: PieceKind, constraint: OtherPiecesGlobalConstraint.() -> Boolean){
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

    var firstPieceFile: Int = 0
        private set

    var firstPieceRank: Int = 0
        private set

    var secondPieceFile: Int = 0
        private set

    var secondPieceRank: Int = 0
        private set

    var playerHasWhite : Boolean = true
        private set

    fun set(pieceKind: PieceKind, constraint: OtherPiecesMutualConstraint.() -> Boolean){
        allConstraints.put(pieceKind, constraint)
    }

    operator fun invoke(firstPieceFile: Int, firstPieceRank: Int,
               secondPieceFile: Int, secondPieceRank: Int,
               playerHasWhite: Boolean) : OtherPiecesMutualConstraint {
        this.firstPieceFile = firstPieceFile
        this.firstPieceRank = firstPieceRank
        this.secondPieceFile = secondPieceFile
        this.secondPieceRank = secondPieceRank
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

    var file:Int = 0
        private set

    var rank:Int = 0
        private set

    var playerHasWhite: Boolean = true
        private set

    fun set(pieceKind: PieceKind, constraint: OtherPiecesIndexedConstraint.() -> Boolean){
        allConstraints.put(pieceKind, constraint)
    }

    operator fun invoke(apparitionIndex: Int,
                        file: Int, rank: Int,
                        playerHasWhite: Boolean) : OtherPiecesIndexedConstraint {
        this.apparitionIndex = apparitionIndex
        this.file = file
        this.rank = rank
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
                        val file: Int, val rank: Int,
                        val playerHasWhite: Boolean)

/*
 Individual king global constraint
 */
data class SingleKingConstraint(val file: Int, val rank: Int,
                                val playerHasWhite: Boolean)

@Suppress("UNUSED")
class MutualConstraint(val firstPieceFile: Int, firstPieceRank: Int,
                       val secondPieceFile: Int, val secondPieceRank: Int,
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
infix fun PieceType.belongingTo(owner: Side) = PieceKind(pieceType = this, side = owner)

@Suppress("UNUSED")
infix fun PieceKind.inCount(instances: Int) = PieceKindCount(pieceKind = this, count = instances)