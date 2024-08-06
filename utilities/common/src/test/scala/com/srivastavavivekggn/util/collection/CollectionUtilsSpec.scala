package com.srivastavavivekggn.scala.util.collection

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.TypeAlias._
import com.srivastavavivekggn.scala.util.collection.CollectionUtils._

import scala.jdk.CollectionConverters._

class CollectionUtilsSpec extends BaseUtilSpec {

  behavior of "diff"

  it should "return the differences between 2 lists (one has diffs)" in {
    val left = List(1, 2, 3)
    val right = List(1, 2)
    val result = CollectionUtils.diff(left, right)
    assert(result.equals(List(3)))

    // ensure empty list on one side returns full list on other side
    assert(CollectionUtils.diff(left, List.empty).equals(List(1, 2, 3)))
  }

  it should "return the differences between 2 lists (both have diffs)" in {
    val left = List(1, 2, 3)
    val right = List(3, 4, 5)
    val result = CollectionUtils.diff(left, right)
    assert(result.equals(List(1, 2, 4, 5)))
  }

  it should "return an empty list when there are no diffs" in {
    val left = List(1, 2, 3)
    val result = CollectionUtils.diff(left, left)
    assert(result.isEmpty)
  }

  /* ---------------------------------------------
  * Java -> Scala
  * ---------------------------------------------*/

  behavior of "asScalaOrEmpty"

  /* ---------------------------------------------
  * Java List -> Scala List
  * ---------------------------------------------*/

  it should "return an empty Scala `List` when called on a null Java `List`" in {
    nullJavaList.asScalaOrEmpty.isEmpty shouldBe true
  }

  it should "return an empty Scala `List` when called on an empty Java `List`" in {
    emptyJavaList.asScalaOrEmpty.isEmpty shouldBe true
  }

  it should "return the corresponding Scala `List` when called on a non-empty Java `List`" in {
    nonEmptyJavaList.asScalaOrEmpty shouldBe nonEmptyJavaList.asScala
  }

  /* ---------------------------------------------
  * Java Set -> Scala Set
  * ---------------------------------------------*/

  it should "return an empty Scala `Set` when called on a null Java `Set`" in {
    nullJavaSet.asScalaOrEmpty.isEmpty shouldBe true
  }

  it should "return an empty Scala `Set` when called on an empty Java `Set`" in {
    emptyJavaSet.asScalaOrEmpty.isEmpty shouldBe true
  }

  it should "return the corresponding Scala `Set` when called on a non-empty Java `Set`" in {
    nonEmptyJavaSet.asScalaOrEmpty shouldBe nonEmptyJavaSet.asScala
  }

  /* ---------------------------------------------
  * Java Map -> Scala Map
  * ---------------------------------------------*/

  it should "return an empty Scala `Map` when called on a null Java `Map`" in {
    nullJavaMap.asScalaOrEmpty.isEmpty shouldBe true
  }

  it should "return an empty Scala `Map` when called on an empty Java `Map`" in {
    emptyJavaMap.asScalaOrEmpty.isEmpty shouldBe true
  }

  it should "return the corresponding Scala `Map` when called on a non-empty Java `Map`" in {
    nonEmptyJavaMap.asScalaOrEmpty shouldBe nonEmptyJavaMap.asScala
  }

  /* ---------------------------------------------
  * Scala -> Java
  * ---------------------------------------------*/

  behavior of "asJavaOrEmpty"

  /* ---------------------------------------------
  * Scala List -> Java List
  * ---------------------------------------------*/

  it should "return a mutable Java `List` when called on a Scala `List`" in {
    noException should be thrownBy nonEmptyScalaList.asJavaOrEmpty.add(4)
  }

  it should "return an empty Java `List` when called on a null Scala `List`" in {
    nullScalaList.asJavaOrEmpty.isEmpty shouldBe true
  }

  it should "return an empty Java `List` when called on an empty Scala `List`" in {
    emptyScalaList.asJavaOrEmpty.isEmpty shouldBe true
  }

  it should "return the corresponding Java `List` when called on a non-empty Scala `List`" in {
    nonEmptyScalaList.asJavaOrEmpty shouldBe nonEmptyScalaList.asJava
  }

  /* ---------------------------------------------
  * Scala Set -> Java Set
  * ---------------------------------------------*/

  it should "return a mutable Java `Set` when called on a Scala `Set`" in {
    noException should be thrownBy nonEmptyScalaSet.asJavaOrEmpty.add(4)
  }

  it should "return an empty Java `Set` when called on a null Scala `Set`" in {
    nullScalaSet.asJavaOrEmpty.isEmpty shouldBe true
  }

  it should "return an empty Java `Set` when called on an empty Scala `Set`" in {
    emptyScalaSet.asJavaOrEmpty.isEmpty shouldBe true
  }

  it should "return the corresponding Java `Set` when called on a non-empty Scala `Set`" in {
    nonEmptyScalaSet.asJavaOrEmpty shouldBe nonEmptyScalaSet.asJava
  }

  /* ---------------------------------------------
  * Scala Map -> Java Map
  * ---------------------------------------------*/

  it should "return a mutable Java `Map` when called on a Scala `Map`" in {
    noException should be thrownBy nonEmptyScalaMap.asJavaOrEmpty.put("4", 4)
  }

  it should "return an empty Java `Map` when called on a null Scala `Map`" in {
    nullScalaMap.asJavaOrEmpty.isEmpty shouldBe true
  }

  it should "return an empty Java `Map` when called on an empty Scala `Map`" in {
    emptyScalaMap.asJavaOrEmpty.isEmpty shouldBe true
  }

  it should "return the corresponding Java `Map` when called on a non-empty Scala `Map`" in {
    nonEmptyScalaMap.asJavaOrEmpty shouldBe nonEmptyScalaMap.asJava
  }

  /* ---------------------------------------------
  * Helpers
  * ---------------------------------------------*/

  def nullScalaList: List[Int] = null
  def nullScalaSet: Set[Int] = null
  def nullScalaMap: Map[String, Int] = null

  def emptyScalaList: List[Int] = List.empty
  def emptyScalaSet: Set[Int] = Set.empty
  def emptyScalaMap: Map[String, Int] = Map.empty

  def nonEmptyScalaList: List[Int] = List(1, 2, 3)
  def nonEmptyScalaSet: Set[Int] = Set(1, 2, 3)
  def nonEmptyScalaMap: Map[String, Int] = Map("1" -> 1, "2" -> 2, "3" -> 3)

  def nullJavaList: JList[Int] = null
  def nullJavaSet: JSet[Int] = null
  def nullJavaMap: JMap[String, Int] = null

  def emptyJavaList: JList[Int] = new JArrayList[Int]()
  def emptyJavaSet: JSet[Int] = new JHashSet[Int]()
  def emptyJavaMap: JMap[String, Int] = new JHashMap[String, Int]()

  def nonEmptyJavaList: JList[Int] = new JArrayList[Int](nonEmptyScalaList.asJava)
  def nonEmptyJavaSet: JSet[Int] = new JHashSet[Int](nonEmptyScalaSet.asJava)
  def nonEmptyJavaMap: JMap[String, Int] = new JHashMap[String, Int](nonEmptyScalaMap.asJava)
}
