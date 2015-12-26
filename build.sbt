name := "aws_helper_in_scala"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "com.amazonaws" % "aws-java-sdk" % "1.10.43",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test"
)