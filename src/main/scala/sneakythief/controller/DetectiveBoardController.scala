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


  initialize()

  def initialize(): Unit = {
    try {
      // Bind the columns using the trait method
      bindColumn(detectiveNameColumn, (entry: (String, Int)) => entry._1)
      bindColumn(timeColumn, (entry: (String, Int)) => entry._2)

      // Set up a method to refresh the table data
      refreshTableData()
    } catch {
      case ex: Exception =>
        println(s"Error initializing DetectiveBoardController: ${ex.getMessage}")
        ex.printStackTrace()
    }
  }

  // Add this method to refresh the table data
  def refreshTableData(): Unit = {
    try {
      val entries = DetectiveBoard.getEntries
      if (entries.isEmpty) {
        println("No entries found in the leaderboard.")
      } else {
        println(s"Entries found: ${entries.mkString(", ")}")
      }

      val leaderboardData = ObservableBuffer(entries: _*)
      println(s"Setting TableView items: ${leaderboardData.mkString(", ")}")
      detectiveboardTable.items = leaderboardData
      detectiveboardTable.refresh()
    } catch {
      case ex: Exception =>
        println(s"Error refreshing table data: ${ex.getMessage}")
        ex.printStackTrace()
    }
  }

  // Modify the BackToMenu method to refresh the data before switching screens
  def BackToMenu(event: MouseEvent): Unit = {
    refreshTableData()
    MainApp.switchScreen("Menu")
  }

  // Modify the handleClear method to refresh the data after clearing
  def handleClear(event: MouseEvent): Unit = {
    try {
      DetectiveBoard.clearEntries()
      refreshTableData()
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
