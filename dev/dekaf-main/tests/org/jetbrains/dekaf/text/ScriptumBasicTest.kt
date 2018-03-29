package org.jetbrains.dekaf.text


import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.dekaf.core.layoutSingleValueOf
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test


/**
 * @author Leonid Bushuev from JetBrains
 */
@Tag("basic")
class ScriptumBasicTest {

    companion object {

        private var myScriptum: Scriptum? = null

        @BeforeAll @JvmStatic
        fun setUp() {
            myScriptum = Scriptum.of(ScriptumBasicTest::class.java)
        }
    }



    @Test
    fun theCommand() {
        check_TheCommand("TheCommand")
    }

    @Test
    fun theCommand_semicolon_sameString() {
        check_TheCommand("TheCommand_semicolon_sameString")
    }

    @Test
    fun theCommand_semicolon_sameString_2() {
        check_TheCommand("TheCommand_semicolon_sameString_2")
    }

    @Test
    fun theCommand_semicolon_nextString() {
        check_TheCommand("TheCommand_semicolon_nextString")
    }

    @Test
    fun theCommand_semicolon_nextString_2() {
        check_TheCommand("TheCommand_semicolon_nextString_2")
    }

    @Test
    fun theCommand_slash_nextString() {
        check_TheCommand("TheCommand_slash_nextString")
    }

    @Test
    fun theCommand_slash_nextString_2() {
        check_TheCommand("TheCommand_slash_nextString_2")
    }

    protected fun check_TheCommand(queryName: String) {
        val query = myScriptum!!.query(queryName, layoutSingleValueOf(Char::class.java))
        assertThat(query.sourceText).isEqualTo("The Command")
    }

    @Test
    fun theCommand_Oracle() {
        val scriptumForOracle = Scriptum.dialectOf(myScriptum!!, "Oracle")

        val command = scriptumForOracle.command("TheCommand")
        assertThat(command.sourceText).isEqualTo("The Oracle Command")
    }


    @Test
    fun basicCommand() {
        val command = myScriptum!!.command("BasicCommand")
        assertThat(command.sourceText)
                .startsWith("insert")
                .endsWith("values (1,2,3)")
    }

    @Test
    fun basicCommand_descriptionContainsFileName() {
        val command = myScriptum!!.command("BasicCommand")
        assertThat(command.description).contains(ScriptumBasicTest::class.java.simpleName)
        assertThat(command.toString()).contains(ScriptumBasicTest::class.java.simpleName)
    }

    @Test
    fun basicCommand_descriptionContainsFragmentName() {
        val command = myScriptum!!.command("BasicCommand")
        assertThat(command.description).contains("BasicCommand")
        assertThat(command.toString()).contains("BasicCommand")
    }


    @Test
    fun plBlock1() {
        val plb = myScriptum!!.command("PLBlock1")
        assertThat(plb.sourceText).contains("end;")
                .doesNotContain("/")
    }

    @Test
    fun postgresProcedure1() {
        val plb = myScriptum!!.command("PostgresProcedure1")
        assertThat(plb.sourceText).startsWith("create")
                .endsWith("language plpgsql")
    }


    @Test
    fun name_basic() {
        val command = myScriptum!!.command("TheCommand")
        assertThat(command.name).isEqualTo("TheCommand")
    }

    @Test
    fun name_adjustCase() {
        val command = myScriptum!!.command("THECOMMAND")
        assertThat(command.name).isEqualTo("TheCommand")
    }


    @Test
    fun fileWithOneCommand_text() {
        val scriptum1 = Scriptum.of(ScriptumBasicTest::class.java, "FileWithOneCommand")
        val text = scriptum1.getText("TheCommand")
        assertThat(text).isNotNull()
        assertThat(text.text).contains("select something", "from some_table")
    }

    @Test
    fun fileWithOneCommand_script() {
        val scriptum1 = Scriptum.of(ScriptumBasicTest::class.java, "FileWithOneCommand")
        val script = scriptum1.script("TheCommand")
        assertThat(script).isNotNull()
        assertThat(script.count()).isEqualTo(1)

        val statements = script.statements
        val statement = statements[0]
        assertThat(statement.sourceText).contains("select something", "from some_table")
    }

    @Test
    fun listFragmentNames_basic() {
        val scriptum = Scriptum.of(ScriptumBasicTest::class.java, "FileWithFragments")
        val names = scriptum.listFragmentNames()
        assertThat(names)
                .isNotEmpty
                .containsSequence("0", "first-fragment", "second-fragment")
    }

    @Test
    fun listFragments_basic() {
        val scriptum = Scriptum.of(ScriptumBasicTest::class.java, "FileWithFragments")
        val fragments = scriptum.listFragments()
        assertThat(fragments).hasSize(3)

        assertThat(fragments[0].fragmentName).isEqualTo("0")
        assertThat(fragments[1].fragmentName).isEqualTo("first-fragment")
        assertThat(fragments[2].fragmentName).isEqualTo("second-fragment")

        assertThat(fragments[0].text).contains("zero fragment")
        assertThat(fragments[1].text).contains("the first fragment")
        assertThat(fragments[2].text).contains("the second fragment")
    }

}
