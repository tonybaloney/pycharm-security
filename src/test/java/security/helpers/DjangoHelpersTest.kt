package security.helpers

import com.intellij.openapi.application.ApplicationManager
import com.jetbrains.python.psi.PyElementGenerator
import com.jetbrains.python.psi.PyStringLiteralExpression
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DjangoHelpersTest: SecurityTestTask() {

    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown(){
        super.tearDown()
    }

    @Test
    fun `test basic statement`() {
        ApplicationManager.getApplication().runReadAction {
            val testString = asPyStringLiteralExpression("'%s'")
            assertTrue(inspectDjangoSqlTemplate(testString))
        }
    }

    @Test
    fun `test basic double quoted statement`() {
        ApplicationManager.getApplication().runReadAction {
            val testString = asPyStringLiteralExpression("\"%s\"")
            assertTrue(inspectDjangoSqlTemplate(testString))
        }
    }

    @Test
    fun `test named function argument double quoted statement`() {
        ApplicationManager.getApplication().runReadAction {
            val testString = asPyStringLiteralExpression("\"%(foo)s\"")
            assertTrue(inspectDjangoSqlTemplate(testString))
        }
    }

    @Test
    fun `test named function argument single quoted statement`() {
        ApplicationManager.getApplication().runReadAction {
            val testString = asPyStringLiteralExpression("'%(foo)s'")
            assertTrue(inspectDjangoSqlTemplate(testString))
        }
    }

    @Test
    fun `test unquoted basic statement`() {
        ApplicationManager.getApplication().runReadAction {
            val testString = asPyStringLiteralExpression("%s")
            assertFalse(inspectDjangoSqlTemplate(testString))
        }
    }

    @Test
    fun `test basic double unquoted statement`() {
        ApplicationManager.getApplication().runReadAction {
            val testString = asPyStringLiteralExpression("%s")
            assertFalse(inspectDjangoSqlTemplate(testString))
        }
    }

    @Test
    fun `test named function argument double unquoted statement`() {
        ApplicationManager.getApplication().runReadAction {
            val testString = asPyStringLiteralExpression("%(foo)s")
            assertFalse(inspectDjangoSqlTemplate(testString))
        }
    }

    @Test
    fun `test named function argument single unquoted statement`() {
        ApplicationManager.getApplication().runReadAction {
            val testString = asPyStringLiteralExpression("%(foo)s")
            assertFalse(inspectDjangoSqlTemplate(testString))
        }
    }

    private fun asPyStringLiteralExpression(str: String): PyStringLiteralExpression {
        val elementGenerator = PyElementGenerator.getInstance(project)
        return elementGenerator.createStringLiteralFromString(str)
    }
}