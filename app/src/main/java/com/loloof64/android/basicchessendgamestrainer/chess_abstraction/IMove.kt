package com.loloof64.android.basicchessendgamestrainer.chess_abstraction

interface IMove {
    fun getFrom(): Square
    fun getTo(): Square
    fun getPromotionPieceType(): PieceType?

    companion object {
        fun replacePiecesByFigurineInSan(san: String): String {
            return san.replace("N", "♘").replace("B", "♗").replace("R", "♖").replace("Q", "♕").replace("K", "♔")
        }
    }
}