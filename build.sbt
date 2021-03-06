name := "akka-serialization-test"

organization := "com.github.dnvriend"

version := "1.0.2"

scalaVersion := "2.11.8"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= {
  val akkaVersion = "2.4.7"
  val json4sVersion = "3.3.0"
  val akkaPersistenceInMemVersion = "1.2.15"
  val avro4s = "1.4.3"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.twitter" %% "chill-akka" % "0.8.0",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "org.json4s" %% "json4s-native" % json4sVersion,
    "org.scalaz" %% "scalaz-core" % "7.2.3",
    "com.github.dnvriend" %% "akka-persistence-inmemory" % akkaPersistenceInMemVersion % Test,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    "org.scalatest" %% "scalatest" % "2.2.6" % Test,
    "org.scalacheck" %% "scalacheck" % "1.12.5" % Test,
    "com.sksamuel.avro4s" %% "avro4s-core" % avro4s,
    "org.apache.avro" % "avro" % "1.7.7"
  )
}

fork in Test := true

scalacOptions ++= Seq("-feature", "-language:higherKinds", "-language:implicitConversions", "-deprecation", "-Ybackend:GenBCode", "-Ydelambdafy:method", "-target:jvm-1.8")

javaOptions in Test ++= Seq("-Xms30m", "-Xmx30m")

parallelExecution in Test := false

licenses +=("Apache-2.0", url("http://opensource.org/licenses/apache2.0.php"))

// enable scala code formatting //
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform

// Scalariform settings
SbtScalariform.autoImport.scalariformPreferences := SbtScalariform.autoImport.scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(RewriteArrowSymbols, true)

// enable updating file headers //
import de.heikoseeberger.sbtheader.license.Apache2_0

headers := Map(
  "scala" -> Apache2_0("2016", "Dennis Vriend"),
  "conf" -> Apache2_0("2016", "Dennis Vriend", "#")
)

enablePlugins(AutomateHeaderPlugin)

// enable protobuf plugin //
// see: https://trueaccord.github.io/ScalaPB/sbt-settings.html
import com.trueaccord.scalapb.{ScalaPbPlugin => PB}

PB.protobufSettings

// protoc-jar which is on the sbt classpath //
// see: https://github.com/os72/protoc-jar
PB.runProtoc in PB.protobufConfig := (args =>
  com.github.os72.protocjar.Protoc.runProtoc("-v300" +: args.toArray))