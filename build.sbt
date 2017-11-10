
name := "playground-scala"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.11"

resolvers += Resolver.sonatypeRepo("releases")

resolvers += "nilskp/maven on bintray" at "http://dl.bintray.com/nilskp/maven"

resolvers in ThisBuild += "internal.nexus" at "http://nexus.oncue.verizon.net/nexus/content/groups/internal"

libraryDependencies ++= Seq(
  "org.typelevel"     %% "cats"              % "0.4.1",
//  "org.typelevel" %% "alleycats" % "0.1.2",
  "com.chuusai"       %% "shapeless"         % "2.2.5",
  "net.liftweb"       %% "lift-json"         % "2.6.2",
  "io.reactivex"      %% "rxscala"           % "0.25.0",
  "org.scalaz"        %% "scalaz-core"       % "7.2.0",
  "org.scalaz"        %% "scalaz-effect"     % "7.2.0",
  "org.scalaz"        %% "scalaz-concurrent" % "7.2.0",
  "org.scalaz.stream" %% "scalaz-stream"     % "0.8",

  "com.hazelcast"      % "hazelcast"         % "3.6",
//  "com.hazelcast"     %% "hazelcast-scala"   % "latest-integration" withSources(),
  "javax.cache"        % "cache-api"         % "1.0.0"//,

//  "iptv.dena" %% "spark" % "2.0.+"
)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.7.1")
