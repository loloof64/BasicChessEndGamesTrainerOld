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

import com.loloof64.android.basicchessendgamestrainer.R
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.FileB
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.FileC
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.FileF
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.FileG
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.Rank1
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.Rank2
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.Rank3
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.Rank4
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.Rank5
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.Rank6
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.Rank7
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.Rank8
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.Queen
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.Rook
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.Bishop
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.Knight
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.Pawn
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.Player
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.PositionConstraints.Companion.Computer

val KRRvK = positionGenerator {
    computerKing {
            file in FileC..FileF
            && rank in Rank3..Rank6
    }

    otherPiecesCount {
        add(Rook belongingTo Player inCount 2)
    }
}

val KQvK = positionGenerator {
    computerKing {
            file in FileC..FileF
            && rank in Rank3..Rank6
    }

    otherPiecesCount {
        add(Queen belongingTo Player inCount 1)
    }
}

val KRvK = positionGenerator {
    computerKing {
            file in FileC..FileF
            && rank in Rank3..Rank6
    }

    otherPiecesCount {
        add(Rook belongingTo Player inCount 1)
    }
}

val KBBvK = positionGenerator {
    computerKing {
            file in FileC..FileF
            && rank in Rank3..Rank6
    }

    otherPiecesCount {
        add(Bishop belongingTo Player inCount 2)
    }

    otherPiecesMutualConstraint {
        set(Bishop belongingTo Player) {
            val firstSquareIsBlack = (firstPieceFile + firstPieceRank) % 2 > 0
            val secondSquareIsBlack = (secondPieceFile + secondPieceRank) % 2 > 0
            firstSquareIsBlack != secondSquareIsBlack
        }
    }
}

val KPvK_I = positionGenerator {
        playerKing {
                rank == (if (playerHasWhite) Rank6 else Rank3)
                && file in FileB..FileG
        }

        computerKing {
                rank == if (playerHasWhite) Rank8 else Rank1
        }

        kingsMutualConstraint {
            playerKingFile == computerKingFile
        }

        otherPiecesCount {
            add(Pawn belongingTo Player inCount 1)
        }

        otherPiecesGlobalConstraint {
            set(Pawn belongingTo Player) {
                (rank == if (playerHasWhite) Rank5 else Rank4)
                && (file == playerKingFile)
            }
        }
}

val KPvK_II = positionGenerator {
    playerKing {
            rank == if (playerHasWhite) Rank1 else Rank8
    }

    computerKing {
            rank == if (playerHasWhite) Rank4 else Rank5
    }

    kingsMutualConstraint {
        Math.abs(playerKingFile - computerKingFile) <= 1
    }

    otherPiecesCount {
        add(Pawn belongingTo Computer inCount 1)
    }

    otherPiecesGlobalConstraint {
        set(Pawn belongingTo Computer) {
            (rank in if (playerHasWhite) Rank3..Rank5 else Rank4..Rank6)
            && (file == playerKingFile)
        }
    }
}

val KPPPvKPPP = positionGenerator {
    playerKing {
            rank == (if (playerHasWhite) Rank1 else Rank8)
    }

    computerKing {
            rank == (if (playerHasWhite) Rank1 else Rank8)
    }

    otherPiecesCount {
        add(Pawn belongingTo Player inCount 3)
        add(Pawn belongingTo Computer inCount 3)
    }

    otherPiecesGlobalConstraint {
        set(Pawn belongingTo Player){
            rank == if (playerHasWhite) Rank5 else Rank4
        }
        set(Pawn belongingTo Computer){
            rank == if (playerHasWhite) Rank7 else Rank2
        }
    }

    otherPiecesIndexedConstraint {
        set(Pawn belongingTo Player){
            file == apparitionIndex
        }
        set(Pawn belongingTo Computer){
            file == apparitionIndex
        }
    }
}

val KNBvK = positionGenerator {
    computerKing {
        file in FileC..FileF
        && rank in Rank3..Rank6
    }

    otherPiecesCount {
        add(Knight belongingTo Player inCount 1)
        add(Bishop belongingTo Player inCount 1)
    }
}

data class ExerciseInfo(val constraints: PositionConstraints, val textId: Int, val mustWin: Boolean)

val availableGenerators = listOf(
        ExerciseInfo(mustWin = true, textId = R.string.exercise_krr_k, constraints = KRRvK),
        ExerciseInfo(mustWin = true, textId = R.string.exercise_kq_k, constraints = KQvK),
        ExerciseInfo(mustWin = true, textId = R.string.exercise_kr_k, constraints =  KRvK),
        ExerciseInfo(mustWin = true, textId = R.string.exercise_kbb_k, constraints = KBBvK),
        ExerciseInfo(mustWin = true, textId = R.string.exercise_kp_k_I, constraints =  KPvK_I),
        ExerciseInfo(mustWin = false, textId = R.string.exercise_kp_k_II, constraints = KPvK_II),
        ExerciseInfo(mustWin = true, textId = R. string.exercise_kppp_kppp, constraints = KPPPvKPPP),
        ExerciseInfo(mustWin = true, textId = R.string.exercise_knb_k, constraints = KNBvK)
)