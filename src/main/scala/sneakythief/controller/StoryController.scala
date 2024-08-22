package sneakythief.controller

import scalafx.animation.{KeyFrame, Timeline}
import scalafx.application.Platform
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, ButtonType}
import scalafx.scene.image.ImageView
import scalafx.scene.text.Text
import scalafx.util.Duration
import scalafxml.core.macros.sfxml
import sneakythief.MainApp

@sfxml
class StoryController(
                       protected val dialogBox: ImageView,
                       protected val arrivalText: Text,
                       protected val instructionsText: Text,
                       protected val skipButton: Button
                     ) {

  // Merged attributes from BaseStoryController
  private var arrivalTimeline: Timeline = _
  private var instructionsTimeline: Timeline = _
  private var dialogFadeTimeline: Timeline = _

  // Merged methods from BaseStoryController
  private def createTimeline(content: String, textNode: Text, durationPerChar: Double): Timeline = {
    new Timeline {
      keyFrames = (0 until content.length).map { i =>
        KeyFrame(Duration(i * durationPerChar), onFinished = _ => {
          textNode.opacity = 1.0
          textNode.text = content.substring(0, i + 1)
        })
      }
      cycleCount = 1
    }
  }

  private def fadeOutText(textNode: Text, fadeDuration: Double, delayAfterFade: Double, onFinish: () => Unit): Unit = {
    val fadeOutTimeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration(0), onFinished = _ => textNode.opacity = 1.0),
        KeyFrame(Duration(fadeDuration), onFinished = _ => textNode.opacity = 0.0)
      )
      cycleCount = 1
    }

    fadeOutTimeline.onFinished = _ => {
      val delayTimeline = new Timeline {
        keyFrames = Seq(
          KeyFrame(Duration(delayAfterFade), onFinished = _ => Platform.runLater(new Runnable {
            override def run(): Unit = onFinish()
          }))
        )
      }
      delayTimeline.play()
    }

    fadeOutTimeline.play()
  }

  // Method to play the story
  def playStory(): Unit = {
    val playerName = MainApp.getPlayerName

    // Initialize text and dialog box opacity
    arrivalText.opacity = 0.0
    instructionsText.opacity = 0.0
    dialogBox.opacity = 0.0

    // Text content
    val arrivalTextContent = s"We're glad you have arrived, Detective $playerName. We have a hostage situation inside the store."
    val instructionsTextContent = "Suspect is armed, and your mission: neutralize the threat and keep the hostages safe."

    // Timelines for text animations
    arrivalTimeline = createTimeline(arrivalTextContent, arrivalText, durationPerChar = 50)
    instructionsTimeline = createTimeline(instructionsTextContent, instructionsText, durationPerChar = 55)

    // Timeline for dialog fade-in effect
    dialogFadeTimeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration(0), onFinished = _ => dialogBox.opacity = 0.0),
        KeyFrame(Duration(800), onFinished = _ => dialogBox.opacity = 1.0)
      )
      cycleCount = 1
    }

    dialogFadeTimeline.play()

    dialogFadeTimeline.onFinished = _ => arrivalTimeline.play()

    arrivalTimeline.onFinished = _ => {
      fadeOutText(arrivalText, 500, 200, () => instructionsTimeline.play())
    }

    instructionsTimeline.onFinished = _ => {
      if (!skipButton.isPressed) {
        Platform.runLater(() => showControlsAlert())
      }
    }
  }

  // Method to skip the story
  def skipStory(): Unit = {
    Option(arrivalTimeline).foreach(_.stop())
    Option(instructionsTimeline).foreach(_.stop())
    Option(dialogFadeTimeline).foreach(_.stop())

    Platform.runLater(() => showControlsAlert())
  }

  // Utility method to show controls alert
  private def showControlsAlert(): Unit = {
    Platform.runLater {
      val alert = new Alert(AlertType.Information) {
        title = "Controls"
        headerText = "Game Controls"
        contentText = "Aim your cursor and shoot the thief while avoiding hostages."
      }

      alert.showAndWait() match {
        case Some(ButtonType.OK) =>
          MainApp.switchScreen("Gameplay")
        case _ => // Handle other cases if needed
      }
    }
  }

  // Start the story when the controller is loaded
  playStory()

  skipButton.onAction = _ => skipStory()
}
