lazy val root = project.in(file(".")).enablePlugins(PlayJava)

name := """serum-platform"""

version := "1.0-SNAPSHOT"

/**
 * Java options
 */

javaOptions += "-Dhttp.port=80"

// TODO: Enable HTTPS, disable HTTP
// http://www.playframework.com/documentation/2.3.x/ConfiguringHttps
//javaOptions += "-Dhttp.port=disabled"
//javaOptions += "-Dhttps.port=443"

// CRITICAL: JDBC WILL NOT STORE TIMESTAMPS IN UTC UNLESS THIS IS HERE.
// DO NOT REMOVE!!!
// DO NOT REMOVE!!!
// DO NOT REMOVE!!!
// DO NOT REMOVE!!!
// DO NOT REMOVE!!!
// DO NOT REMOVE!!!
javaOptions += "-Duser.timezone=UTC"
// P.S. DO. NOT. REMOVE!!!

/**
 * Library dependencies
 */

libraryDependencies += javaJpa

libraryDependencies += "org.hibernate" % "hibernate-entitymanager" % "3.6.9.Final"

libraryDependencies += "org.postgresql" % "postgresql" % "9.3-1102-jdbc41"

libraryDependencies += "com.restfb" % "restfb" % "1.6.14"

libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.8.6"
