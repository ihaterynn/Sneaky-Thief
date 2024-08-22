package sneakythief.controller

import scalafx.application.Platform
import scalafxml.core.macros.sfxml
import sneakythief.MainApp

@sfxml
class RootLayoutController {

  // Handler for the Menu button
  def BackToMenu(): Unit = {
    MainApp.switchScreen("Menu")
  }

  // Handler for the Exit button
  def handleExit(): Unit = {
    Platform.exit() // Close the application
  }
}
