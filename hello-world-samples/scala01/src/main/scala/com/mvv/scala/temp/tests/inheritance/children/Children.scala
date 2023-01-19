//noinspection ScalaUnusedSymbol
package com.mvv.scala.temp.tests.inheritance.children

import com.mvv.scala.temp.tests.inheritance.parentclass.{AbstractParentClass, OpenParentClass, SealedParentClass, StdParentClass}


class StdChildClass1 extends AbstractParentClass


import scala.language.adhocExtensions // you need it if you still want extend non-opened class (otherwise warning)
class StdChildClass extends StdParentClass


class OpenChildClass extends OpenParentClass


// compilation error
//class ChildOfSealedClass extends SealedParentClass
