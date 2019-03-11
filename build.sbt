name := "akka-simple-cluster-k8s"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.12.6"

resolvers += Resolver.bintrayRepo("tanukkii007", "maven")

mainClass := Some("com.softwaremill.akkaSimpleCluster.Main")

// Ash is used for Alpine images
enablePlugins(DockerPlugin, AshScriptPlugin)

// This would be used instead of the above if you were based on plain bash based images (eg: Non-Alpine)
//enablePlugins(DockerPlugin, JavaServerAppPackaging)

val akkaVersion = "2.5.21"
val akkaHttpVersion = "10.1.7"
val akkaManagementVersion = "1.0.0-RC3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
  "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % akkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management" % akkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management-cluster-http" % akkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % akkaManagementVersion,
  "com.github.TanUkkii007" %% "akka-cluster-custom-downing" % "0.0.12"
)

// CHANGE ME: The repo you want to push to - GCR is handy when running on GKE
dockerRepository := Some("gcr.io")

// CHANGE ME: The name of the docker image you'll deploy with
packageName in Docker := "kubernetes-playground-230221/akka-simple-cluster-k8s"

dockerBaseImage := "adoptopenjdk/openjdk8:jdk8u202-b08-alpine-slim"

dockerExposedPorts ++= Seq(8080, 2551, 8588)
dockerUpdateLatest := true

// Don't run as the root user - run as "guest" instead.
daemonUserUid in Docker := Some("405")
daemonUser in Docker := "guest"
