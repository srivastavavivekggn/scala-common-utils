package com.srivastavavivekggn.scala.util.collection

import com.srivastavavivekggn.scala.util.TypeAlias.{JArrayList, JHashMap, JHashSet, JIterator, JList, JMap, JSet}
import com.srivastavavivekggn.scala.util.collection.Decorators.{AsJavaOrEmpty, AsScalaOrEmpty}
import com.srivastavavivekggn.scala.util.lang.StringUtils.Delimiters.{AT, DOT}

import scala.jdk.CollectionConverters._
import scala.reflect.ClassTag


object CollectionUtils {

  /**
   * Determine if there are any difference between the given lists
   *
   * @param left  the left list
   * @param right the right list
   * @tparam E the list entry type
   * @return the differences between the 2 lists
   */
  def diff[E](left: List[E], right: List[E]): List[E] = {

    val leftList = orEmpty(left)
    val rightList = orEmpty(right)

    val leftDiff = leftList.diff(rightList)
    val rightDiff = rightList.diff(leftList)

    leftDiff ++ rightDiff
  }


  /* ---------------------------------------------
  * Scala null safety
  * ---------------------------------------------*/

  /**
    * Return the Scala `List` or `List.empty` if null
    */
  def orEmpty[E](s: List[E]): List[E] = Option(s).getOrElse(List.empty)

  /**
    * Return the Scala `Set` or `Set.empty` if null
    */
  def orEmpty[E](s: Set[E]): Set[E] = Option(s).getOrElse(Set.empty)

  /**
    * Return the Scala Array or an empty array if null
    *
    * @param a the array
    * @tparam E the param type
    * @return the array or an empty array
    */
  def orEmpty[E](a: Array[E])(implicit tag: ClassTag[E]): Array[E] = Option(a).getOrElse(Array.empty[E])

  /**
    * Return the Scala `Map` or `Map.empty` if null
    */
  def orEmpty[K, V](s: Map[K, V]): Map[K, V] = Option(s).getOrElse(Map.empty)

  /* ---------------------------------------------
  * Java null safety
  * ---------------------------------------------*/

  /**
    * Return the Java `List` or a new `ArrayList` if null
    */
  def orEmpty[E](s: JList[E]): JList[E] = Option(s).getOrElse(new JArrayList[E]())

  /**
    * Return the Java `Set` or a new `HashSet` if null
    */
  def orEmpty[E](s: JSet[E]): JSet[E] = Option(s).getOrElse(new JHashSet[E]())

  /**
    * Return the Java `Map` or a new `HashMap` if null
    */
  def orEmpty[K, V](s: JMap[K, V]): JMap[K, V] = Option(s).getOrElse(new JHashMap[K, V]())

  /* ---------------------------------------------
  * Null-safe Java -> Scala conversions
  * ---------------------------------------------*/

  /**
    * Convert a (possibly null) Java `List` into a (non-null) Scala `List`
    */
  def asScalaListOrEmpty[E](j: JList[E]): List[E] = orEmpty(j).asScala.toList

  /**
    * Convert a (possibly null) Java `Set` into a (non-null) Scala `Set`
    */
  def asScalaSetOrEmpty[E](j: JSet[E]): Set[E] = orEmpty(j).asScala.toSet

  /**
    * Convert a (possibly null) Java `Map` into a (non-null) Scala `Map`
    */
  def asScalaMapOrEmpty[K, V](j: JMap[K, V]): Map[K, V] = orEmpty(j).asScala.toMap

  /* ---------------------------------------------
  * Null-safe Scala -> (mutable) Java conversions
  * ---------------------------------------------*/

  /**
    * Convert a (possibly null) Scala `List` into a (non-null, mutable) Java `List`
    */
  def asJavaListOrEmpty[E](s: List[E]): JList[E] = new JArrayList[E](orEmpty(s).asJava)

  /**
    * Convert a (possibly null) Scala `Set` into a (non-null, mutable) Java `Set`
    */
  def asJavaSetOrEmpty[E](s: Set[E]): JSet[E] = new JHashSet[E](orEmpty(s).asJava)

  /**
    * Convert a (possibly null) Scala `Map` into a (non-null, mutable) Java `Map`
    */
  def asJavaMapOrEmpty[K, V](s: Map[K, V]): JMap[K, V] = new JHashMap[K, V](orEmpty(s).asJava)

  /* ---------------------------------------------
  * Implicit null-safe Java -> Scala converters
  * ---------------------------------------------*/

  /**
    * Adds an `asScalaOrEmpty` method that implicitly converts a (possibly null) Java `List` into a (non-null) Scala `List`
    */
  implicit def asScalaListOrEmptyConverter[E](j: JList[E]): AsScalaOrEmpty[List[E]] = new AsScalaOrEmpty(asScalaListOrEmpty(j))

