package sneakythief.controller

import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, TextField}
import scalafx.scene.image.Image
import scalafx.scene.layout._
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml
import sneakythief.MainApp

@sfxml
class InputNameController(
                           private val nameField: TextField,
                           private val okButton: Button,
                           private val cancelButton: Button,
                           private val anchorPane: AnchorPane
                         ) {

  private var dialogStage: Stage = _

  def initialize(): Unit = {
    println("Initializing InputNameController")

    if (anchorPane == null) {
      println("AnchorPane is null")
    } else {
      val imagePath = "/Graphics/Misc/blueframe.png"
      val imageStream = getClass.getResourceAsStream(imagePath)

      if (imageStream == null) {
        println(s"Failed to load image from path: $imagePath")
      } else {
        val image = new Image(imageStream)
        val backgroundImage = new BackgroundImage(
          image,
          BackgroundRepeat.NoRepeat,
          BackgroundRepeat.NoRepeat,
          BackgroundPosition.Center,
          BackgroundSize.Default
        )
        anchorPane.background = new Background(Array(backgroundImage))
      }
    }

    // Set button actions directly
    okButton.onAction = handleOk _
    cancelButton.onAction = handleCancel _
  }

  def handleOk(event: ActionEvent): Unit = {
    val name = nameField.text.value.trim
    if (name.nonEmpty) {
      MainApp.setPlayerName(name) // Store the name in MainApp
      MainApp.switchScreen("Story") // Direct to the Story screen
    } else {
      new Alert(AlertType.Warning) {
        title = "Input Required"
        headerText = ""
        contentText = "Please enter your name, Detective!"
      }.showAndWait()
    }
  }

  def handleCancel(event: ActionEvent): Unit = {
    MainApp.switchScreen("Menu")
  }

}
