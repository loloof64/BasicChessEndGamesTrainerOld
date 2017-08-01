package com.loloof64.android.basicchessendgamestrainer.chess_abstraction

data class Piece(val color: Color, val pieceType: PieceType) {
    fun toFen() : Char {
        return if (color == Color.WHITE) pieceType.lowercaseFen.toUpperCase()
        else pieceType.lowercaseFen.toLowerCase()
    }

    companion object {
        fun fromFen(pieceFen: Char): Piece? {
            return when (pieceFen){
                'P' -> WHITE_PAWN
                'N' -> WHITE_KNIGHT
                'B' -> WHITE_BISHOP
                'R' -> WHITE_ROOK
                'Q' -> WHITE_QUEEN
                'K' -> WHITE_KING

                'p' -> BLACK_PAWN
                'n' -> BLACK_KNIGHT
                'b' -> BLACK_BISHOP
                'r' -> BLACK_ROOK
                'q' -> BLACK_QUEEN
                'k' -> BLACK_KING
                else -> null
            }
        }

        val WHITE_PAWN = Piece(color = Color.WHITE, pieceType = PieceType.PAWN)
        val WHITE_KNIGHT = Piece(color = Color.WHITE, pieceType = PieceType.KNIGHT)
        val WHITE_BISHOP = Piece(color = Color.WHITE, pieceType = PieceType.BISHOP)
        val WHITE_ROOK = Piece(color = Color.WHITE, pieceType = PieceType.ROOK)
        val WHITE_QUEEN = Piece(color = Color.WHITE, pieceType = PieceType.QUEEN)
        val WHITE_KING = Piece(color = Color.WHITE, pieceType = PieceType.KING)

        val BLACK_PAWN = Piece(color = Color.BLACK, pieceType = PieceType.PAWN)
        val BLACK_KNIGHT = Piece(color = Color.BLACK, pieceType = PieceType.KNIGHT)
        val BLACK_BISHOP = Piece(color = Color.BLACK, pieceType = PieceType.BISHOP)
        val BLACK_ROOK = Piece(color = Color.BLACK, pieceType = PieceType.ROOK)
        val BLACK_QUEEN = Piece(color = Color.BLACK, pieceType = PieceType.QUEEN)
        val BLACK_KING = Piece(color = Color.BLACK, pieceType = PieceType.KING)
    }
}

enum class Color {
    WHITE,
    BLACK
}

enum class PieceType(val lowercaseFen: Char) {
    PAWN('p'),
    KNIGHT('n'),
    BISHOP('b'),
    ROOK('r'),
    QUEEN('q'),
    KING('k'),
}