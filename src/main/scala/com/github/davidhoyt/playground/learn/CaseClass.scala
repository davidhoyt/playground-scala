package com.github.davidhoyt.playground.learn

import java.util.Objects

object CaseClass {
  //Desugared case class
  class Book(val title: String, val authors: List[String]) {
    override def hashCode(): Int =
      super.hashCode() + Objects.hash(title, authors)

    override def equals(obj: scala.Any): Boolean =
      obj match {
        case b: Book => b.title == title && b.authors == authors
        case _ => false
      }

    override def toString: String =
      s"Book($title, $authors)"
  }

  object Book {
    def apply(title: String, authors: List[String]): Book =
      new Book(title, authors)
  }

  //case class Book(title: String, authors: List[String])
}
