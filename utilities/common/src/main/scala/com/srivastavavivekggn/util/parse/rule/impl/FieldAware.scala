package com.srivastavavivekggn.scala.util.parse.rule.impl

import fastparse._
import fastparse.NoWhitespace.noWhitespaceImplicit

trait FieldAware {

  def fields: Seq[String]

  def fieldToParser[_: P](s: String): P[Unit] = s match {
    case x: String if x.endsWith(".*") => P(x.dropRight(2) ~ CharsWhile(_ != ' ').!)
    case x: String => P(x)
  }

  def fieldsParser[_: P]: P[String] = (fields.toList match {
    case Nil => fieldToParser("")
    case head :: Nil => fieldToParser(head)
    case head :: tail => tail.foldLeft(fieldToParser(head))((h, f) => {
      P(h | fieldToParser(f))
    })
  }).!
}
