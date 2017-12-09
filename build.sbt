import Dependencies._

val baseName = "async-trace-by-aws-x-ray"

val commonSettings = Seq(
  scalaVersion := "2.12.4",
  organization := "com.github.yoshiyoshifujii"
)

val assemblySettings = Seq(
  assemblyMergeStrategy in assembly := {
    case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
    case "application.conf" => MergeStrategy.concat
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  },
  assemblyJarName in assembly := s"${name.value}.jar",
)

lazy val root = (project in file(".")).
  aggregate(
    publisher,
    subscriber
  ).
  settings(commonSettings: _*).
  settings(assemblySettings: _*).
  settings(
    name := s"$baseName"
  )

lazy val publisher = (project in file("./modules/publisher")).
  settings(commonSettings: _*).
  settings(assemblySettings: _*).
  settings(
    name := s"$baseName-publisher",
    libraryDependencies ++= publisherDeps
  )

lazy val subscriber = (project in file("./modules/subscriber")).
  settings(commonSettings: _*).
  settings(assemblySettings: _*).
  settings(
    name := s"$baseName-subscriber",
    libraryDependencies ++= subscriberDeps
  )

