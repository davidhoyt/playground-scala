package com.github.davidhoyt.playground.learn

object ForComprehensions extends App {
  case class Book(title: String, authors: List[String])

  val books = List(
    Book(title = "Book 1", authors = List("Odersky", "Hutch")),
    Book(title = "Book 2", authors = List("Eklund"))
  )

  val allAuthors: List[String] =
    for {
      b: Book <- books: List[Book]
      a: String <- b.authors: List[String]
      if a.contains("u")
    } yield a.take(3)

  val allAuthorsDesugared: List[String] =
    books flatMap { b =>
      val filtered = b.authors.filter { a =>
        a.contains("u")
      }
      filtered.map { a =>
        a.take(3)
      }
    }

  def flatMapForListOfBooks[A](lst: List[Book], fn: Book => List[A]): List[A] = ???
  def mapForListOfAuthors[A](authors: List[String], fn: String => A): List[A] = ???

  println(allAuthors)
  println(allAuthorsDesugared)

  val bookTitles: List[String] =
    for (b <- books)
      yield b.title

  val bookTitlesDesugared: List[String ] =
    books map { b =>
      b.title
    }

  def mapStringToInt(l: List[String], fn: String => Int): List[Int] = {
//    import scala.collection.mutable
//    val lst = mutable.ListBuffer.newBuilder[Int]
//    for (elem <- l)
//      lst += fn(elem)
//    lst.result().toList
//    l map fn
//    l.foldLeft(List.empty[Int]) {
//      case (lst, current) => lst :+ fn(current)
//    }

//    mapStringToX[Int](l, fn)
    mapXToY[String, Int](l, fn)
  }

  def mapStringToX[B](l: List[String], fn: String => B): List[B] =
    mapXToY[String, B](l, fn)

  def mapXToY[A, B](l: List[A], fn: A => B): List[B] =
    l.foldLeft(List.empty[B]) {
      case (lst, current) => lst :+ fn(current)
    }

  def flatMapStringToInt(l: List[String], fn: String => List[Int]): List[Int] = {
    l.foldLeft(List.empty[List[Int]]) {
      case (lst, current) => lst :+ fn(current)
    }.flatten
  }

  println(mapStringToInt(List("Hi", "there"), (x: String) => x.length))
  println(flatMapStringToInt(List("Hi", "there"), (x: String) => List(0, x.length)))

  val given: Option[String] = Option("test")
  val given2: Option[Int] = Some(10)

  val myOpt: Option[(Int, Int)] =
    for {
      g <- given
      h = g.length
      g2 <- given2
    } yield (h, g2 * 2)

  println(myOpt)

  val givenDesugared: Option[(Int, Int)] = given flatMap{ g =>
    val h = g.length
    given2 map{ g2 =>
      (h, g2 * 2)
    }
  }

  println(givenDesugared)
  //flatmap
  //map
  //no filter

}
