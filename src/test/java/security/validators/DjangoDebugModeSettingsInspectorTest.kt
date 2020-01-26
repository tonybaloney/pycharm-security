package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyAssignmentStatement
import com.nhaarman.mockitokotlin2.*
import org.jetbrains.annotations.NotNull
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DjangoDebugModeSettingsInspectorTest: SecurityTestTask() {
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
    fun `test django settings file with debug mode on`(){
        var code = """
            DEBUG = True
        """.trimIndent()
        testCodeString(code, 1)
    }

    @Test
    fun `test django settings file with debug mode off`(){
        var code = """
            DEBUG = False
        """.trimIndent()
        testCodeString(code, 0)
    }

    @Test
    fun `test django settings with no debug mode`(){
        var code = """
            X = 1
        """.trimIndent()
        testCodeString(code, 0)
    }

    private fun testCodeString(code: String, times: Int = 1){
        val mockHolder = mock<ProblemsHolder> {
            on { registerProblem(any<PsiElement>(), eq(Checks.DjangoDebugModeCheck.getDescription())) } doAnswer {}
        }
        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("settings.py", PythonFileType.INSTANCE.language, code);
            val mockLocalSession = mock<LocalInspectionToolSession> {
                on { file } doReturn (testFile)
            }
            assertNotNull(testFile)
            val testVisitor = DjangoDebugModeSettingsInspection().buildVisitor(mockHolder, true, mockLocalSession) as PyInspectionVisitor

            val expr: @NotNull MutableCollection<PyAssignmentStatement> = PsiTreeUtil.findChildrenOfType(testFile, PyAssignmentStatement::class.java)
            assertNotNull(expr)
            expr.forEach { e ->
                testVisitor.visitPyAssignmentStatement(e)
            }
            Mockito.verify(mockHolder, Mockito.times(times)).registerProblem(any<PsiElement>(), eq(Checks.DjangoDebugModeCheck.getDescription()))
            Mockito.verify(mockLocalSession, Mockito.times(1)).file
        }
    }
}