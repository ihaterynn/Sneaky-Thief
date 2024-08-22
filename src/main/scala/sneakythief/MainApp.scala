package sneakythief

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, BorderPane}
import scalafx.scene.media.{Media, MediaPlayer}
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.scene.layout.{AnchorPane => JFXAnchorPane, BorderPane => JFXBorderPane}
import sneakythief.util.Database

object MainApp extends JFXApp {

  // Variable to store player's name
  var playerName: String = _

  // Background music player
  private var bgmPlayer: MediaPlayer = _

  // Initialize the database and create tables if they don't exist
  Database.setupDB()

  /**
   * Initializes the background music player with looping enabled.
   */
  private def initializeBgm(): Unit = {
    val bgmPath = getClass.getResource("/SFX/BGM.wav").toExternalForm
    val bgm = new Media(bgmPath)
    bgmPlayer = new MediaPlayer(bgm)
    bgmPlayer.cycleCount = MediaPlayer.Indefinite
    bgmPlayer.volume = 0.5
    bgmPlayer.play()
  }

  /**
   * Loads the root layout from the FXML file and wraps it in a ScalaFX BorderPane.
   */
  private def loadRootLayout(): BorderPane = {
    val resourceUrl = getClass.getResource("/sneakythief/view/RootLayout.fxml")
    require(resourceUrl != null, "RootLayout.fxml not found")
    val loader = new FXMLLoader(resourceUrl, NoDependencyResolver)
    val rootLayout = loader.load().asInstanceOf[JFXBorderPane]
    new BorderPane(rootLayout)
  }

  /**
   * Loads the menu layout from the FXML file and wraps it in a ScalaFX AnchorPane.
   */
  private def loadMenuLayout(): AnchorPane = {
    val resourceUrl = getClass.getResource("/sneakythief/view/Menu.fxml")
    require(resourceUrl != null, "Menu.fxml not found")
    val loader = new FXMLLoader(resourceUrl, NoDependencyResolver)
    val menuLayout = loader.load().asInstanceOf[JFXAnchorPane]
    new AnchorPane(menuLayout)
  }

  /**
   * Wraps a given screen layout with the root layout and returns a new Scene.
   */
  private def wrapWithRootLayout(screenLayout: AnchorPane): Scene = {
    val rootLayout = loadRootLayout()
    rootLayout.center = screenLayout
    new Scene(rootLayout)
  }

  // Initialize the primary stage
  stage = new PrimaryStage {
    title = "Sneaky Thief"
    scene = new Scene(loadRootLayout()) // Initially load the root layout
  }

  /**
   * Switches the current screen to the specified screen.
   */
  def switchScreen(screenName: String): Unit = {
    println(s"Switching to screen: $screenName")
    screenName match {
      case "Gameplay" => Gameplay.startGame() // Special case for Gameplay
      case _ =>
        val resourceUrl = getClass.getResource(s"/sneakythief/view/$screenName.fxml")
        require(resourceUrl != null, s"FXML file $screenName.fxml not found")
        val loader = new FXMLLoader(resourceUrl, NoDependencyResolver)
        val layout = loader.load().asInstanceOf[JFXAnchorPane]
        val newScene = wrapWithRootLayout(new AnchorPane(layout))
        stage.scene = newScene
    }
  }

  /**
   * Sets the player's name.
   */
  def setPlayerName(name: String): Unit = {
    playerName = name
  }

  /**
   * Retrieves the player's name.
   */
  def getPlayerName: String = playerName

  /**
   * Retrieves the background music player instance.
   */
  def getBgmPlayer: MediaPlayer = bgmPlayer

  // Show the menu screen initially when the stage is shown
  stage.onShown = _ => {
    initializeBgm()
    switchScreen("Menu")
  }
}
