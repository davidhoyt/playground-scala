//package com.github.davidhoyt.playground.miscellaneous
//
//import java.nio.ByteBuffer
//
//import shapeless._
//
//trait OhcCodec[T]
//  extends OhcEncoder[T]
//    with OhcDecoder[T]
//
//trait OhcEncoder[-T] {
//  def ohcEncode(given: T, buffer: ByteBuffer): Unit
//  def ohcEncodedSize(given: T): Int
//}
//
//trait OhcDecoder[+T] {
//  def ohcDecode(buffer: ByteBuffer): T
//}
//
//object OhcCodec
//  extends Serializers
//    with LowPriorityImplicitsOhcCodec
//
//trait Serializers
//  extends ShapelessSerializers
//
//trait LowPriorityImplicitsOhcCodec {
//  implicit def toOhcCodec[T](implicit encoder: OhcEncoder[T], decoder: OhcDecoder[T]): OhcCodec[T] = new OhcCodec[T] {
//    override def ohcEncode(given: T, buffer: ByteBuffer): Unit = encoder.ohcEncode(given, buffer)
//    override def ohcEncodedSize(given: T): Int = encoder.ohcEncodedSize(given)
//    override def ohcDecode(buffer: ByteBuffer): T = decoder.ohcDecode(buffer)
//  }
//}
//
//trait StandardSerializers {
//  import java.nio.ByteBuffer
//  import java.nio.charset.StandardCharsets
//  import scala.collection.generic.CanBuildFrom
//
//  implicit object booleanCodec extends OhcCodec[Boolean] {
//    override def ohcEncode(b: Boolean, buf: ByteBuffer): Unit = {
//      buf.put((if (b) 1 else 0).toByte)
//      ()
//    }
//
//    override def ohcDecode(buf: ByteBuffer): Boolean =
//      buf.get == 1
//
//    override def ohcEncodedSize(b: Boolean): Int =
//      1
//  }
//
//  implicit object byteCodec extends OhcCodec[Byte] {
//    override def ohcEncode(b: Byte, buf: ByteBuffer): Unit = {
//      buf.put(b)
//      ()
//    }
//
//    override def ohcDecode(buf: ByteBuffer): Byte =
//      buf.get
//
//    override def ohcEncodedSize(b: Byte): Int =
//      1
//  }
//
//  implicit object shortCodec extends OhcCodec[Short] {
//    override def ohcEncode(s: Short, buf: ByteBuffer): Unit = {
//      buf.putShort(s)
//      ()
//    }
//
//    override def ohcDecode(buf: ByteBuffer): Short =
//      buf.getShort
//
//    override def ohcEncodedSize(s: Short): Int =
//      java.lang.Short.BYTES
//  }
//
//  implicit object intCodec extends OhcCodec[Int] {
//    override def ohcEncode(i: Int, buf: ByteBuffer): Unit = {
//      buf.putInt(i)
//      ()
//    }
//
//    override def ohcDecode(buf: ByteBuffer): Int =
//      buf.getInt
//
//    override def ohcEncodedSize(i: Int): Int =
//      Integer.BYTES
//  }
//
//  implicit object longCodec extends OhcCodec[Long] {
//    override def ohcEncode(l: Long, buf: ByteBuffer): Unit = {
//      buf.putLong(l)
//      ()
//    }
//
//    override def ohcDecode(buf: ByteBuffer): Long =
//      buf.getLong
//
//    override def ohcEncodedSize(l: Long): Int =
//      java.lang.Long.BYTES
//  }
//
//  implicit object floatCodec extends OhcCodec[Float] {
//    override def ohcEncode(f: Float, buf: ByteBuffer): Unit = {
//      buf.putFloat(f)
//      ()
//    }
//
//    override def ohcDecode(buf: ByteBuffer): Float =
//      buf.getFloat
//
//    override def ohcEncodedSize(f: Float): Int =
//      java.lang.Float.BYTES
//  }
//
//  implicit object doubleCodec extends OhcCodec[Double] {
//    override def ohcEncode(d: Double, buf: ByteBuffer): Unit = {
//      buf.putDouble(d)
//      ()
//    }
//
//    override def ohcDecode(buf: ByteBuffer): Double =
//      buf.getDouble
//
//    override def ohcEncodedSize(d: Double): Int =
//      java.lang.Double.BYTES
//  }
//
//  implicit object charCodec extends OhcCodec[Char] {
//    override def ohcEncode(c: Char, buf: ByteBuffer): Unit = {
//      buf.putChar(c)
//      ()
//    }
//
//    override def ohcDecode(buf: ByteBuffer): Char =
//      buf.getChar
//
//    override def ohcEncodedSize(c: Char): Int =
//      java.lang.Character.BYTES
//  }
//
//  implicit object stringCodec extends OhcCodec[String] {
//    override def ohcEncode(s: String, buf: ByteBuffer): Unit = {
//      val bytes = s.getBytes(StandardCharsets.UTF_8)
//      buf.put(((bytes.length >>> 8) & 0xFF).toByte)
//      buf.put(((bytes.length >>> 0) & 0xFF).toByte)
//      buf.put(bytes)
//      ()
//    }
//
//    override def ohcDecode(buf: ByteBuffer): String = {
//      val len = ((buf.get() & 0xff) << 8) + ((buf.get() & 0xff) << 0)
//      val bytes = new Array[Byte](len)
//      buf.get(bytes)
//      new String(bytes, StandardCharsets.UTF_8)
//    }
//
//    override def ohcEncodedSize(s: String): Int =
//      2 + writeUtfLen(s)
//
//    def writeUtfLen(s: String): Int = {
//      val strlen = s.length
//      var utflen = 0
//      var c = 0
//      var i = 0
//
//      while (i < strlen) {
//        c = s.charAt(i)
//        if ((c >= 0x0001) && (c <= 0x007F))
//            utflen += 1
//        else if (c > 0x07FF)
//            utflen += 3
//        else
//            utflen += 2
//        i += 1
//      }
//
//      utflen + 2
//    }
//  }
//
//  implicit def optionCodec[A](implicit codecForA: OhcCodec[A]): OhcCodec[Option[A]] = new OhcCodec[Option[A]] {
//    override def ohcEncode(opt: Option[A], buf: ByteBuffer): Unit = {
//      booleanCodec.ohcEncode(opt.isDefined, buf)
//      if (opt.isDefined)
//        codecForA.ohcEncode(opt.get, buf)
//    }
//
//    override def ohcDecode(buf: ByteBuffer): Option[A] = {
//      val defined = booleanCodec.ohcDecode(buf)
//      if (defined)
//        Some(codecForA.ohcDecode(buf))
//      else
//        None
//    }
//
//    override def ohcEncodedSize(opt: Option[A]): Int =
//      1 + {
//        if (opt.isDefined)
//          codecForA.ohcEncodedSize(opt.get)
//        else
//          0
//      }
//  }
//
//  implicit def traversableCodec[S[_], T](implicit codecForA: OhcCodec[T], intCodec: OhcCodec[Int], is: collection.generic.IsTraversableLike[S[T]] { type A = T }, cbf: CanBuildFrom[Nothing, T, S[T]]): OhcCodec[S[T]] = new OhcCodec[S[T]] {
//    override def ohcEncode(given: S[T], buffer: ByteBuffer): Unit = {
//      val sa = is.conversion(given)
//      intCodec.ohcEncode(sa.size, buffer)
//      sa foreach (codecForA.ohcEncode(_, buffer))
//    }
//
//    override def ohcEncodedSize(given: S[T]): Int = {
//      val sa = is.conversion(given)
//      val size = sa.foldLeft(0) { (i, a) =>
//        i + codecForA.ohcEncodedSize(a)
//      }
//      intCodec.ohcEncodedSize(size) + size
//    }
//
//    override def ohcDecode(buffer: ByteBuffer): S[T] = {
//      val size = intCodec.ohcDecode(buffer)
//      val builder = cbf()
//      var i = 0
//      while (i < size) {
//        val entry = codecForA.ohcDecode(buffer)
//        builder += entry
//        i += 1
//      }
//      builder.result()
//    }
//  }
//
//  implicit def mapCodec[M[K0, +V0] <: Map[K0, V0], K, V](implicit codecForK: OhcCodec[K], codecForV: OhcCodec[V], intCodec: OhcCodec[Int], cbf: CanBuildFrom[Nothing, (K, V), M[K, V]]): OhcCodec[M[K, V]] = new OhcCodec[M[K, V]] {
//    override def ohcEncode(given: M[K, V], buffer: ByteBuffer): Unit = {
//      intCodec.ohcEncode(given.size, buffer)
//      given foreach { case (k, v) =>
//        codecForK.ohcEncode(k, buffer)
//        codecForV.ohcEncode(v, buffer)
//      }
//    }
//
//    override def ohcEncodedSize(given: M[K, V]): Int = {
//      val size = given.foldLeft(0) { case (i, (k, v)) =>
//        i + codecForK.ohcEncodedSize(k) + codecForV.ohcEncodedSize(v)
//      }
//      intCodec.ohcEncodedSize(size) + size
//    }
//
//    override def ohcDecode(buffer: ByteBuffer): M[K, V] = {
//      val size = intCodec.ohcDecode(buffer)
//      val builder = cbf()
//      var i = 0
//      while (i < size) {
//        val k = codecForK.ohcDecode(buffer)
//        val v = codecForV.ohcDecode(buffer)
//        builder += (k -> v)
//        i += 1
//      }
//      builder.result()
//    }
//  }
//}
//
//trait ShapelessSerializers extends StandardSerializers {
//  import java.nio.ByteBuffer
//  import shapeless._
//
//  sealed trait IndexedOhcCodec[C, N <: Nat] extends OhcCodec[C]
//
//  implicit def hnilCodec[N <: Nat] = new IndexedOhcCodec[HNil, N] {
//    override def ohcEncode(h: HNil, buf: ByteBuffer): Unit = ()
//    override def ohcDecode(buf: ByteBuffer): HNil = HNil
//    override def ohcEncodedSize(h: HNil): Int = 0
//  }
//
//  implicit def hconsCodec[H, T <: HList, N <: Nat](implicit
//                                                   headCodec: Lazy[OhcCodec[H]],
//                                                   tailCodec: Lazy[IndexedOhcCodec[T, Succ[N]]]
//                                                   ): IndexedOhcCodec[H :: T, N] = new IndexedOhcCodec[H :: T, N] {
//    override def ohcEncode(hlist: H :: T, buffer: ByteBuffer): Unit = {
//      headCodec.value.ohcEncode(hlist.head, buffer)
//      tailCodec.value.ohcEncode(hlist.tail, buffer)
//    }
//
//    override def ohcDecode(buffer: ByteBuffer): H :: T = {
//      val head = headCodec.value.ohcDecode(buffer)
//      val tail = tailCodec.value.ohcDecode(buffer)
//      head :: tail
//    }
//
//    override def ohcEncodedSize(hlist: H :: T): Int =
//      headCodec.value.ohcEncodedSize(hlist.head) +
//      tailCodec.value.ohcEncodedSize(hlist.tail)
//  }
//
//  implicit def cnilCodec[N <: Nat] = new IndexedOhcCodec[CNil, N] {
//    override def ohcEncode(c: CNil, buf: ByteBuffer): Unit = ()
//    override def ohcDecode(buf: ByteBuffer): CNil = ???
//    override def ohcEncodedSize(c: CNil): Int = 0
//  }
//
//  implicit def cconsCodec[L, R <: Coproduct, N <: Nat](implicit
//                                                       headCodec: Lazy[OhcCodec[L]],
//                                                       tailCodec: Lazy[IndexedOhcCodec[R, Succ[N]]],
//                                                       byteCodec: OhcCodec[Byte],
//                                                       toInt: ops.nat.ToInt[N]
//                                                       ): IndexedOhcCodec[L :+: R, N] = new IndexedOhcCodec[L :+: R, N] {
//
//    val index = toInt().toByte
//
//    override def ohcEncode(coproduct: L :+: R, buffer: ByteBuffer): Unit = coproduct match {
//      case Inl(l) =>
//        val serializer = headCodec.value
//        byteCodec.ohcEncode(index, buffer)
//        serializer.ohcEncode(l, buffer)
//      case Inr(r) =>
//        tailCodec.value.ohcEncode(r, buffer)
//    }
//
//    override def ohcDecode(buffer: ByteBuffer): L :+: R = {
//      buffer.mark()
//      val selectedIndex = byteCodec.ohcDecode(buffer)
//      val codec = headCodec.value
//
//      if (selectedIndex == index) {
//        val value = codec.ohcDecode(buffer)
//        Inl[L, R](value)
//      } else {
//        buffer.reset()
//        Inr[L, R](tailCodec.value.ohcDecode(buffer))
//      }
//    }
//
//    override def ohcEncodedSize(coproduct: L :+: R): Int = coproduct match {
//      case Inl(h) => byteCodec.ohcEncodedSize(index) + headCodec.value.ohcEncodedSize(h)
//      case Inr(t) => tailCodec.value.ohcEncodedSize(t)
//    }
//  }
//
//  implicit def materializeCodec[A, C](implicit
//                                      gen: Generic.Aux[A, C],
//                                      reprSerializer: Lazy[IndexedOhcCodec[C, _0]]
//                                      ): IndexedOhcCodec[A, _0] = new IndexedOhcCodec[A, _0] {
//
//    override def ohcEncode(instance: A, buffer: ByteBuffer): Unit =
//      reprSerializer.value.ohcEncode(gen.to(instance), buffer)
//
//    override def ohcDecode(buffer: ByteBuffer): A =
//      gen.from(reprSerializer.value.ohcDecode(buffer))
//
//    override def ohcEncodedSize(instance: A): Int =
//      reprSerializer.value.ohcEncodedSize(gen.to(instance))
//  }
//}
//
//object TestTypes {
//  // Has to be in separate module due to SI-7046:
//  //   https://issues.scala-lang.org/browse/SI-7046
//
//  case class MyProduct(fieldA: String, fieldB: Int)
//
//  sealed trait MyAdt
//  case object A extends MyAdt
//  case object B extends MyAdt
//  case class C(field1: Int) extends MyAdt
//}
//
//object AutoTypeClassDerivation extends App {
//  import TestTypes._
//  import OhcCodec._
//
//  val toInt0 = implicitly[ops.nat.ToInt[_0]]
//  val toInt1 = implicitly[ops.nat.ToInt[Succ[_0]]]
//
//  val genericProduct = Generic.materialize[MyProduct, String :: Int :: HNil]
//  val genericAdt = Generic.materialize[MyAdt, A.type :+: B.type :+: C :+: CNil]
//
//  val adtAsCoproduct0 = genericAdt.to(A)
//  val adtAsCoproduct1 = genericAdt.to(B)
//  val adtAsCoproduct2 = genericAdt.to(C(123))
//
//  val hconsStep2 = hnilCodec[Succ[Succ[_0]]]
//  val hconsStep1 = hconsCodec[Int, HNil, Succ[_0]]
//  val hconsStep0 = hconsCodec[String, Int :: HNil, _0]
//
//  val productImplicitlyMaterialized = implicitly[OhcCodec[MyProduct]]
//
//  val cconsStep2 = cnilCodec[Succ[Succ[_0]]]
//  val cconsStep1 = cconsCodec[String, CNil, Succ[_0]]
//  val cconsStep0 = cconsCodec[String, Int :+: CNil, _0]
//
//  val indexedCConsStep0 = implicitly[IndexedOhcCodec[CNil, _0]]
//  val indexedCConsStep1 = implicitly[IndexedOhcCodec[Int :+: CNil, Succ[_0]]]
//
//  val adtCConsStep3 = cnilCodec[Succ[Succ[Succ[_0]]]]
//  val adtCConsStep2 = cconsCodec[C, CNil, Succ[Succ[_0]]]
//  val adtCConsStep1 = cconsCodec[B.type, C :+: CNil, Succ[_0]]
//  val adtCConsStep0 = cconsCodec[A.type, B.type :+: C :+: CNil, _0]
//
//  val adtMaterialized = materializeCodec[MyAdt, A.type :+: B.type :+: C :+: CNil]
//  val adtIndexed = implicitly[IndexedOhcCodec[MyAdt, _0]]
//
//  def validateCodec[T](value: T)(implicit codec: OhcCodec[T]): Unit = {
//    val size = codec.ohcEncodedSize(value)
//    val bb = ByteBuffer.allocate(size)
//    codec.ohcEncode(value, bb)
//    bb.rewind()
//    val result = codec.ohcDecode(bb)
//
//    require(result == value)
//  }
//
//  validateCodec[MyAdt](A)(adtMaterialized)
//  validateCodec[MyAdt](B)(adtMaterialized)
//  validateCodec[MyAdt](C(123))(adtMaterialized)
//
//  val adtImplicitlyMaterialized = implicitly[OhcCodec[MyAdt]]
//
//  validateCodec[MyAdt](A)(adtImplicitlyMaterialized)
//  validateCodec[MyAdt](B)(adtImplicitlyMaterialized)
//  validateCodec[MyAdt](C(123))(adtImplicitlyMaterialized)
//}
