package org.mvv.scala.mapstruct.mappers


enum SelectEnumMode :
  case
      /** Recursive quotes 'Select' is used for every part of package/className/enumValue. */
      ByEnumFullClassName
      /** Tricky but interesting approach :-) */
    , ByEnumClassThisType
