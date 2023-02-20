package org.mvv.scala.tools.beans

import java.nio.file.{Files, Path}


inline def fileExists(f: String) = Files.exists(Path.of(f))
