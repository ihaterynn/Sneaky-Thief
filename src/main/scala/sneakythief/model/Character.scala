package sneakythief.model

import scalafx.scene.image.{Image, ImageView}

trait Character {
  // Shared screen dimensions for all characters
  protected val screenWidth: Int = 1280
  protected val screenHeight: Int = 720

  // Method to create a character ImageView
  def createCharacter(imagePath: String, fitHeight: Double, fitWidth: Double, positionX: Double, positionY: Double): ImageView = {
    val imageView = new ImageView(new Image(getClass.getResourceAsStream(imagePath)))
    imageView.fitHeight = fitHeight
    imageView.fitWidth = fitWidth
    imageView.layoutX = positionX
    imageView.layoutY = positionY
    imageView
  }
}
