val pluginName = "gerrit-support"

name := pluginName

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

val gerritVersion = "2.15.2"

val scalatraV = "2.4.+"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  // provided by gerrit
  "com.google.inject"     %   "guice"             % "3.0"       % Provided,
  "com.google.gerrit"     %   "gerrit-plugin-api" % gerritVersion % Provided,
  "com.google.code.gson"  %   "gson"              % "2.7"       % Provided,
  "joda-time"             %   "joda-time"         % "2.9.4"     % Provided,

  // added to assembly
  "org.scalatra"          %%  "scalatra"          % scalatraV,

  // test dependencies
  "org.scalatra"          %%  "scalatra-scalatest"% scalatraV   % Test,
  "org.scalatest"         %%  "scalatest"         % "3.0.1"     % Test,
  "net.codingwell"        %%  "scala-guice"       % "4.1.0"     % Test
  )

assemblyJarName in assembly := s"$pluginName.jar"

scalacOptions += "-target:jvm-1.7"

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

packageOptions in (Compile, packageBin) +=  {
  Package.ManifestAttributes(
    "Gerrit-ApiType" -> "plugin",
    "Gerrit-PluginName" -> pluginName,
    "Gerrit-Module" -> "com.googlesource.gerrit.plugins.support.Module",
    "Gerrit-HttpModule" -> "com.googlesource.gerrit.plugins.support.HttpModule"
  )
}
