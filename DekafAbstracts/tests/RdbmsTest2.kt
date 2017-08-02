package org.jetbrains.dekaf

import org.jetbrains.dekaf.assertions.expectedClass
import org.jetbrains.dekaf.assertions.expectedNotSameAs
import org.jetbrains.dekaf.assertions.expectedSameAs
import org.junit.jupiter.api.Test
import java.io.*


class RdbmsTest2 {

    @Test
    fun dedulplicate_on_create() {
        val str1 = java.lang.String("FantasticDB") as String
        val str2 = java.lang.String("FantasticDB") as String
        str2 expectedNotSameAs str1
        val rdbms1 = Rdbms.of(str1)
        val rdbms2 = Rdbms.of(str2)
        rdbms2 expectedSameAs rdbms1
    }


    internal class JustAClass(val rdbms: Rdbms) : Serializable

    @Test
    @Throws(IOException::class, ClassNotFoundException::class)
    fun serialization() {
        val c1 = JustAClass(Rdbms.of("MicroDB"))

        val os = ByteArrayOutputStream()
        val oos = ObjectOutputStream(os)
        oos.writeObject(c1)

        val ois = ObjectInputStream(ByteArrayInputStream(os.toByteArray()))
        val gotObject = ois.readObject()
        gotObject expectedClass JustAClass::class.java

        val c2 = gotObject as JustAClass
        c2.rdbms expectedSameAs c1.rdbms
    }

}