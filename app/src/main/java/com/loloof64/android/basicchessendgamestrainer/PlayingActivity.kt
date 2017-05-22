package com.loloof64.android.basicchessendgamestrainer

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import com.loloof64.android.basicchessendgamestrainer.exercise_chooser.availableGenerators
import com.loloof64.android.basicchessendgamestrainer.playing_activity.MovesListAdapter
import com.loloof64.android.basicchessendgamestrainer.playing_activity.PromotionPieceChooserDialogFragment
import kotlinx.android.synthetic.main.activity_playing.*
import java.util.*

val listAdapter = MovesListAdapter()

class SpaceLeftAndRightItemDecorator(val space: Int): RecyclerView.ItemDecoration(){
    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        outRect?.left = space
        outRect?.right = space
    }
}

class PlayingActivity : AppCompatActivity(), PromotionPieceChooserDialogFragment.Companion.Listener {

    companion object {
        val currentPositionkey = "CurrentPosition"
        val playerHasWhiteKey = "PlayerHasWhite"
        val gameFinishedKey = "GameFinished"
        val lastExerciseKey = "LastExercise"
        val playerGoalIDKey = "PlayerGoalID"
        val playerGoalInAlertModeKey = "PlayerGoalInAlertMode"
        val waitingForPlayerGoalKey = "WaitingForPlayerGoal"
        val generatorIndexKey = "GeneratorIndex"
        val adapterItemsKey = "AdapterItems"
        val startedToWriteMovesKey = "StartedToWriteMoves"
        val moveToHighlightFromFileKey = "MoveToHighlightFromFile"
        val moveToHighlightFromRankKey = "MoveToHighlightFromRank"
        val moveToHighlightToFileKey = "MoveToHighlightToFile"
        val moveToHighlightToRankKey = "MoveToHighlightToRank"

        val standardFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    }

    override fun reactToPromotionPieceSelection(piece: Short) {
        playingBoard.validatePromotionMove(piece)
        playingBoard.checkIfGameFinished()
        if (!playingBoard.gameFinished()) playingBoard.makeComputerPlay()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        val gridLayoutColumns = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 9 else 6
        val gridLayoutManager = GridLayoutManager(this, gridLayoutColumns)
        moves_list_view.layoutManager = gridLayoutManager
        moves_list_view.adapter = listAdapter
        val spaceDp = 5.0f
        val space = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spaceDp, resources.displayMetrics)
        moves_list_view.addItemDecoration(SpaceLeftAndRightItemDecorator(space.toInt()))

        generatorIndex = intent.extras?.getInt(generatorIndexKey) ?: 0
        val generatedPosition = availableGenerators[generatorIndex].second.generatePosition(random.nextBoolean())
        if (generatedPosition.isNotEmpty()) {
            newGame(generatedPosition)
        }
        else {
            Toast.makeText(this, R.string.position_generation_error, Toast.LENGTH_LONG).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(currentPositionkey, playingBoard.toFEN())
        outState?.putBoolean(playerHasWhiteKey, playingBoard.playerHasWhite())
        outState?.putBoolean(gameFinishedKey, playingBoard.gameFinished())
        outState?.putString(lastExerciseKey, lastExercise)
        outState?.putInt(playerGoalIDKey, playerGoalTextId)
        outState?.putBoolean(playerGoalInAlertModeKey, playerGoalInAlertMode)
        outState?.putBoolean(waitingForPlayerGoalKey, playingBoard.isWaitingForPlayerGoal())
        outState?.putStringArray(adapterItemsKey, listAdapter.items)
        outState?.putBoolean(startedToWriteMovesKey, playingBoard.hasStartedToWriteMoves())
        outState?.putInt(moveToHighlightFromFileKey, playingBoard.getMoveToHighlightFromFile())
        outState?.putInt(moveToHighlightFromRankKey, playingBoard.getMoveToHighlightFromRank())
        outState?.putInt(moveToHighlightToFileKey, playingBoard.getMoveToHighlightToFile())
        outState?.putInt(moveToHighlightToRankKey, playingBoard.getMoveToHighlightToRank())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            playingBoard.reloadPosition(fen = savedInstanceState.getString(currentPositionkey),
                    playerHasWhite = savedInstanceState.getBoolean(playerHasWhiteKey),
                    gameFinished = savedInstanceState.getBoolean(gameFinishedKey),
                    waitingForPlayerGoal = savedInstanceState.getBoolean(waitingForPlayerGoalKey),
                    hasStartedToWriteMoves = savedInstanceState.getBoolean(startedToWriteMovesKey),
                    moveToHighlightFromFile = savedInstanceState.getInt(moveToHighlightFromFileKey),
                    moveToHighlightFromRank = savedInstanceState.getInt(moveToHighlightFromRankKey),
                    moveToHighlightToFile = savedInstanceState.getInt(moveToHighlightToFileKey),
                    moveToHighlightToRank = savedInstanceState.getInt(moveToHighlightToRankKey)
            )
            lastExercise = savedInstanceState.getString(lastExerciseKey)
            setPlayerGoalTextId(savedInstanceState.getInt(playerGoalIDKey),
                    savedInstanceState.getBoolean(playerGoalInAlertModeKey))
            listAdapter.items = savedInstanceState.getStringArray(adapterItemsKey)
        }
    }

    fun askForPromotionPiece() {
        val title = getString(R.string.promotion_chooser_title)
        val dialog = PromotionPieceChooserDialogFragment.newInstance(title, playingBoard.isWhiteToPlay())
        dialog.show(supportFragmentManager, "promotionPieceChooser")
    }

    fun reactForIllegalMove() {
        Toast.makeText(this, R.string.illegal_move, Toast.LENGTH_SHORT).show()
    }

    fun reverseBoard(view: View) {
        playingBoard.reverse()
    }

    /**
     * If playerHasWhite is given null, it will be set to the turn of the given fen
     */
    fun newGame(fen: String = standardFEN){
        setPlayerGoalTextId(R.string.empty_string, alertMode = false)
        listAdapter.clear()
        lastExercise = fen
        playingBoard.new_game(fen)
    }

    fun restartLastExercise(view: View){
        AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.restarting_exercise_alert_title)
                .setMessage(R.string.restarting_exercise_alert_message)
                .setPositiveButton(R.string.yes, {_, _ ->
                    newGame(lastExercise)
                })
                .setNegativeButton(R.string.no, null)
                .show()
    }

    fun newExercise(view: View){
        AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.new_exercise_alert_title)
                .setMessage(R.string.new_exercise_alert_message)
                .setPositiveButton(R.string.yes, {_, _ ->
                    val generatedPosition = availableGenerators[generatorIndex].second.generatePosition(random.nextBoolean())
                    newGame(generatedPosition)
                })
                .setNegativeButton(R.string.no, null)
                .show()
    }

    fun addTextInMovesList(txt: String){
        listAdapter.addText(txt)
        moves_list_view.post {
            moves_list_view.smoothScrollToPosition(listAdapter.itemCount)
        }
    }

    fun setPlayerGoalTextId(textID: Int, alertMode: Boolean){
        playerGoalTextId = textID
        playerGoalInAlertMode = alertMode
        label_player_goal.text = resources.getString(textID)
        if (alertMode) label_player_goal.setTextColor(Color.parseColor("#FA2323"))
        else label_player_goal.setTextColor(Color.parseColor("#000000"))
    }

    private lateinit var lastExercise:String
    private var generatorIndex: Int = 0
    private var random = Random()
    private var playerGoalTextId: Int = -1
    private var playerGoalInAlertMode = false
}
