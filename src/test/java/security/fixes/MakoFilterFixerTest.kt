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
import org.jetbrains.annotations.NotNull
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MakoFilterFixerTest: SecurityTestTask() {
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
        val fixer = MakoFilterFixer()
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
            val fixer = MakoFilterFixer()
            var expr = PsiTreeUtil.findChildrenOfType(testFile, PyCallExpression::class.java).first()
            result = fixer.runFix(project, testFile, expr)?.text ?: ""
        }
        return result.replace(" ","").replace("\n", "")
    }

    @Test
    fun `replace environment with no args`(){
        var code = """
            import mako.template
            env = mako.template.Template('xyz')
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals("mako.template.Template('xyz',default_filters=['h'])", newCode)
    }

    @Test
    fun `replace environment with existing arg`(){
        var code = """
            import mako.template
            env = mako.template.Template('xyz', default_filters=['h'])
        """.trimIndent()
        val newCode = getNewFileForCode(code)
        TestCase.assertEquals(newCode, "")
    }


    @Test
    fun `test batch fix`(){
        var code = """
            import mako.template
            env = mako.template.Template('xyz')
        """.trimIndent()

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val fixer = MakoFilterFixer()
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