package security.validators

import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.psi.PyAssignmentStatement
import com.jetbrains.python.psi.PyBinaryExpression
import com.jetbrains.python.psi.PyCallExpression
import com.nhaarman.mockitokotlin2.*
import org.jetbrains.annotations.NotNull
import org.junit.jupiter.api.*
import org.mockito.Mockito
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TimingAttackValidatorTest: SecurityTestTask() {
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

    private fun testCodeString(code: String, times: Int = 1, checkMatch: Checks.CheckType){
        val mockHolder = mock<AnnotationHolder> {
            on { createWarningAnnotation(any<PsiElement>(), eq(checkMatch.toString())) } doReturn(dummyAnnotation);
        }
        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("test.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val testValidator = TimingAttackValidator()
            testValidator.holder = mockHolder

            val expr: @NotNull MutableCollection<PyBinaryExpression> = PsiTreeUtil.findChildrenOfType(testFile, PyBinaryExpression::class.java)
            assertNotNull(expr)
            expr.forEach { e ->
                testValidator.visitPyBinaryExpression(e)
            }
            Mockito.verify(mockHolder, Mockito.times(times)).createWarningAnnotation(any<PsiElement>(), eq(checkMatch.toString()))
        }
    }
}