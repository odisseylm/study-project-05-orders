package com.mvv.scala.temp.tests.tasty

import java.nio.file.{Files, Path}


inline def fileExists(f: String) = Files.exists(Path.of(f))
