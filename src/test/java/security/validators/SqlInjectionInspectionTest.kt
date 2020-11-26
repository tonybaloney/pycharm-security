package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SqlInjectionInspectionTest: SecurityTestTask() {
    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown(){
        super.tearDown()
    }

    @Test
    fun `verify description is not empty`(){
        assertFalse(SqlInjectionInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test format string string select`(){
        val code = """
            query = "SELECT * FROM users WHERE id = {0}".format(id)
        """.trimIndent()
        testStringLiteralExpression(code, 1, Checks.SqlInjectionCheck, "test.py", SqlInjectionInspection())
    }

    @Test
    fun `test format string not select`(){
        val code = """
            query = "SELECT a banana {0}".format(id)
        """.trimIndent()
        testStringLiteralExpression(code, 0, Checks.SqlInjectionCheck, "test.py", SqlInjectionInspection())
    }

    @Test
    fun `test format string update`(){
        val code = """
            query = "UPDATE users SET id = {0} WHERE x=1".format(id)
        """.trimIndent()
        testStringLiteralExpression(code, 1, Checks.SqlInjectionCheck, "test.py", SqlInjectionInspection())
    }

    @Test
    fun `test format string string not update`(){
        val code = """
            query = "UPDATE a banana {0}".format(id)
        """.trimIndent()
        testStringLiteralExpression(code, 0, Checks.SqlInjectionCheck, "test.py", SqlInjectionInspection())
    }


    @Test
    fun `test insert into format function`(){
        val code = """
            query = "INSERT INTO users (id) VALUES ( id = {0} )".format(id)
        """.trimIndent()
        testStringLiteralExpression(code, 1, Checks.SqlInjectionCheck, "test.py", SqlInjectionInspection())
    }

    @Test
    fun `test format string string select perc format`(){
        val code = """
            query = "SELECT * FROM users WHERE id = {0}" % id
        """.trimIndent()
        testStringLiteralExpression(code, 1, Checks.SqlInjectionCheck, "test.py", SqlInjectionInspection())
    }

    @Test
    fun `test format string not select perc format`(){
        val code = """
            query = "SELECT a banana {0}" % id
        """.trimIndent()
        testStringLiteralExpression(code, 0, Checks.SqlInjectionCheck, "test.py", SqlInjectionInspection())
    }

    @Test
    fun `test format string update perc format`(){
        val code = """
            query = "UPDATE users SET id = {0} WHERE x=1" % id
        """.trimIndent()
        testStringLiteralExpression(code, 1, Checks.SqlInjectionCheck, "test.py", SqlInjectionInspection())
    }

    @Test
    fun `test format string string not update perc format`(){
        val code = """
            query = "UPDATE a banana {0}" % id
        """.trimIndent()
        testStringLiteralExpression(code, 0, Checks.SqlInjectionCheck, "test.py", SqlInjectionInspection())
    }


    @Test
    fun `test insert into format function perc format`(){
        val code = """
            query = "INSERT INTO users (id) VALUES ( id = {0} )" % id
        """.trimIndent()
        testStringLiteralExpression(code, 1, Checks.SqlInjectionCheck, "test.py", SqlInjectionInspection())
    }

    @Test
    fun `test format string string select fstring format`(){
        val code = """
            query = f"SELECT * FROM users WHERE id = {0}"
        """.trimIndent()
        testFormattedStringElement(code, 1, Checks.SqlInjectionCheck, "test.py", SqlInjectionInspection())
    }

    @Test
    fun `test format string not select fstring format`(){
        val code = """
            query = f"SELECT a banana {0}"
        """.trimIndent()
        testFormattedStringElement(code, 0, Checks.SqlInjectionCheck, "test.py", SqlInjectionInspection())
    }

    @Test
    fun `test format string update fstring format`(){
        val code = """
            query = f"UPDATE users SET id = {0} WHERE x=1"
        """.trimIndent()
        testFormattedStringElement(code, 1, Checks.SqlInjectionCheck, "test.py", SqlInjectionInspection())
    }

    @Test
    fun `test format string string not update fstring format`(){
        val code = """
            query = f"UPDATE a banana {0}"
        """.trimIndent()
        testFormattedStringElement(code, 0, Checks.SqlInjectionCheck, "test.py", SqlInjectionInspection())
    }


    @Test
    fun `test insert into format function fstring format`(){
        val code = """
            query = f"INSERT INTO users (id) VALUES ( id = {0} )"
        """.trimIndent()
        testFormattedStringElement(code, 1, Checks.SqlInjectionCheck, "test.py", SqlInjectionInspection())
    }
}