package com.srivastavavivekggn

package object util {

  object TypeAlias {

    type JCollection[A] = java.util.Collection[A]
    type JIterator[A] = java.util.Iterator[A]

    type JList[A] = java.util.List[A]
    type JArrayList[A] = java.util.ArrayList[A]

    type JSet[A] = java.util.Set[A]
    type JHashSet[A] = java.util.HashSet[A] // unordered
    type JTreeSet[A] = java.util.TreeSet[A] // ordered
    type JLinkedHashSet[A] = java.util.LinkedHashSet[A] // preserves insertion order

    type JMap[A, B] = java.util.Map[A, B]
    type JHashMap[A, B] = java.util.HashMap[A, B] // unordered
    type JTreeMap[A, B] = java.util.TreeMap[A, B] // ordered by key
    type JLinkedHashMap[A, B] = java.util.LinkedHashMap[A, B] // preserves insertion order

    type JBoolean = java.lang.Boolean
    type JLong = java.lang.Long
    type JFloat = java.lang.Float
    type JDouble = java.lang.Double
  }

}
