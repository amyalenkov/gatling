import io.gatling.build.SonatypeReleasePlugin

import BuildSettings._
import Bundle._
import ConfigFiles._
import CopyLogback._
import Dependencies._
import VersionFile._
import pl.project13.scala.sbt.JmhPlugin
import sbt.Keys._
import sbt._

// Root project

lazy val root = Project("gatling-parent", file("."))
  .enablePlugins(SonatypeReleasePlugin)
  .dependsOn(Seq(commons, core, http, jms, jdbc, redis).map(_ % "compile->compile;test->test"): _*)
  .aggregate(commons, core, jdbc, redis, httpAhc, http, jms, charts, metrics, app, recorder, testFramework, bundle, compiler)
  .settings(basicSettings: _*)
  .settings(noArtifactToPublish)
  .settings(docSettings(benchmarks, bundle): _*)
  .settings(libraryDependencies ++= docDependencies)
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))


// Modules

def gatlingModule(id: String) = Project(id, file(id))
  .enablePlugins(SonatypeReleasePlugin)
  .settings(gatlingModuleSettings: _*)
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))


lazy val commons = gatlingModule("gatling-commons")
  .settings(libraryDependencies ++= commonsDependencies(scalaVersion.value))
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))


lazy val core = gatlingModule("gatling-core")
  .dependsOn(commons % "compile->compile;test->test")
  .settings(libraryDependencies ++= coreDependencies)
  .settings(generateVersionFileSettings: _*)
  .settings(copyGatlingDefaults(compiler): _*)
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))


lazy val jdbc = gatlingModule("gatling-jdbc")
  .dependsOn(core % "compile->compile;test->test")
  .settings(libraryDependencies ++= jdbcDependencies)
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))


lazy val redis = gatlingModule("gatling-redis")
  .dependsOn(core % "compile->compile;test->test")
  .settings(libraryDependencies ++= redisDependencies)
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))


lazy val httpAhc = gatlingModule("gatling-http-ahc")
  .dependsOn(core % "compile->compile;test->test")
  .settings(libraryDependencies ++= httpAhcDependencies)
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))


lazy val http = gatlingModule("gatling-http")
  .dependsOn(httpAhc % "compile->compile;test->test")
  .settings(libraryDependencies ++= httpDependencies)
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))


lazy val jms = gatlingModule("gatling-jms")
  .dependsOn(core % "compile->compile;test->test")
  .settings(libraryDependencies ++= jmsDependencies)
  .settings(parallelExecution in Test := false)
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))


lazy val charts = gatlingModule("gatling-charts")
  .dependsOn(core % "compile->compile;test->test")
  .settings(libraryDependencies ++= chartsDependencies)
  .settings(excludeDummyComponentLibrary: _*)
  .settings(chartTestsSettings: _*)
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))


lazy val metrics = gatlingModule("gatling-metrics")
  .dependsOn(core % "compile->compile;test->test")
  .settings(libraryDependencies ++= metricsDependencies)
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))


lazy val compiler = gatlingModule("gatling-compiler")
  .settings(scalaVersion := "2.10.6")
  .settings(libraryDependencies ++= compilerDependencies(scalaVersion.value))
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))


lazy val benchmarks = gatlingModule("gatling-benchmarks")
  .dependsOn(core, http)
  .enablePlugins(JmhPlugin)
  .settings(libraryDependencies ++= benchmarkDependencies)
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))

lazy val app = gatlingModule("gatling-app")
  .dependsOn(core, http, jms, jdbc, redis, metrics, charts)
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))


lazy val recorder = gatlingModule("gatling-recorder")
  .dependsOn(core % "compile->compile;test->test", http)
  .settings(libraryDependencies ++= recorderDependencies)
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))


lazy val testFramework = gatlingModule("gatling-test-framework")
  .dependsOn(app)
  .settings(libraryDependencies ++= testFrameworkDependencies)
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))


lazy val bundle = gatlingModule("gatling-bundle")
  .dependsOn(core, http)
  .settings(generateConfigFiles(core): _*)
  .settings(generateConfigFiles(recorder): _*)
  .settings(copyLogbackXml(core): _*)
  .settings(bundleSettings: _*)
  .settings(noArtifactToPublish)
  .settings(publishTo := Some("Artifactory Realm" at "https://repos.ncloudtech.ru/libs-snapshot-local"))

