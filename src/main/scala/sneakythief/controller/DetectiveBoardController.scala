package sneakythief.controller

import sneakythief.MainApp
import sneakythief.model.DetectiveBoard
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Alert, Button, TableColumn, TableView}
import scalafx.scene.input.MouseEvent
import scalafxml.core.macros.sfxml
import scalafx.scene.control.Alert.AlertType


// Trait to handle table column binding
trait TableController[S] {
  def bindColumn[T](column: TableColumn[S, T], valueExtractor: S => T): Unit = {
    column.cellValueFactory = { cellData =>
      ObjectProperty(valueExtractor(cellData.value))
    }
  }
}

@sfxml
class DetectiveBoardController(
                                private val detectiveboardTable: TableView[(String, Int)],
                                private val detectiveNameColumn: TableColumn[(String, Int), String],
                                private val timeColumn: TableColumn[(String, Int), Int],
                              ) extends TableController[(String, Int)] {

  def initialize(): Unit = {
    try {
      // Bind the columns using the trait method
      bindColumn(detectiveNameColumn, (entry: (String, Int)) => entry._1)
      bindColumn(timeColumn, (entry: (String, Int)) => entry._2)

      // Retrieve and display entries
      val entries = DetectiveBoard.getEntries
      if (entries.isEmpty) {
        println("No entries found in the leaderboard.")
      } else {
        println(s"Entries found: ${entries.mkString(", ")}")
      }

      val leaderboardData = ObservableBuffer(entries: _*)
      println(s"Setting TableView items: ${leaderboardData.mkString(", ")}")
      detectiveboardTable.items = leaderboardData
      detectiveboardTable.refresh() // Ensure the UI is refreshed
    } catch {
      case ex: Exception =>
        new Alert(AlertType.Error) {
          initOwner(MainApp.stage)
          title = "Error"
          headerText = "An error occurred"
          contentText = s"Could not load leaderboard data: ${ex.getMessage}"
        }.showAndWait()
    }
  }

  def BackToMenu(event: MouseEvent): Unit = {
    MainApp.switchScreen("Menu")
  }

  def handleClear(event: MouseEvent): Unit = {
    try {
      DetectiveBoard.clearEntries()
      detectiveboardTable.items = ObservableBuffer.empty
      detectiveboardTable.refresh() // Refresh the UI after clearing
    } catch {
      case ex: Exception =>
        new Alert(AlertType.Error) {
          initOwner(MainApp.stage)
          title = "Error"
          headerText = "An error occurred"
          contentText = s"Could not clear leaderboard data: ${ex.getMessage}"
        }.showAndWait()
    }
  }
}
