package security.fixes

import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Editor
import com.jetbrains.python.PythonFileType
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.*
import org.mockito.Mockito
import security.SecurityTestTask
import security.validators.TimingAttackValidator

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UseHmacCompareDigestFixerTest: SecurityTestTask() {
    lateinit var dummyAnnotation: Annotation

    @BeforeAll
    override fun setUp() {
        super.setUp()
        this.dummyAnnotation = Annotation(0, 0, HighlightSeverity.WARNING, "", "")
    }

    @AfterAll
    override fun tearDown(){
        super.tearDown()
    }

    @Test
    fun `verify fixer properties`(){
        val fixer = UseHmacCompareDigestFixer()
        assertTrue(fixer.startInWriteAction())
        assertTrue(fixer.familyName.isNotBlank())
        assertTrue(fixer.name.isNotBlank())
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
            val fixer = UseHmacCompareDigestFixer()
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
            val fixer = UseHmacCompareDigestFixer()
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
            val fixer = UseHmacCompareDigestFixer()
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
}