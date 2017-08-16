package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

import com.loloof64.android.basicchessendgamestrainer.R

val KRRvK = positionGenerator {
    computerKing {
            location.file in BoardCoordinate.FILE_C..BoardCoordinate.FILE_F
                    && location.rank in BoardCoordinate.RANK_3..BoardCoordinate.RANK_6
    }

    otherPiecesCount {
        add(PieceType.rook belongingTo Side.player inCount 2)
    }
}

val KQvK = positionGenerator {
    computerKing {
            location.file in BoardCoordinate.FILE_C..BoardCoordinate.FILE_F
                    && location.rank in BoardCoordinate.RANK_3..BoardCoordinate.RANK_6
    }

    otherPiecesCount {
        add(PieceType.queen belongingTo Side.player inCount 1)
    }
}

val KRvK = positionGenerator {
    computerKing {
            location.file in BoardCoordinate.FILE_C..BoardCoordinate.FILE_F
                    && location.rank in BoardCoordinate.RANK_3..BoardCoordinate.RANK_6
    }

    otherPiecesCount {
        add(PieceType.rook belongingTo Side.player inCount 1)
    }
}

val KBBvK = positionGenerator {
    computerKing {
            location.file in BoardCoordinate.FILE_C..BoardCoordinate.FILE_F
                    && location.rank in BoardCoordinate.RANK_3..BoardCoordinate.RANK_6
    }

    otherPiecesCount {
        add(PieceType.bishop belongingTo Side.player inCount 2)
    }

    otherPiecesMutualConstraint {
        add(pieceKind = PieceType.bishop belongingTo Side.player) {
            val firstSquareIsBlack = (firstPieceLocation.file + firstPieceLocation.rank) % 2 > 0
            val secondSquareIsBlack = (secondPieceLocation.file + secondPieceLocation.rank) % 2 > 0
            firstSquareIsBlack != secondSquareIsBlack
        }
    }
}

val KPvK_I = positionGenerator {
        playerKing {
                location.rank == (if (playerHasWhite) BoardCoordinate.RANK_6 else BoardCoordinate.RANK_3)
                        && location.file in BoardCoordinate.FILE_B..BoardCoordinate.FILE_G
        }

        computerKing {
                location.rank == if (playerHasWhite) BoardCoordinate.RANK_8 else BoardCoordinate.RANK_1
        }

        kingsMutualConstraint {
            playerKingCoordinate.file == computerKingCoordinate.file
        }

        otherPiecesCount {
            add(PieceType.pawn belongingTo Side.player inCount 1)
        }

        otherPiecesGlobalConstraint {
            add(pieceKind = PieceType.pawn belongingTo Side.player) {
                (location.rank == if (playerHasWhite) BoardCoordinate.RANK_5 else BoardCoordinate.RANK_4)
                && (location.file == playerKingLocation.file)
            }
        }
}

val KPvK_II = positionGenerator {
    playerKing {
            location.rank == if (playerHasWhite) BoardCoordinate.RANK_1 else BoardCoordinate.RANK_8
    }

    computerKing {
            location.rank == if (playerHasWhite) BoardCoordinate.RANK_4 else BoardCoordinate.RANK_5
    }

    kingsMutualConstraint {
        Math.abs(playerKingCoordinate.file - computerKingCoordinate.file) <= 1
    }

    otherPiecesCount {
        add(PieceType.pawn belongingTo Side.computer inCount 1)
    }

    otherPiecesGlobalConstraint {
        add(pieceKind = PieceType.pawn belongingTo Side.computer) {
            (location.rank in if (playerHasWhite) BoardCoordinate.RANK_3..BoardCoordinate.RANK_5 else BoardCoordinate.RANK_4..BoardCoordinate.RANK_6)
            && (location.file == playerKingLocation.file)
        }
    }
}

val KPPPvKPPP = positionGenerator {
    playerKing {
            location.rank == (if (playerHasWhite) BoardCoordinate.RANK_1 else BoardCoordinate.RANK_8)
    }

    computerKing {
            location.rank == (if (playerHasWhite) BoardCoordinate.RANK_1 else BoardCoordinate.RANK_8)
    }

    otherPiecesCount {
        add(PieceType.pawn belongingTo Side.player inCount 3)
        add(PieceType.pawn belongingTo Side.computer inCount 3)
    }

    otherPiecesGlobalConstraint {
        add(pieceKind = PieceType.pawn belongingTo Side.player){
            location.rank == if (playerHasWhite) BoardCoordinate.RANK_5 else BoardCoordinate.RANK_4
        }
        add(pieceKind = PieceType.pawn belongingTo Side.computer){
            location.rank == if (playerHasWhite) BoardCoordinate.RANK_7 else BoardCoordinate.RANK_2
        }
    }

    otherPiecesIndexedConstraint {
        add(pieceKind = PieceType.pawn belongingTo Side.player){
            location.file == apparitionIndex
        }
        add(pieceKind = PieceType.pawn belongingTo Side.computer){
            location.file == apparitionIndex
        }
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
        ExerciseInfo(mustWin = true, textId = R. string.exercise_kppp_kppp, constraints = KPPPvKPPP)
)