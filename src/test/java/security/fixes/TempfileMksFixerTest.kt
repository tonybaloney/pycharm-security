package security.fixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.HighlightSeverity
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
import org.jetbrains.annotations.NotNull
import org.junit.jupiter.api.*
import org.mockito.Mockito
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TempfileMksFixerTest: SecurityTestTask() {
    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown(){
        super.tearDown()
    }

    @Test
    fun `verify fixer properies`(){
        val fixer = TempfileMksFixer()
        assertTrue(fixer.startInWriteAction())
        assertTrue(fixer.familyName.isNotBlank())
        assertTrue(fixer.name.isNotBlank())
        assertTrue(fixer.text.isNotBlank())
    }

    @Test
    fun `test get call element at caret`(){
        var code = """
            import tempfile
            tempfile.mktemp()
        """.trimIndent()

        val mockCaretModel = mock<CaretModel> {
            on { offset } doReturn 16
        }
        val mockEditor = mock<Editor> {
            on { caretModel } doReturn mockCaretModel
        }

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val fixer = TempfileMksFixer()
            assertTrue(fixer.isAvailable(project, mockEditor, testFile))
            var el = getCallElementAtCaret(testFile, mockEditor)
            assertNotNull(el)
            assertTrue(el!!.text.contains("tempfile.mktemp"))
        }

        verify(mockEditor, Mockito.times(1)).caretModel
        verify(mockCaretModel, Mockito.times(1)).offset
    }

    @Test
    fun `test get new element at caret`(){
        var code = """
            import tempfile
            tempfile.mktemp()
        """.trimIndent()

        val mockCaretModel = mock<CaretModel> {
            on { offset } doReturn 16
        }
        val mockEditor = mock<Editor> {
            on { caretModel } doReturn mockCaretModel
        }

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val fixer = TempfileMksFixer()
            assertTrue(fixer.isAvailable(project, mockEditor, testFile))
            var el = fixer.getNewExpressionAtCaret(testFile, mockEditor, project)
            assertNotNull(el)
            assertTrue(el!!.text.contains("tempfile.mkstemp"))
        }

        verify(mockEditor, Mockito.times(1)).caretModel
        verify(mockCaretModel, Mockito.times(1)).offset
    }

    @Test
    fun `test batch fix`(){
        var code = """
            import tempfile
            tempfile.mktemp()
            tempfile.mktemp()
        """.trimIndent()

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val fixer = TempfileMksFixer()
            val expr: @NotNull MutableCollection<PyCallExpression> = PsiTreeUtil.findChildrenOfType(testFile, PyCallExpression::class.java)
            assertNotNull(expr)
            expr.forEach { e ->
                val mockProblemDescriptor = mock<ProblemDescriptor> {
                    on { psiElement } doReturn(e)
                }
                fixer.applyFix(project, mockProblemDescriptor)
                assertNotNull(e)
                verify(mockProblemDescriptor, times(3)).psiElement
            }
        }
    }
}