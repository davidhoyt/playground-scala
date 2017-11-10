package com.github.davidhoyt.playground.myspray

import net.liftweb.json._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

class FlattenSerializer[A <: Product : TypeTag : ClassTag] extends Serializer[A] {
  implicit val man = manifest[A]

  val mirror = runtimeMirror(getClass.getClassLoader)

  val members = typeOf[A].members.sorted.collect {
    case m: MethodSymbol if m.isCaseAccessor => m
  }

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), A] = ???

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    def cleanJson(json: JValue): JValue =
      Extraction.decompose(json) remove {
        case JField(n, _) if n contains "$" => true
        case _ => false
      }

    def flatten(lst: List[JField], json: JValue): List[JField] =
      json match {
        case JField(_, JObject(entries)) =>
          entries.foldLeft(lst)(_ ++ flatten(List.empty, _))
        case JObject(entries)  =>
          entries.foldLeft(lst)(_ ++ flatten(List.empty, _))
        case field @ JField(_, _) =>
          lst :+ field
        case _ =>
          lst
      }

    {
      case instance: Product if man.runtimeClass.isAssignableFrom(instance.getClass) =>
        val reflected = mirror.reflect(instance)
        val flattened = members.foldLeft(List.empty[JField]) { (lst, symbol) =>
          flatten(lst, JField(symbol.name.decodedName.toString, Extraction.decompose(reflected.reflectMethod(symbol)())))
        }
        JObject(flattened)
    }
  }
}

object Main extends App {
  case class Foo(fooField1: String, fooField2: Bar, fooField3: Int)
  case class Bar(barField1: String, barField2: Int, barField3: Baz)
  case class Baz(bazField1: Int)

  case class Blah(field1: Int)

  val foo = Foo("foo-field1", Bar("bar-field1", 0, Baz(1)), 2)

  implicit val formats = DefaultFormats + new FlattenSerializer[Foo]()

  println(pretty(JsonAST.render(Extraction.decompose(Blah(123)))))
}
