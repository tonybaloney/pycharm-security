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
import com.jetbrains.python.psi.PyCallExpression
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import org.jetbrains.annotations.NotNull
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TempfileMktempInspectionTest: SecurityTestTask() {
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
    fun `test temp file with insecure make`(){
        var code = """
            import tempfile
            tempfile.mktemp()
        """.trimIndent()
        testCodeString(code, 1)
    }

    @Test
    fun `test temp file with make (safe) temp`(){
        var code = """
            import tempfile
            mkstemp()
        """.trimIndent()
        testCodeString(code, 0)
    }

    private fun testCodeString(code: String, times: Int = 1){
        val mockHolder = mock<ProblemsHolder> {
            on { registerProblem(any<PsiElement>(), eq(Checks.TempfileMktempCheck.toString())) }
        }
        val mockLocalSession = mock<LocalInspectionToolSession> {

        }
        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("test.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val testVisitor = DjangoDebugModeSettingsInspection().buildVisitor(mockHolder, true, mockLocalSession) as PyInspectionVisitor

            val expr: @NotNull MutableCollection<PyCallExpression> = PsiTreeUtil.findChildrenOfType(testFile, PyCallExpression::class.java)
            assertNotNull(expr)
            expr.forEach { e ->
                testVisitor.visitPyCallExpression(e)
            }
            Mockito.verify(mockHolder, Mockito.times(times)).registerProblem(any<PsiElement>(), eq(Checks.TempfileMktempCheck.toString()))
        }
    }
}