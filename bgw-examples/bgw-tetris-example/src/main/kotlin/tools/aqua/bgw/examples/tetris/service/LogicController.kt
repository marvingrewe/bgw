package tools.aqua.bgw.examples.tetris.service

import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.event.KeyCode
import tools.aqua.bgw.examples.tetris.entity.Piece
import tools.aqua.bgw.examples.tetris.entity.Tetris
import tools.aqua.bgw.examples.tetris.entity.Tile
import tools.aqua.bgw.examples.tetris.view.Refreshable
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.math.roundToLong

/**
 * Controller managing game actions.
 */
class LogicController(private val view: Refreshable) {
	
	/**
	 * Mutex.
	 */
	private val mutex: Any = Any()
	
	/**
	 * Piece generator.
	 */
	private val generator: PieceGenerator = PieceGenerator()
	
	/**
	 * Running state.
	 */
	private var running: Boolean = false
	
	/**
	 * Blocked state from down button.
	 */
	private var blocked: Boolean = false
	
	/**
	 * Counter for delay.
	 */
	private var counter: Int = 10
	
	/**
	 * Game timer.
	 */
	private var timer: Timer = Timer()
	
	/**
	 * Current Tetris instance.
	 */
	private var tetris: Tetris = Tetris(generator.generate3())
	
	/**
	 * Starts game. When called while game is already running, nothing happens.
	 */
	fun startGame() {
		synchronized(mutex) {
			if (!running) {
				nextPiece()
				
				startTimer(400)
				
				running = true
				view.hideStartInstructions()
			}
		}
	}
	
	/**
	 * Starts a new game timer with given delay.
	 *
	 * @param speed Delay for the timer.
	 */
	private fun startTimer(speed: Long) {
		view.refreshSpeed(speed)
		counter = 10
		
		timer = Timer()
		timer.scheduleAtFixedRate(
			delay = speed,
			period = speed
		) {
			BoardGameApplication.runOnGUIThread {
				movePiece()
				
				if (counter <= 0) {
					timer.cancel()
					startTimer((speed * 0.8).roundToLong())
				}
			}
		}
	}
	
	/**
	 * Stops the current timer. This method may be called repeatedly. The second and subsequent calls have no effect.
	 */
	fun stopTimer() {
		timer.cancel()
	}
	
	/**
	 * Navigates current piece in given direction if possible.
	 *
	 * @param keyCode Pressed key. Has to be an Arrow key or an [IllegalArgumentException] will be thrown.
	 *
	 * @throws IllegalArgumentException If [keyCode] is not an arrow key.
	 */
	fun navigate(keyCode: KeyCode) {
		require(keyCode.isArrow()) { "$keyCode is not an arrow key." }
		
		if (blocked || !running)
			return
		
		synchronized(mutex) {
			when (keyCode) {
				KeyCode.LEFT ->
					if (!TetrisChecker.checkCollision(tetris = tetris, offsetX = -1))
						tetris.left()
				
				KeyCode.RIGHT ->
					if (!TetrisChecker.checkCollision(tetris = tetris, offsetX = 1))
						tetris.right()
				
				KeyCode.DOWN -> {
					while (!TetrisChecker.checkCollision(tetris = tetris, offsetY = 1))
						tetris.down()
					blocked = true
				}
				KeyCode.UP ->
					rotatePiece()
				
				else ->
					throw IllegalStateException("$keyCode is not an arrow key.")
			}
			view.refresh(tetris)
		}
	}
	
	/**
	 * Advances piece.
	 */
	private fun nextPiece() {
		synchronized(mutex) {
			counter--
			tetris.nextPiece(generator.getRandomPiece()).also { view.refresh(tetris) }
		}
	}
	
	/**
	 * Moves piece down by one if possible.
	 * If movement causes collision, piece is fixed and `nextPiece()` is called.
	 */
	private fun movePiece() {
		if (TetrisChecker.checkCollision(tetris, offsetY = 1)) {
			fixPiece()
		} else {
			tetris.down()
			view.refresh(tetris)
		}
	}
	
	/**
	 * Fixes piece at place and calls `nextPiece()`.
	 */
	private fun fixPiece() {
		synchronized(mutex) {
			val piece = tetris.currentPiece
			
			if (tetris.currentYPosition <= 0) {
				loose()
				return
			}
			
			for (y in 0 until piece.height) {
				for (x in 0 until piece.width) {
					piece.tiles[y][x]?.let {
						tetris.tetris[y + tetris.currentYPosition - 1][x + tetris.currentXPosition - 1] = it
					}
				}
			}
			
			clearRows()
			nextPiece()
			blocked = false
		}
	}
	
	/**
	 * Rotates piece clockwise if possible.
	 */
	private fun rotatePiece() {
		if (tetris.currentPiece.height == tetris.currentPiece.width) //square tile
			return
		
		var offsetY = 0
		var offsetX = 0
		val newPiece = when (tetris.currentPiece.height) {
			//strip horizontal
			1 -> {
				offsetY = -1
				offsetX = 1
				Piece(Array(4) { t -> Array(1) { tetris.currentPiece.tiles[0][t] } })
			}
			
			//2x3 tile
			2 -> Piece(Array(3) { y -> Array(2) { x -> tetris.currentPiece.tiles[(x + 1) % 2][y] } })
			
			//3x2 tile
			3 -> Piece(Array(2) { y -> Array(3) { x -> tetris.currentPiece.tiles[-x + 2][y] } })
			
			//strip vertical
			4 -> {
				offsetY = 1
				offsetX = -1
				Piece(Array(1) { Array(4) { t -> tetris.currentPiece.tiles[t][0] } })
			}
			
			else -> throw IllegalStateException()
		}
		
		//Move to the right if out of bounds
		while (tetris.currentXPosition + offsetX < 1)
			offsetX++
		
		//Move to the left if out of bounds
		while (tetris.currentXPosition + offsetX + newPiece.width > 11) {
			offsetX--
		}
		
		//If rotation is valid, update model
		if (!TetrisChecker.checkCollision(tetris, newPiece, offsetY, offsetX)) {
			tetris.apply {
				currentPiece = newPiece
				currentYPosition += offsetY
				currentXPosition += offsetX
			}
		}
	}
	
	/**
	 * Clears all full rows and adds points by the following table:
	 *
	 * 4 rows cleared : 1200P.
	 * 3 rows cleared : 300P.
	 * 2 rows cleared : 100P.
	 * 1 row cleared : 40P.
	 */
	private fun clearRows() {
		synchronized(mutex) {
			val grid = tetris.tetris
			var row = 19
			var cleared = 0
			
			//Iterate rows
			while (row >= 0) {
				if (TetrisChecker.isRowFull(tetris, row)) {
					for (y in row downTo 0) {
						for (x in 0 until 10) {
							grid[y][x] = if (y == 0) Tile(null) else grid[y - 1][x]
						}
					}
					//Count cleared rows
					cleared++
				} else {
					row--
				}
			}
			
			//Add points
			tetris.points += when (cleared) {
					4 -> 1200
					3 -> 300
					2 -> 100
					1 -> 40
					else -> 0
				}
			
			//Refresh points
			view.refreshPoints(tetris.points)
		}
	}
	
	/**
	 * Sets lost state and stops game.
	 */
	private fun loose() {
		running = false
		view.loose()
		
		stopTimer()
	}
}