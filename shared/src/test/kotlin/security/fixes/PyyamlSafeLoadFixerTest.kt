package security.fixes

import com.intellij.codeInspection.ProblemDescriptor
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PyyamlSafeLoadFixerTest: SecurityTestTask() {

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
        val fixer = PyyamlSafeLoadFixer()
        assertTrue(fixer.startInWriteAction())
        assertTrue(fixer.familyName.isNotBlank())
        assertTrue(fixer.name.isNotBlank())
        assertTrue(fixer.text.isNotBlank())
        val mockProblemDescriptor = mock<ProblemDescriptor> {
        }
        fixer.applyFix(this.project, mockProblemDescriptor)
    }

    @Test
    fun `test get call element at caret`(){
        var code = """
            import yaml
            yaml.load()
        """.trimIndent()

        val mockCaretModel = mock<CaretModel> {
            on { offset } doReturn 12
        }
        val mockEditor = mock<Editor> {
            on { caretModel } doReturn mockCaretModel
        }

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val fixer = PyyamlSafeLoadFixer()
            assertTrue(fixer.isAvailable(project, mockEditor, testFile))
            var el = getCallElementAtCaret(testFile, mockEditor)
            assertNotNull(el)
            assertTrue(el!!.text.contains("yaml.load"))
        }

        verify(mockEditor, Mockito.times(1)).caretModel
        verify(mockCaretModel, Mockito.times(1)).offset
    }

    @Test
    fun `test get new element at caret`(){
        var code = """
            import yaml
            yaml.load()
        """.trimIndent()

        val mockCaretModel = mock<CaretModel> {
            on { offset } doReturn 12
        }
        val mockEditor = mock<Editor> {
            on { caretModel } doReturn mockCaretModel
        }

        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val fixer = PyyamlSafeLoadFixer()
            assertTrue(fixer.isAvailable(project, mockEditor, testFile))
            var el = fixer.getNewExpressionAtCaret(testFile, mockEditor, project)
            assertNotNull(el)
            assertTrue(el!!.text.contains("yaml.safe_load"))
        }

        verify(mockEditor, Mockito.times(1)).caretModel
        verify(mockCaretModel, Mockito.times(1)).offset
    }
}