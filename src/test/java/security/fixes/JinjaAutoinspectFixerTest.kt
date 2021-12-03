package security.fixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Editor
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.psi.PyCallExpression
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import junit.framework.TestCase
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
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
            val testFile = this.createLightFile("test.py", PythonFileType.INSTANCE.language, code)

            assertNotNull(testFile)
            val fixer = JinjaAutoinspectUnconditionalFixer()
            val expr = PsiTreeUtil.findChildrenOfType(testFile, PyCallExpression::class.java).first()
            result = fixer.runFix(project, testFile, expr)?.text ?: ""
        }
        return result.replace(" ","").replace("\n", "")
    }

    @Test
    fun `replace environment with no args`(){
        val code = """
            import jinja2
            env = jinja2.Environment()
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals("jinja2.Environment(autoescape=True)", newCode)
    }

    @Test
    fun `replace template with no args`(){
        val code = """
            import jinja2
            env = jinja2.Template()
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals(newCode, "jinja2.Template(autoescape=True)")
    }

    @Test
    fun `replace environment with args`(){
        val code = """
            import jinja2
            env = jinja2.Environment(loader=PackageLoader('yourapplication', 'templates'))
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals("jinja2.Environment(loader=PackageLoader('yourapplication','templates'),autoescape=True)", newCode)
    }

    @Test
    fun `replace environment with nested keyword args`(){
        val code = """
            import jinja2
            env = jinja2.Environment(loader=PackageLoader(package_path='yourapplication',package_name='templates'))
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals("jinja2.Environment(loader=PackageLoader(package_path='yourapplication',package_name='templates'),autoescape=True)", newCode)
    }

    @Test
    fun `replace environment with args and false value`(){
        val code = """
            import jinja2
            env = jinja2.Environment(loader=PackageLoader('yourapplication', 'templates'), autoescape=False)
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals("jinja2.Environment(loader=PackageLoader('yourapplication','templates'),autoescape=True)", newCode)
    }

    @Test
    fun `replace template with args`(){
        val code = """
            import jinja2
            env = jinja2.Template("foo")
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals("jinja2.Template(\"foo\",autoescape=True)", newCode)
    }

    @Test
    fun `replace template with args and false value`(){
        val code = """
            import jinja2
            env = jinja2.Template("foo", autoescape=False)
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals("jinja2.Template(\"foo\",autoescape=True)", newCode)
    }

    @Test
    fun `replace template with args and no value`(){
        val code = """
            import jinja2
            env = jinja2.Template("foo", autoescape=x())
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals("", newCode)
    }

    @Test
    fun `replace template with args and other value`(){
        val code = """
            import jinja2
            env = jinja2.Template("foo", autoescape=1)
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals("", newCode)
    }

    @Test
    fun `test batch fix`(){
        val code = """
            import jinja2
            env = jinja2.Environment()
        """.trimIndent()

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code)
            assertNotNull(testFile)
            val fixer = JinjaAutoinspectUnconditionalFixer()
            val expr: MutableCollection<PyCallExpression> = PsiTreeUtil.findChildrenOfType(testFile, PyCallExpression::class.java)
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

    @Test
    fun `test get expression at caret`(){
        val code = """
            import jinja2
            env = jinja2.Environment()
        """.trimIndent()

        val mockCaretModel = mock<CaretModel> {
            on { offset } doReturn 29
        }
        val mockEditor = mock<Editor> {
            on { caretModel } doReturn mockCaretModel
        }

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code)
            assertNotNull(testFile)
            val fixer = JinjaAutoinspectUnconditionalFixer()
            assertTrue(fixer.isAvailable(project, mockEditor, testFile))
            val el = getPyCallExpressionAtCaret(testFile, mockEditor)
            assertNotNull(el)
            assertTrue(el is PyCallExpression)
            assertTrue(el!!.text.contains("Environment()"))
        }

        verify(mockEditor, Mockito.times(1)).caretModel
        verify(mockCaretModel, Mockito.times(1)).offset
    }

    @Test
    fun `test get top most expression at caret`(){
        val code = """
            import jinja2
            env = jinja2.Environment(loader=PackageLoader(package_path='yourapplication',package_name='templates'))
        """.trimIndent()

        val mockCaretModel = mock<CaretModel> {
            on { offset } doReturn 53
        }
        val mockEditor = mock<Editor> {
            on { caretModel } doReturn mockCaretModel
        }

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code)
            assertNotNull(testFile)
            val fixer = JinjaAutoinspectUnconditionalFixer()
            assertTrue(fixer.isAvailable(project, mockEditor, testFile))
            val el = getPyCallExpressionAtCaret(testFile, mockEditor)
            assertNotNull(el)
            assertTrue(el is PyCallExpression)
            assertTrue(el!!.text.contains("Environment("))
        }

        verify(mockEditor, Mockito.times(1)).caretModel
        verify(mockCaretModel, Mockito.times(1)).offset
    }
}