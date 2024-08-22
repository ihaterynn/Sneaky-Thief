package sneakythief.util

import scalikejdbc._
import sneakythief.model.DetectiveBoard

trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
  val dbURL = "jdbc:derby:myDB;create=true;"

  // Initialize JDBC driver & connection pool
  Class.forName(derbyDriverClassname)
  ConnectionPool.singleton(dbURL, "Sneaky_Thief", "21074653")

  // Ad-hoc session provider for the REPL
  implicit val session = AutoSession
}

object Database extends Database {
  def setupDB(): Unit = {
    if (!hasDBInitialize) {
      DetectiveBoard.initializeTable()
    }
  }

  def hasDBInitialize: Boolean = {
    DB.getTable("DetectiveBoard") match {
      case Some(_) => true
      case None    => false
    }
  }
}
