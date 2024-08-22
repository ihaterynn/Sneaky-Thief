package sneakythief.model

import scalafx.scene.image.ImageView
import scalafx.scene.layout.AnchorPane
import scalafx.animation.{Interpolator, TranslateTransition}
import scalafx.util.Duration
import scala.util.Random

object Thief extends Character {

  // Method to create the Thief ImageView using the shared method from Character trait
  def createThief(): ImageView = {
    createCharacter("/Graphics/Thief/Hiding.png", 200, 200, 50, 50)
  }

  // Method to move the thief smoothly within screen bounds
  def moveThiefSmoothly(thief: ImageView, gameplayPane: AnchorPane): Unit = {
    val random = new Random()
    val maxX = screenWidth - thief.fitWidth.value
    val maxY = screenHeight - thief.fitHeight.value

    def transitionThief(): Unit = {
      val newX = random.nextDouble() * maxX
      val newY = random.nextDouble() * maxY

      val transition = new TranslateTransition {
        duration = Duration(800) // Increased speed by reducing duration
        node = thief
        toX = newX - thief.layoutX.value
        toY = newY - thief.layoutY.value
        interpolator = Interpolator.EaseBoth
      }

      transition.onFinished = _ => transitionThief() // Continue moving after each transition
      transition.play()
    }

    transitionThief() // Start the movement loop
  }
}
