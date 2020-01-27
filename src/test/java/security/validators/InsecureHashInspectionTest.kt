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
class InsecureHashInspectionTest: SecurityTestTask() {
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
    fun `verify description is not empty`(){
        assertFalse(InsecureHashInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test new hash with insecure algorithm`(){
        var code = """
            import hashlib
            hashlib.new('sha')
        """.trimIndent()
        testCodeString(code, 1, Checks.InsecureHashAlgorithms)
    }

    @Test
    fun `test new hash with insecure algorithm kwarg`(){
        var code = """
            import hashlib
            hashlib.new(name='sha')
        """.trimIndent()
        testCodeString(code, 1, Checks.InsecureHashAlgorithms)
    }

    @Test
    fun `test new hash with more-secure algorithm`(){
        var code = """
            import hashlib
            hashlib.new('blake2')
        """.trimIndent()
        testCodeString(code, 0, Checks.InsecureHashAlgorithms)
    }

    @Test
    fun `test new hash with type import`(){
        var code = """
            import hashlib
            hashlib.sha()
        """.trimIndent()
        testCodeString(code, 1, Checks.InsecureHashAlgorithms)
    }

    @Test
    fun `test new hash with non-string argument`(){
        var code = """
            import hashlib
            hashlib.new(1)
        """.trimIndent()
        testCodeString(code, 0, Checks.InsecureHashAlgorithms)
    }

    @Test
    fun `test new hash with length-attack algorithm kwarg`(){
        var code = """
            import hashlib
            hashlib.new(name='whirlpool')
        """.trimIndent()
        testCodeString(code, 1, Checks.LengthAttackHashAlgorithms)
    }

    @Test
    fun `test new hash with length-attack algorithm type import`(){
        var code = """
            import hashlib
            hashlib.whirlpool()
        """.trimIndent()
        testCodeString(code, 1, Checks.LengthAttackHashAlgorithms)
    }

    @Test
    fun `test new hash with length-attack algorithm`(){
        var code = """
            import hashlib
            hashlib.new('whirlpool')
        """.trimIndent()
        testCodeString(code, 1, Checks.LengthAttackHashAlgorithms)
    }

    @Test
    fun `test secure algorithm`(){
        var code = """
            import hashlib
            hashlib.new('blake2')
        """.trimIndent()
        testCodeString(code, 0, Checks.InsecureHashAlgorithms)
    }

    private fun testCodeString(code: String, times: Int = 1, check: Checks.CheckType){
        val mockHolder = mock<ProblemsHolder> {
            on { registerProblem(any<PsiElement>(), eq(check.getDescription())) } doAnswer {}
        }
        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("test.py", PythonFileType.INSTANCE.language, code);
            val mockLocalSession = mock<LocalInspectionToolSession> {
                on { file } doReturn (testFile)
            }
            assertNotNull(testFile)
            val testVisitor = InsecureHashInspection().buildVisitor(mockHolder, true, mockLocalSession) as PyInspectionVisitor

            val expr: @NotNull MutableCollection<PyCallExpression> = PsiTreeUtil.findChildrenOfType(testFile, PyCallExpression::class.java)
            assertNotNull(expr)
            expr.forEach { e ->
                testVisitor.visitPyCallExpression(e)
            }
            Mockito.verify(mockHolder, Mockito.times(times)).registerProblem(any<PsiElement>(), eq(check.getDescription()))
            Mockito.verify(mockLocalSession, Mockito.times(1)).file
        }
    }
}