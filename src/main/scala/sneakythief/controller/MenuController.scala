package sneakythief.controller

import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafxml.core.macros.sfxml
import scalafx.event.ActionEvent
import sneakythief.MainApp

@sfxml
class MenuController(
                      private val startButton: Button,
                      private val muteButton: Button
                    ) {

  private val muteImageView = new ImageView()
  muteButton.graphic = muteImageView
  private var isMuted: Boolean = false

  // Initialize mute button image
  private def initMuteButton(): Unit = {
    updateMuteImage()
  }

  // Call the initialization method when the controller is created
  initMuteButton()

  // Start the game by switching to the InputName scene
  def StartGame(): Unit = {
    MainApp.switchScreen("InputName")
  }

  // Toggle mute functionality for the background music
  def ToggleMute(): Unit = {
    isMuted = !isMuted
    val bgmPlayer = MainApp.getBgmPlayer
    bgmPlayer.volume = if (isMuted) 0.0 else 0.5
    updateMuteImage()
  }

  // Update the mute button image based on the current mute state
  private def updateMuteImage(): Unit = {
    val imageUrl = if (isMuted) "/Graphics/Misc/bgmOFF-.png" else "/Graphics/Misc/bgmON+.png"
    muteImageView.image = new Image(getClass.getResourceAsStream(imageUrl))
    muteImageView.fitHeight = 60.0
    muteImageView.fitWidth = 60.0
  }

  // Open the Detective Board scene
  def OpenDetectiveboard(event: ActionEvent): Unit = {
    MainApp.switchScreen("DetectiveBoard")
  }
}
