package sneakythief

import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, BorderPane}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.control.ProgressBar
import scalafx.scene.text.Text
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.scene.media.AudioClip
import scalafx.util.Duration
import scalafxml.core.{FXMLLoader, FXMLView, NoDependencyResolver}
import javafx.scene.layout.{BorderPane => JFXBorderPane}
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import scalafx.scene.text.Font
import sneakythief.model.{Civilian, CivilianAttributes, DetectiveBoard, Thief}

object Gameplay {

  // Define screen dimensions
  val screenWidth = 1280
  val screenHeight = 720

  // Timer countdown logic
  private var timeLeft = 15
  private var timer: Timeline = _

  // Variables to track clicks and health
  private var thiefClickCount = 0
  private var health = 1.0

  // Start the game
  def startGame(): Unit = {
    resetGameState() // Reset game state before starting

    // Load the RootLayout from FXML using scalafxml
    val resource = getClass.getResource("/sneakythief/view/RootLayout.fxml")
    val rootLayout: JFXBorderPane = FXMLView(resource, NoDependencyResolver).asInstanceOf[JFXBorderPane]

    // Wrap the JavaFX BorderPane in a ScalaFX BorderPane
    val rootLayoutScala = new BorderPane(rootLayout)

    // Create the AnchorPane for gameplay elements
    val gameplayPane = new AnchorPane()

    // Set up the background image
    val backgroundImage = new ImageView(new Image(getClass.getResourceAsStream("/Graphics/Background/store.gif"))) {
      fitHeight = screenHeight
      fitWidth = screenWidth
      preserveRatio = false
    }
    gameplayPane.children.add(backgroundImage)

    // Load the PixelOperatorBold font
    val pixelOperatorBold = Font.loadFont(getClass.getResourceAsStream("/Fonts/pixel-operator-bold.ttf"), 30)

    // Set up the health label
    val hpLabel = new Text("HP:") {
      layoutX = 849
      layoutY = 70
      font = pixelOperatorBold
      style = """
      -fx-font-size: 40px;
      -fx-fill: white;
      -fx-font-weight: bold;
      """
    }
    gameplayPane.children.add(hpLabel)

    // Set up the timer label with the correct initial value
    val timerLabel = new Text("Timer: 0:15") {
      layoutX = 50
      layoutY = 70
      font = pixelOperatorBold
      style = """
      -fx-font-size: 40px;
      -fx-fill: white;
      -fx-font-weight: bold;
      """
    }
    gameplayPane.children.add(timerLabel)

    // Set up the health bar with inline CSS
    val healthBar = new ProgressBar {
      layoutX = 916
      layoutY = 45
      prefWidth = 346
      prefHeight = 35
      progress = 1.0
      style = """
      -fx-background-color: red;
      -fx-accent: red;
      -fx-font-weight: bold;
      """
    }
    gameplayPane.children.add(healthBar)

    // Create and set up civilians using the Civilian model
    val civilian1 = Civilian.createCivilian(CivilianAttributes("Civilian1", positionX = 200, positionY = 250, imagePath = "/Graphics/Civillian/civillian1.png"))
    val civilian2 = Civilian.createCivilian(CivilianAttributes("Civilian2", positionX = 645, positionY = 218, imagePath = "/Graphics/Civillian/civillian2.png"))
    val civilian3 = Civilian.createCivilian(CivilianAttributes("Civilian3", positionX = 765, positionY = 521, imagePath = "/Graphics/Civillian/civillian3.png"))

    gameplayPane.children.addAll(civilian1, civilian2, civilian3)

    // Load the gunshot sound
    val gunshotSound = new AudioClip(getClass.getResource("/SFX/Gunshot.wav").toExternalForm)

    // Create and set up the thief using the Thief model
    val thief = Thief.createThief()
    gameplayPane.children.add(thief)

    // Initialize the timer
    timer = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration(1000), onFinished = _ => {
          timeLeft -= 1
          val minutes = timeLeft / 60
          val seconds = timeLeft % 60
          timerLabel.text = f"Timer: $minutes%01d:$seconds%02d"

          if (timeLeft <= 0) {
            handleLoss() // Call the loss handler
          }
        })
      )
      cycleCount = timeLeft // Run once per second
    }

    timer.play()

    // Start the smooth movement of the thief after the scene is fully loaded
    Platform.runLater {
      Thief.moveThiefSmoothly(thief, gameplayPane)
    }

    // Start the movement of civilians
    Platform.runLater {
      Civilian.moveCivilian1(civilian1, gameplayPane)
      Civilian.moveCivilian3(civilian3, gameplayPane)
    }

    // Handle thief clicks
    thief.onMouseClicked = new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {
        thiefClickCount += 1
        health -= 0.1
        healthBar.progress = health

        // Check win conditions
        if (thiefClickCount >= 10 || health <= 0.0) {
          handleWin() // Call the win handler
        }
      }
    }

    // Handle general screen clicks for gunshot sound
    gameplayPane.onMouseClicked = new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {
        gunshotSound.play() // Play gunshot sound
      }
    }

    // Handle civilian clicks to trigger loss
    val civilianClickHandler = new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = handleLoss()
    }

    civilian1.onMouseClicked = civilianClickHandler
    civilian2.onMouseClicked = civilianClickHandler
    civilian3.onMouseClicked = civilianClickHandler

    // Add the gameplay pane to the center of the BorderPane
    rootLayoutScala.center = gameplayPane

    // Create a new scene with the root layout and set it on the stage
    val mainStage = MainApp.stage
    val newScene = new Scene(rootLayoutScala, screenWidth, screenHeight)
    mainStage.scene = newScene
    mainStage.show()
  }

  private def handleWin(): Unit = {
    timer.stop() // Stop the timer when the player wins

    // Calculate the time taken by subtracting the remaining time from the initial time
    val timeTaken = 15 - timeLeft

    // Store the player's time and name in the leaderboard
    val playerName = MainApp.getPlayerName
    val timeScore = timeTaken // Use the time taken as the timeScore
    try {
      DetectiveBoard.addEntry(playerName, timeScore) // Save entry to the database
      println(s"Record added on win: Player: $playerName, Time Taken: $timeScore seconds")
    } catch {
      case e: Exception =>
        println(s"Error adding record on win: ${e.getMessage}")
    }

    MainApp.switchScreen("WinEnding")
  }

  private def handleLoss(): Unit = {
    timer.stop() // Stop the timer

    // Calculate the time taken by subtracting the remaining time from the initial time
    val timeTaken = 15 - timeLeft

    // Store the player's time and name in the leaderboard
    val playerName = MainApp.getPlayerName
    val timeScore = timeTaken // Use the time taken as the timeScore
    try {
      DetectiveBoard.addEntry(playerName, timeScore) // Save entry to the database
      println(s"Record added on loss: Player: $playerName, Time: $timeScore seconds")
    } catch {
      case e: Exception =>
        println(s"Error adding record on loss: ${e.getMessage}")
    }
    MainApp.switchScreen("LoseEnding") // Switch to the LoseEnding screen
  }

  // Reset the game state
  private def resetGameState(): Unit = {
    timeLeft = 15
    thiefClickCount = 0 // Reset to 0
    health = 1.0
  }
}
