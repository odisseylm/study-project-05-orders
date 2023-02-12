package org.mvv.mapstruct.scala.debug

import scala.quoted.Quotes


// base Selector = Selector <: AnyRef
def dumpSelector(using quotes: Quotes)(selector: quotes.reflect.Selector, str: StringBuilder, padLength: Int): Unit =
  import quotes.reflect.*
  selector match
    // Selector <: AnyRef
    // RenameSelector <: Selector
    case el if el.isRenameSelector => dumpRenameSelector(el.asInstanceOf[RenameSelector], str, padLength)
    // OmitSelector <: Selector
    case el if el.isOmitSelector => dumpOmitSelector(el.asInstanceOf[OmitSelector], str, padLength)
    // GivenSelector <: Selector
    case el if el.isGivenSelector => dumpGivenSelector(el.asInstanceOf[GivenSelector], str, padLength)
    // base Selector = Selector <: AnyRef
    case el if el.isSelector => dumpBaseSelector(el, str, padLength)


// Selector <: AnyRef
// RenameSelector <: Selector
def dumpRenameSelector(using quotes: Quotes)(el: quotes.reflect.RenameSelector, str: StringBuilder, nextPadLength: Int): Unit = {}
// OmitSelector <: Selector
def dumpOmitSelector(using quotes: Quotes)(el: quotes.reflect.OmitSelector, str: StringBuilder, nextPadLength: Int): Unit = {}
// GivenSelector <: Selector
def dumpGivenSelector(using quotes: Quotes)(el: quotes.reflect.GivenSelector, str: StringBuilder, nextPadLength: Int): Unit = {}
// base Selector <: AnyRefSelector
def dumpBaseSelector(using quotes: Quotes)(el: quotes.reflect.Selector, str: StringBuilder, nextPadLength: Int): Unit = {}
