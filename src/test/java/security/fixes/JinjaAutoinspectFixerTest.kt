package security.fixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.psi.PyCallExpression
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import junit.framework.TestCase
import org.jetbrains.annotations.NotNull
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JinjaAutoinspectFixerTest: SecurityTestTask() {
    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown(){
        super.tearDown()
    }

    @Test
    fun `verify fixer properties`(){
        val fixer = JinjaAutoinspectUnconditionalFixer()
        assertTrue(fixer.startInWriteAction())
        assertTrue(fixer.familyName.isNotBlank())
        assertTrue(fixer.name.isNotBlank())
        assertTrue(fixer.text.isNotBlank())
    }

    private fun getNewFileForCode(code: String): String {
        var result: String = ""
        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("test.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val fixer = JinjaAutoinspectUnconditionalFixer()
            var expr = PsiTreeUtil.findChildrenOfType(testFile, PyCallExpression::class.java).first()
            result = fixer.runFix(project, testFile, expr)!!.text
        }
        return result.replace(" ","").replace("\n", "")
    }

    @Test
    fun `replace environment with no args`(){
        var code = """
            import jinja2
            env = jinja2.Environment()
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals("jinja2.Environment(autoescape=True)", newCode)
    }

    @Test
    fun `replace template with no args`(){
        var code = """
            import jinja2
            env = jinja2.Template()
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals(newCode, "jinja2.Template(autoescape=True)")
    }

    @Test
    fun `replace environment with args`(){
        var code = """
            import jinja2
            env = jinja2.Environment(loader=PackageLoader('yourapplication', 'templates'))
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals("jinja2.Environment(loader=PackageLoader('yourapplication','templates'),autoescape=True)", newCode)
    }

    @Test
    fun `replace environment with args and false value`(){
        var code = """
            import jinja2
            env = jinja2.Environment(loader=PackageLoader('yourapplication', 'templates'), autoescape=False)
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals("jinja2.Environment(loader=PackageLoader('yourapplication','templates'),autoescape=True)", newCode)
    }

    @Test
    fun `replace template with args`(){
        var code = """
            import jinja2
            env = jinja2.Template("foo")
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals("jinja2.Template(\"foo\",autoescape=True)", newCode)
    }

    @Test
    fun `replace template with args and false value`(){
        var code = """
            import jinja2
            env = jinja2.Template("foo", autoescape=False)
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals("jinja2.Template(\"foo\",autoescape=True)", newCode)
    }


    @Test
    fun `test batch fix`(){
        var code = """
            import jinja2
            env = jinja2.Environment()
        """.trimIndent()

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val fixer = JinjaAutoinspectUnconditionalFixer()
            val expr: @NotNull MutableCollection<PyCallExpression> = PsiTreeUtil.findChildrenOfType(testFile, PyCallExpression::class.java)
            assertNotNull(expr)
            expr.forEach { e ->
                val mockProblemDescriptor = mock<ProblemDescriptor> {
                    on { psiElement } doReturn(e)
                }
                fixer.applyFix(project, mockProblemDescriptor)
                assertNotNull(e)
                verify(mockProblemDescriptor, times(2)).psiElement
            }
        }
    }
}