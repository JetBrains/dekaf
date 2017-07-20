package org.jetbrains.dekaf.sql


import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


/**
 * @author Leonid Bushuev from JetBrains
 */
class SqlScriptBuilderTest {


    @Test
    fun parse_1() {
        val commandText = "select * from dual"
        val script = build(commandText)

        assertThat(script.hasStatements()).isTrue()
        assertThat(script.statements).hasSize(1)
        val statement = script.statements[0]
        assertThat(statement.sourceText).isEqualTo(commandText)
        assertThat(statement.row).isEqualTo(1)
    }


    @Test
    fun parse_1x() {
        val commandText = "select something\n" + "from some_table\n"
        val statement = parseScriptWithOneStatement(commandText)
        assertThat(statement.sourceText).contains("select something", "from some_table")
    }


    @Test
    fun parse_1_create_view() {
        val commandText = "create view my_view \n" +
                "as                  \n" +
                "select *            \n" +
                "from some_table     \n"
        val statement = parseScriptWithOneStatement(commandText)
        assertThat(statement.sourceText).contains("create view", "from some_table")
    }

    @Test
    fun parse_1_PL_block() {
        val commandText = "create or replace procedure My_Proc as \n" +
                "declare X natural := 2;                \n" +
                "begin                                  \n" +
                "    dbms_Output.put_line(X);           \n" +
                "end;                                   \n"
        val statement = parseScriptWithOneStatement(commandText)
        assertThat(statement.sourceText).contains("create or replace procedure My_Proc",
                                                  "declare X natural := 2;",
                                                  "begin",
                                                  "dbms_Output.put_line(X);",
                                                  "end;")
    }


    @Test
    fun parse_2_in_2_lines() {
        val text = "create table X;\n drop table X"
        val script = build(text)

        assertThat(script.count()).isEqualTo(2)
        assertThat(script.statements[0].sourceText).isEqualTo("create table X")
        assertThat(script.statements[1].sourceText).isEqualTo("drop table X")
    }


    @Test
    fun parse_singleLineComment() {
        val text = "-- a single line comment \n" + "do something"
        val script = build(text)

        assertThat(script.count()).isEqualTo(1)
        assertThat(script.statements[0].sourceText).isEqualTo("do something")
    }


    @Test
    fun parse_singleLineComment_preserveOracleHint() {
        val text = "select --+index(i) \n" +
                "      all fields   \n" +
                "from my_table      \n"
        val script = build(text)

        assertThat(script.hasStatements()).isTrue()
        assertThat(script.count()).isEqualTo(1)
        val queryText = script.statements[0].sourceText
        assertThat(queryText).contains("--+index(i)")
    }


    @Test
    fun parse_multiLineComment_1() {
        val text = "/* a multi-line comment*/\n" + "do something"
        val script = build(text)

        assertThat(script.count()).isEqualTo(1)
        assertThat(script.statements[0].sourceText).isEqualTo("do something")
    }


    @Test
    fun parse_multiLineComment_3() {
        val text = "/*                      \n" +
                " * a multi-line comment \n" +
                " */                     \n" +
                "do something            \n"
        val script = build(text)

        assertThat(script.count()).isEqualTo(1)
        assertThat(script.statements[0].sourceText).isEqualTo("do something")
    }


    @Test
    fun parse_multiLineComment_preserveOracleHint() {
        val text = "select /*+index(i)*/ * \n" + "from table             \n"
        val script = build(text)

        assertThat(script.count()).isEqualTo(1)
        val queryText = script.statements[0].sourceText
        assertThat(queryText).contains("/*+index(i)*/")
    }


    @Test
    fun parse_OraclePackage() {
        val script = build(ORACLE_PKG1)

        assertThat(script.statements).hasSize(2)
        assertThat(script.statements[0].sourceText).startsWith("create package pkg1")
                .endsWith("end pkg1;")
        assertThat(script.statements[1].sourceText).startsWith("create package body")
                .endsWith("end;")
    }

    @Test
    fun parse_OraclePackage_lines() {
        val script = build(ORACLE_PKG1)

        assertThat(script.statements).hasSize(2)
                .extracting("row")
                .containsSequence(1, 9)
    }

    companion object {

        private fun parseScriptWithOneStatement(commandText: String): SqlStatement {
            val script = build(commandText)

            val statements = script.statements
            assertThat(statements).hasSize(1)
            return statements[0]
        }

        private val ORACLE_PKG1 =
                "create package pkg1 is            \n" + //  line 1
                "                                  \n" + //
                "  procedure pro1 (n natural);     \n" + //
                "  function fun1 return number;    \n" + //
                "                                  \n" + //
                "end pkg1;                         \n" + //
                "/                                 \n" + //
                "                                  \n" + //
                "create package body pkg1 is       \n" + //  line 9
                "                                  \n" + //
                "  procedure pro1(n natural) is    \n" + //
                "  begin                           \n" + //
                "    null;                         \n" + //
                "  end;                            \n" + //
                "                                  \n" + //
                "  function fun1 return number is  \n" + //
                "  begin                           \n" + //
                "    return 44 / 13;               \n" + //
                "  end;                            \n" + //
                "                                  \n" + //
                "end;                              \n" + //
                "/                                 \n"   //


        private fun build(text: String): SqlScript {
            val b = SqlScriptBuilder()
            b.parse(text)
            return b.build()
        }
    }


}
