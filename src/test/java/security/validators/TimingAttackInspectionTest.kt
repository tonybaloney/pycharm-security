package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.codeInspection.QuickFix
import com.intellij.codeInspection.ex.QuickFixAction
import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyBinaryExpression
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
class TimingAttackInspectionTest: SecurityTestTask() {
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
    fun `test match of password comparator left`(){
        var code = """
            password = "BANANA"
            if password == "BANANA":
                pass
        """.trimIndent()
        testCodeString(code, 1, Checks.TimingAttackCheck)
    }

    @Test
    fun `test match of password comparator right`(){
        var code = """
            password = "BANANA"
            if "BANANA" == password:
                pass
        """.trimIndent()
        testCodeString(code, 1, Checks.TimingAttackCheck)
    }

    @Test
    fun `test skip of normal comparator`(){
        var code = """
            var = "BANANA"
            if "BANANA" == var:
                pass
        """.trimIndent()
        testCodeString(code, 0, Checks.TimingAttackCheck)
    }

    private fun testCodeString(code: String, times: Int = 1, check: Checks.CheckType){
        val mockHolder = mock<ProblemsHolder> {
            on { registerProblem(any<PsiElement>(), eq(check.getDescription()), any<LocalQuickFix>()) } doAnswer {}
        }
        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("test.py", PythonFileType.INSTANCE.language, code);
            val mockLocalSession = mock<LocalInspectionToolSession> {
                on { file } doReturn (testFile)
            }
            assertNotNull(testFile)
            val testVisitor = TimingAttackInspection().buildVisitor(mockHolder, true, mockLocalSession) as PyInspectionVisitor

            val expr: @NotNull MutableCollection<PyBinaryExpression> = PsiTreeUtil.findChildrenOfType(testFile, PyBinaryExpression::class.java)
            assertNotNull(expr)
            expr.forEach { e ->
                testVisitor.visitPyBinaryExpression(e)
            }
            Mockito.verify(mockHolder, Mockito.times(times)).registerProblem(any<PsiElement>(), eq(check.getDescription()), any<LocalQuickFix>())
            Mockito.verify(mockLocalSession, Mockito.times(1)).file
        }
    }
}