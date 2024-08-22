package sneakythief.controller

import scalafx.event.ActionEvent
import scalafxml.core.macros.sfxml
import sneakythief.MainApp

@sfxml
class WinEndingController {

  def BackToMenu(event: ActionEvent): Unit = {
    MainApp.switchScreen("Menu")
  }
}
