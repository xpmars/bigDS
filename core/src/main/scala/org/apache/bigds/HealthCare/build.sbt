name := "Health Care BigDS support"

version := "0.0.1"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq("org.apache.spark" % "spark-core_2.10" % "1.2.0",
                        "org.apache.spark" % "spark-mllib_2.10" % "1.2.0",
                        "org.apache.commons" % "commons-math3" % "3.0",
                        "org.scalatest"    %% "scalatest"       % "1.9.1"  % "test"
                      )

resolvers  ++= Seq("Apache Repository" at "https://repository.apache.org/content/repositories/releases",
               "Akka Repository" at "http://repo.akka.io/releases/",
               "Spray Repository" at "http://repo.spray.cc/")