package org.mvv.mapstruct.scala

import java.nio.file.{Files, Path}


inline def fileExists(f: String) = Files.exists(Path.of(f))
