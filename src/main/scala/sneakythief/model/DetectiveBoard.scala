package sneakythief.model

import scalikejdbc._

object DetectiveBoard {

  def initializeTable(): Unit = {
    DB.autoCommit { implicit session =>
      try {
        println("Starting table creation for DetectiveBoard.")
        sql"""
          CREATE TABLE DetectiveBoard (
            id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
            playerName VARCHAR(64),
            timeScore INT
          )
        """.execute.apply()
        println("DetectiveBoard table creation executed successfully.")
      } catch {
        case e: Exception =>
          println(s"Error creating DetectiveBoard table: ${e.getMessage}")
      }
    }
  }

  def addEntry(playerName: String, timeScore: Int): Unit = {
    DB.autoCommit { implicit session =>
      try {
        println(s"Adding entry: Player: $playerName, Time: $timeScore")
        sql"INSERT INTO DetectiveBoard (playerName, timeScore) VALUES ($playerName, $timeScore)".update.apply()
        println("Entry added successfully.")
      } catch {
        case e: Exception =>
          println(s"Error adding entry to DetectiveBoard: ${e.getMessage}")
      }
    }
  }

  def getEntries: List[(String, Int)] = {
    DB.readOnly { implicit session =>
      try {
        val result = sql"SELECT playerName, timeScore FROM DetectiveBoard ORDER BY timeScore ASC"
          .map(rs => (rs.string("playerName"), rs.int("timeScore"))).list.apply()

        println(s"Entries retrieved: ${result.mkString(", ")}") // Add this line
        result
      } catch {
        case e: Exception =>
          println(s"Error retrieving entries from DetectiveBoard: ${e.getMessage}")
          List.empty
      }
    }
  }

  def clearEntries(): Unit = {
    DB.autoCommit { implicit session =>
      try {
        sql"DELETE FROM DetectiveBoard".update.apply()
        println("All entries cleared from DetectiveBoard.")
      } catch {
        case e: Exception =>
          println(s"Error clearing DetectiveBoard: ${e.getMessage}")
      }
    }
  }
}
