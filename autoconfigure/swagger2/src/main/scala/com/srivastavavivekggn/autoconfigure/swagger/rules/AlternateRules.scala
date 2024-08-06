package com.srivastavavivekggn.springboot.autoconfigure.swagger.rules

import java.lang.reflect.{Type, WildcardType}

import com.fasterxml.classmate.{ResolvedType, TypeResolver}
import com.srivastavavivekggn.scala.util.TypeAlias.{JList, JSet}
import springfox.documentation.schema.{AlternateTypeRule, AlternateTypeRules}

import scala.concurrent.Future

object AlternateRules {

  // simple typeresolver instance
  val typeResolver = new TypeResolver

  // wildcard type
  val wildcard = classOf[WildcardType]


  // simple parameterized types
  private val parameterizedTypes = Seq(
    classOf[Option[_]],
    classOf[Future[_]]
  )

  // types that should resolve as a java.util.List
  private val listTypes = Seq(
    classOf[List[_]],
    classOf[Seq[_]]
  )

  // types that should resolve as a java.util.Set
  private val setTypes = Seq(classOf[Set[_]])

  // types that should resolve as a java.util.Map
  private val mapType = classOf[Map[_, _]]

  // simple concatenation of all types
  private val allTypes = parameterizedTypes ++ listTypes ++ setTypes


  /**
    * Generic type rule to handle all scala types we care about
    *
    * @param source   the source type (default is wildcard)
    * @param target   the target type (default is wildcard)
    * @param priority the rule priority (lower number is a higher priority)
    */
  case class ScalaTypeRule(source: Class[_] = wildcard,
                           target: Class[_] = wildcard,
                           priority: Int = AlternateTypeRules.GENERIC_SUBSTITUTION_RULE_ORDER)
    extends AlternateTypeRule(typeResolver.resolve(source), typeResolver.resolve(target), priority) {


    /**
      * Override the default appliesTo
      *
      * @param `type` the type we're checking against
      * @return true if this rule applies to the given type
      */
    override def appliesTo(`type`: ResolvedType): Boolean = {

      super.appliesTo(`type`) match {

        // if the source isn't wildcard and matches this type
        case true if (!source.equals(wildcard)) => true

        // if it doesn't seem to match but the type is actually an instance of source
        case false if (`type`.isInstanceOf(source)) => true

        // if its a map, we need to handle it differently
        case false if (`type`.isInstanceOf(mapType)) => true

        // otherwise
        case _ =>
          // see if the `type` matches any of our defined types
          val isScalaType = allTypes.find(`type`.isInstanceOf(_)).isDefined

          // unwrap any parameterized (or nested) types
          val unwrapped = unwrap(`type`)

          // if its a scala type, and the source is wildcard or matches our unwrapped type
          isScalaType && (source.equals(wildcard) || unwrapped.isInstanceOf(source))
      }
    }

    /**
      * Override the default alternate resolution
      *
      * @param `type` the type we're resolving
      * @return the resolved target type
      */
    override def alternateFor(`type`: ResolvedType): ResolvedType = {

      if (`type`.isInstanceOf(mapType)) {
        val tParams = `type`.getTypeParameters
        typeResolver.resolve(mapType, tParams.get(0), tParams.get(1))
      }
      else {
        // fully unwrap the type (any level of nested types)
        val alt = unwrap(`type`)

        target.equals(wildcard) match {
          case true => resolve(`type`.getErasedType, alt)
          case false => resolve(`type`.getErasedType, target)
        }
      }
    }

    /**
      * Recursively unwraps nested parameterized types
      *
      * @param t the type to unwrap
      * @return the unwrapped type
      */
    private def unwrap(t: ResolvedType): ResolvedType = {
      if (!t.getTypeParameters.isEmpty) {

        val p = t.getTypeParameters.get(0)

        if(!allTypes.contains(p.getErasedType) && !p.getTypeParameters.isEmpty) {
          p
        }
        else {
          unwrap(p)
        }
      }
      else {
        t
      }
    }

    /**
      * Properly resolve  the given erasedType into it's proper Java type if needed
      *
      * @param erasedType the erased type
      * @param paramType  the param/return type
      * @return the resolved type
      */
    private def resolve(erasedType: Class[_], paramType: Type): ResolvedType = {
      if (listTypes.contains(erasedType)) {
        typeResolver.resolve(classOf[JList[_]], paramType)
      }
      else if (setTypes.contains(erasedType)) {
        typeResolver.resolve(classOf[JSet[_]], paramType)
      }
      else {
        typeResolver.resolve(paramType)
      }
    }
  }

}
