package security.fixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Editor
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.psi.PyBinaryExpression
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
class UseCompareDigestFixerTest: SecurityTestTask() {
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
        val fixer = UseCompareDigestFixer()
        assertTrue(fixer.startInWriteAction())
        assertTrue(fixer.familyName.isNotBlank())
        assertTrue(fixer.name.isNotBlank())
        assertTrue(fixer.text.isNotBlank())
        val mockProblemDescriptor = mock<ProblemDescriptor> {
        }
        fixer.applyFix(this.project, mockProblemDescriptor)
    }

    @Test
    fun `test get binary expression element at caret`(){
        var code = """
            if password == "SECRET":
                pass
        """.trimIndent()

        val mockCaretModel = mock<CaretModel> {
            on { offset } doReturn 8
        }
        val mockEditor = mock<Editor> {
            on { caretModel } doReturn mockCaretModel
        }

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val fixer = UseCompareDigestFixer()
            assertTrue(fixer.isAvailable(project, mockEditor, testFile))
            var el = getBinaryExpressionElementAtCaret(testFile, mockEditor)
            assertNotNull(el)
            assertTrue(el!!.text.contains("password == "))
        }

        verify(mockEditor, Mockito.times(1)).caretModel
        verify(mockCaretModel, Mockito.times(1)).offset
    }

    @Test
    fun `test get new element at caret`(){
        var code = """
            import hashlib
            if password == "SECRET":
                pass
        """.trimIndent()

        val mockCaretModel = mock<CaretModel> {
            on { offset } doReturn 20
        }
        val mockEditor = mock<Editor> {
            on { caretModel } doReturn mockCaretModel
        }

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val fixer = UseCompareDigestFixer()
            assertTrue(fixer.isAvailable(project, mockEditor, testFile))
            var oldEl = getBinaryExpressionElementAtCaret(testFile, mockEditor)
            assertNotNull(oldEl)
            var el = fixer.getNewExpressionAtCaret(testFile, project, oldEl!!)
            assertNotNull(el)
            assertTrue(el!!.text.contains("compare_digest"))
        }

        verify(mockEditor, Mockito.times(1)).caretModel
        verify(mockCaretModel, Mockito.times(1)).offset
    }

    @Test
    fun `test get new element at caret with no imports`(){
        var code = """
            if password == "SECRET":
                pass
        """.trimIndent()

        val mockCaretModel = mock<CaretModel> {
            on { offset } doReturn 8
        }
        val mockEditor = mock<Editor> {
            on { caretModel } doReturn mockCaretModel
        }

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val fixer = UseCompareDigestFixer()
            assertTrue(fixer.isAvailable(project, mockEditor, testFile))
            var oldEl = getBinaryExpressionElementAtCaret(testFile, mockEditor)
            assertNotNull(oldEl)
            var el = fixer.getNewExpressionAtCaret(testFile, project, oldEl!!)
            assertNotNull(el)
            assertTrue(el!!.text.contains("compare_digest"))
        }

        verify(mockEditor, Mockito.times(1)).caretModel
        verify(mockCaretModel, Mockito.times(1)).offset
    }

    @Test
    fun `test batch fix`(){
        var code = """
            if password == "SECRET":
                pass
            if password == "SECRET":
                pass
        """.trimIndent()

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val fixer = UseCompareDigestFixer()
            val expr: @NotNull MutableCollection<PyBinaryExpression> = PsiTreeUtil.findChildrenOfType(testFile, PyBinaryExpression::class.java)
            assertNotNull(expr)
            expr.forEach { e ->
                val mockProblemDescriptor = mock<ProblemDescriptor> {
                    on { psiElement } doReturn(e)
                }
                fixer.applyFix(project, mockProblemDescriptor)
                assertNotNull(e)
                verify(mockProblemDescriptor, times(4)).psiElement
            }
        }
    }
}