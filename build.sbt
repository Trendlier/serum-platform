lazy val root = project.in(file(".")).enablePlugins(PlayJava)

name := """serum-platform"""

version := "1.0-SNAPSHOT"

libraryDependencies += javaEbean

libraryDependencies += "org.postgresql" % "postgresql" % "9.3-1102-jdbc41"
