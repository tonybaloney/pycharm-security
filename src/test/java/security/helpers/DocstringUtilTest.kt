package security.helpers

import com.intellij.lang.Language
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.documentation.doctest.PyDocstringParserDefinition
import com.jetbrains.python.psi.PyAssignmentStatement
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.SecurityTestTask
import security.settings.SecuritySettings

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocstringUtilTest: SecurityTestTask() {

    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown(){
        super.tearDown()
    }

    @Test
    fun `test statement in docstring enabled`(){
        val code = """
            def foo():
                x = 1
        """.trimIndent()
        assertTrue(testSkipDocstring(code, true, PyDocstringParserDefinition.PYTHON_DOCSTRING_FILE.language))
    }

    @Test
    fun `test statement in docstring disabled`(){
        val code = """
            def foo():
                x = 1
        """.trimIndent()
        assertFalse(testSkipDocstring(code, false, PyDocstringParserDefinition.PYTHON_DOCSTRING_FILE.language))
    }

    @Test
    fun `test statement outside docstring enabled`(){
        val code = """
            def foo():
                x = 1
        """.trimIndent()
        assertFalse(testSkipDocstring(code, true, PythonFileType.INSTANCE.language))
    }

    @Test
    fun `test statement outside docstring disabled`(){
        val code = """
            def foo():
                x = 1
        """.trimIndent()
        assertFalse(testSkipDocstring(code, false, PythonFileType.INSTANCE.language))
    }

    private fun testSkipDocstring(code: String, skip: Boolean, language: Language): Boolean{
        var result = false
        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("test.py", language, code)

            SecuritySettings.instance.ignoreDocstrings = skip
            assertNotNull(testFile)
            // Get first assignment statement
            val expr: MutableCollection<PyAssignmentStatement> = PsiTreeUtil.findChildrenOfType(testFile, PyAssignmentStatement::class.java)
            result = skipDocstring(expr.first())
        }
        return result
    }
}