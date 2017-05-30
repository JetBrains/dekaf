package org.jetbrains.dekaf.crazy


object Inspiration {

    val primaryColumns: Map<String,String> = mapOf(
            "id"   to "number(N)",
            "name" to "varchar(V)",
            "code" to "varchar(V)"
    )

    val secondaryColumns: Map<String,String> = mapOf(
            "nr"   to "number(N)",
            "line" to "number(N)",
            "pos"  to "number(N)",
            "name" to "varchar(V)",
            "code" to "varchar(V)"
    )

    val auxiliaryTypes: List<String> = listOf(
            "number(N)",
            "varchar(W)",
            "char(C)",
            "date",
            "raw(W)"
    )


}