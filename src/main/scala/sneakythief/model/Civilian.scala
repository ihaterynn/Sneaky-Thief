package sneakythief.model

import scalafx.scene.image.ImageView
import scalafx.animation.TranslateTransition
import scalafx.scene.layout.AnchorPane
import scalafx.util.Duration
import scala.math.{min, max}

case class CivilianAttributes(
                               name: String,
                               positionX: Double,
                               positionY: Double,
                               imagePath: String = "/Graphics/Civillian/default.png", // default image path
                               fitHeight: Double = 150,
                               fitWidth: Double = 125
                             )

object Civilian extends Character {

  // Create a civilian based on the provided attributes
  def createCivilian(attributes: CivilianAttributes): ImageView = {
    createCharacter(attributes.imagePath, attributes.fitHeight, attributes.fitWidth, attributes.positionX, attributes.positionY)
  }

  // Method to move civilian1 up and down within the screen bounds
  def moveCivilian1(civilian: ImageView, gameplayPane: AnchorPane): Unit = {
    val initialY = civilian.layoutY.value
    val maxY = screenHeight - civilian.fitHeight.value // Ensure the entire image stays within the screen

    def transitionCivilian(): Unit = {
      val direction = if (civilian.layoutY.value <= initialY) 1 else -1 // Determine direction based on initial position
      val moveDistance = 200.0 // Movement distance
      val newY = civilian.layoutY.value + direction * moveDistance
      val clampedY = min(max(newY, 0), maxY) // Clamp the new Y position within bounds

      val transition = new TranslateTransition {
        duration = Duration(1500)
        node = civilian
        toY = clampedY - civilian.layoutY.value
        autoReverse = true
        cycleCount = TranslateTransition.Indefinite
      }

      transition.play()
    }

    transitionCivilian()
  }

  // Method to move civilian3 left and right within the screen bounds
  def moveCivilian3(civilian: ImageView, gameplayPane: AnchorPane): Unit = {
    val initialX = civilian.layoutX.value
    val maxX = screenWidth - civilian.fitWidth.value

    def transitionCivilian(): Unit = {
      val direction = if (civilian.layoutX.value <= initialX) 1 else -1 // Determine direction based on initial position
      val moveDistance = 200.0 // Movement distance
      val newX = civilian.layoutX.value + direction * moveDistance
      val clampedX = min(max(newX, 0), maxX) // Clamp the new X position within bounds

      val transition = new TranslateTransition {
        duration = Duration(1500)
        node = civilian
        toX = clampedX - civilian.layoutX.value
        autoReverse = true
        cycleCount = TranslateTransition.Indefinite
      }

      transition.onFinished = _ => {
        transitionCivilian()
      }
      transition.play()
    }

    transitionCivilian()
  }
}
