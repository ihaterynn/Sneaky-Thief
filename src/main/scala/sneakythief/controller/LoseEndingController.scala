package sneakythief.controller

import scalafx.event.ActionEvent
import scalafxml.core.macros.sfxml
import sneakythief.MainApp

@sfxml
class LoseEndingController {

  def BackToMenu(event: ActionEvent): Unit = {
    MainApp.switchScreen("Menu") // Consistent casing
  }

  def PlayAgain(event: ActionEvent): Unit = {
    MainApp.switchScreen("Gameplay")
  }
}
