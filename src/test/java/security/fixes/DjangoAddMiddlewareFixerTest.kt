package security.fixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Editor
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.psi.PyListLiteralExpression
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.jetbrains.annotations.NotNull
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DjangoAddMiddlewareFixerTest: SecurityTestTask() {

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
        val fixer = DjangoAddMiddlewareFixer("test")
        assertTrue(fixer.startInWriteAction())
        assertTrue(fixer.familyName.isNotBlank())
        assertTrue(fixer.name.isNotBlank())
        assertTrue(fixer.text.isNotBlank())
    }

    @Test
    fun `test get list literal expression at caret`(){
        var code = """
            MIDDLEWARE = [
                'test_banana',
                'test_apple'
            ]
        """.trimIndent()

        val mockCaretModel = mock<CaretModel> {
            on { offset } doReturn 14
        }
        val mockEditor = mock<Editor> {
            on { caretModel } doReturn mockCaretModel
        }

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val fixer = DjangoAddMiddlewareFixer("test_banana")
            assertTrue(fixer.isAvailable(project, mockEditor, testFile))
            var el = getListLiteralExpressionAtCaret(testFile, mockEditor)
            assertNotNull(el)
            assertTrue(el is PyListLiteralExpression)
            assertTrue(el!!.text.contains("'test_banana'"))
        }

        verify(mockEditor, Mockito.times(1)).caretModel
        verify(mockCaretModel, Mockito.times(1)).offset
    }

    @Test
    fun `test get new element at caret`(){
        var code = """
            MIDDLEWARE = [
                'test_banana',
                'test_apple'
            ]
        """.trimIndent()

        val mockCaretModel = mock<CaretModel> {
            on { offset } doReturn 14
        }
        val mockEditor = mock<Editor> {
            on { caretModel } doReturn mockCaretModel
        }

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val fixer = DjangoAddMiddlewareFixer("test_plum")
            assertTrue(fixer.isAvailable(project, mockEditor, testFile))
            var oldEl = getListLiteralExpressionAtCaret(testFile, mockEditor)
            assertNotNull(oldEl)
            var el = fixer.getNewExpression(project, oldEl!!)
            assertNotNull(el)
            assertTrue(el.text.contains("test_plum"))
            assertTrue(el.text.contains("test_banana"))
            assertTrue(el.text.contains("test_apple"))
        }

        verify(mockEditor, Mockito.times(1)).caretModel
        verify(mockCaretModel, Mockito.times(1)).offset
    }

    @Test
    fun `test batch fix`(){
        var code = """
            MIDDLEWARE = [
                'test_banana',
                'test_apple'
            ]
            MIDDLEWARE = [
                'test_banana',
                'test_apple'
            ]
        """.trimIndent()

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val fixer = DjangoAddMiddlewareFixer("test_middleware")
            val expr: @NotNull MutableCollection<PyListLiteralExpression> = PsiTreeUtil.findChildrenOfType(testFile, PyListLiteralExpression::class.java)
            assertNotNull(expr)
            expr.forEach { e ->
                val mockProblemDescriptor = mock<ProblemDescriptor> {
                    on { psiElement } doReturn(e)
                }
                fixer.applyFix(project, mockProblemDescriptor)
                assertNotNull(e)
                verify(mockProblemDescriptor, times(1)).psiElement
            }
        }
    }
}