package com.loloof64.android.basicchessendgamestrainer.exercise_chooser

import com.loloof64.android.basicchessendgamestrainer.R

val KRRvK_PositionGenerator = positionGenerator {
    kingsIndividualConstraints {
        computerKing {
            location.file in BoardCoordinate.FILE_C..BoardCoordinate.FILE_F
            && location.rank in BoardCoordinate.RANK_3..BoardCoordinate.RANK_6
        }
    }

    otherPiecesCount {
        add(PieceType.rook belongingTo Side.player inCount 2)
        add(PieceType.pawn belongingTo Side.player inCount 3)
    }
}

val KQvK_PositionGenerator = positionGenerator {
    kingsIndividualConstraints {
        computerKing {
            location.file in BoardCoordinate.FILE_C..BoardCoordinate.FILE_F
                    && location.rank in BoardCoordinate.RANK_3..BoardCoordinate.RANK_6
        }
    }

    otherPiecesCount {
        add(PieceType.queen belongingTo Side.player inCount 1)
    }
}

val KRvK_PositionGenerator = positionGenerator {
    kingsIndividualConstraints {
        computerKing {
            location.file in BoardCoordinate.FILE_C..BoardCoordinate.FILE_F
                    && location.rank in BoardCoordinate.RANK_3..BoardCoordinate.RANK_6
        }
    }

    otherPiecesCount {
        add(PieceType.rook belongingTo Side.player inCount 1)
    }
}

val KBBvK_PositionGenerator = positionGenerator {
    kingsIndividualConstraints {
        computerKing {
            location.file in BoardCoordinate.FILE_C..BoardCoordinate.FILE_F
                    && location.rank in BoardCoordinate.RANK_3..BoardCoordinate.RANK_6
        }
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

val KPvK_I_PositionGenerator = positionGenerator {
    kingsIndividualConstraints {
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
}

val KPvK_II_PositionGenerator = positionGenerator {
    kingsIndividualConstraints {
        playerKing {
            location.rank == if (playerHasWhite) BoardCoordinate.RANK_1 else BoardCoordinate.RANK_8
        }

        computerKing {
            location.rank == if (playerHasWhite) BoardCoordinate.RANK_4 else BoardCoordinate.RANK_5
        }
    }

    kingsMutualConstraint {
        Math.abs(playerKingCoordinate.file - computerKingCoordinate.file) <= 1
    }

    otherPiecesCount {
        add(PieceType.pawn belongingTo Side.player inCount 1)
    }

    otherPiecesGlobalConstraint {
        add(pieceKind = PieceType.pawn belongingTo Side.computer) {
            (location.rank == if (playerHasWhite) BoardCoordinate.RANK_5 else BoardCoordinate.RANK_4)
            && (location.file == playerKingLocation.file)
        }
    }
}

val availableGenerators = arrayOf(
        R.string.exercise_krr_k to KRRvK_PositionGenerator,
        R.string.exercise_kq_k to KQvK_PositionGenerator,
        R.string.exercise_kr_k to KRvK_PositionGenerator,
        R.string.exercise_kbb_k to KBBvK_PositionGenerator,
        R.string.exercise_kp_k_I to KPvK_I_PositionGenerator,
        R.string.exercise_kp_k_II to KPvK_II_PositionGenerator
)