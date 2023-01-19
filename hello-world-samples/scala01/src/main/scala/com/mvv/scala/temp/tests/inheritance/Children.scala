//noinspection ScalaUnusedSymbol
package com.mvv.scala.temp.tests.inheritance

import com.mvv.scala.temp.tests.inheritance.parentclass.StdParentClass
import com.mvv.scala.temp.tests.inheritance.parentclass.OpenParentClass
import com.mvv.scala.temp.tests.inheritance.parentclass.SealedParentClass


import scala.language.adhocExtensions // you need it if you still want extend non-opened class (otherwise warning)
class StdChildClass extends StdParentClass


class OpenChildClass extends OpenParentClass


// compilation error
//class ChildOfSealedClass extends SealedParentClass