  /**
    * Adds an `asScalaOrEmpty` method that implicitly converts a (possibly null) Java `Set` into a (non-null) Scala `Set`
    */
  implicit def asScalaSetOrEmptyConverter[E](j: JSet[E]): AsScalaOrEmpty[Set[E]] = new AsScalaOrEmpty(asScalaSetOrEmpty(j))

  /**
    * Adds an `asScalaOrEmpty` method that implicitly converts a (possibly null) Java `Map` into a (non-null) Scala `Map`
    */
  implicit def asScalaMapOrEmptyConverter[K, V](j: JMap[K, V]): AsScalaOrEmpty[Map[K, V]] = new AsScalaOrEmpty(asScalaMapOrEmpty(j))

  /* ---------------------------------------------
  * Implicit null-safe Scala -> (mutable) Java converters
  * ---------------------------------------------*/

  /**
    * Adds an `asJavaOrEmpty` method that implicitly converts a (possibly null) Scala `List` into a (non-null, mutable) Java `List`
    */
  implicit def asJavaListOrEmptyConverter[E](s: List[E]): AsJavaOrEmpty[JList[E]] = new AsJavaOrEmpty(asJavaListOrEmpty(s))

  /**
    * Adds an `asJavaOrEmpty` method that implicitly converts a (possibly null) Scala `Set` into a (non-null, mutable) Java `Set`
    */
  implicit def asJavaSetOrEmptyConverter[E](s: Set[E]): AsJavaOrEmpty[JSet[E]] = new AsJavaOrEmpty(asJavaSetOrEmpty(s))

  /**
    * Adds an `asJavaOrEmpty` method that implicitly converts a (possibly null) Scala `Map` into a (non-null, mutable) Java `Map`
    */
  implicit def asJavaMapOrEmptyConverter[K, V](s: Map[K, V]): AsJavaOrEmpty[JMap[K, V]] = new AsJavaOrEmpty(asJavaMapOrEmpty(s))

  /* ---------------------------------------------
  * Misc.
  * ---------------------------------------------*/

  /**
    * Type alias for partial functions that can be applied to `Map` entries
    *
    * @tparam K the key type
    * @tparam V the value type
    */
  type EntryMapper[K, V] = PartialFunction[(K, V), (K, V)]

  /**
    * Returns an EntryMapper produced by applying the given mapper function to the key of each `Map` entry
    *
    * @param f the mapper function
    * @tparam K the key type
    * @tparam V the value type
    * @return the EntryMapper
    */
  def keyMapper[K, V](f: K => K): EntryMapper[K, V] = {
    case (k, v) => (f(k), v)
  }


  //=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  //
  // Convenience helpers to map key/value pairs for persistence / retrieval
  // placed here as it is used in multiple locations

  private val objectKeyEncoder: EntryMapper[String, String] = keyMapper(_.replace(DOT, AT))
  private val objectKeyDecoder: EntryMapper[String, String] = keyMapper(_.replace(AT, DOT))

  def objectKeyEncoded(incoming: Map[String, String]): JMap[String, String] = {
    orEmpty(incoming).map(objectKeyEncoder).asJavaOrEmpty
  }

  def objectKeyDecoded(incoming: JMap[String, String]): Map[String, String] = {
    asScalaMapOrEmpty(incoming).map(objectKeyDecoder)
  }

  //=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  /**
    * Filter function that only keeps null values
    */
  def isNull[T](t: T): Boolean = Option(t).isEmpty

  /**
    * Filter function that only keeps non-null values
    */
  def notNull[T](t: T): Boolean = !isNull(t)

  def toList[T](t: T): List[T] = toList(Option(t))

  def toList[T](t: Option[T]): List[T] = t.map(List(_)).getOrElse(List.empty)



  //=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  /**
   * Empty the given queue into a scala List, up to the max requested size
   *
   * @param queue   the queue to empty
   * @param maxSize the maximum number of elements to pull from the queue (default is no limit)
   * @return the list of records
   */
  def drainQueue[T](queue: java.util.Queue[T], maxSize: Int = Int.MaxValue): List[T] = {
    if (queue.isEmpty) {
      List.empty
    }
    else {
      // create a mutable buffer
      val listBuffer = scala.collection.mutable.ListBuffer[T]()
      var count = 0;

      do {
        val h = queue.poll()
        if (h != null) {
          listBuffer += h
          count += 1;
        }
      } while (!queue.isEmpty && count < maxSize)

      listBuffer.toList
    }
  }
}
