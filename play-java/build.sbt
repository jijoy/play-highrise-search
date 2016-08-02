name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"
PlayKeys.externalizeResources := false


libraryDependencies ++= Seq(
javaJdbc,
  cache,
  javaJpa,
  "org.hibernate" % "hibernate-entitymanager" % "5.1.0.Final",
  "mysql" % "mysql-connector-java" % "5.1.26",
  filters,
  javaWs
)
