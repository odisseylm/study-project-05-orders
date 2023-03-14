package com.mvv.utils


/** kotlin style - for easier migrating from kotlin */
//noinspection ScalaFileName
object kotlin:
  extension [T](obj: T)
    inline def also(f: T=>Unit): T = { f(obj); obj }
