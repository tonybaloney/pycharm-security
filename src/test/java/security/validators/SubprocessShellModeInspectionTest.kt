package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.LocalQuickFix
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
import kotlin.check

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SubprocessShellModeInspectionTest: SecurityTestTask() {
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
        assertFalse(SubprocessShellModeInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test subprocess call with shell mode`(){
        var code = """
            import subprocess
            subprocess.call(shell=True)
        """.trimIndent()
        testCodeString(code, 1)
    }

    @Test
    fun `test subprocess call with shell mode ref arg`(){
        var code = """
            import subprocess
            subprocess.call(x, shell=True)
        """.trimIndent()
        testCodeString(code, 1)
    }

    @Test
    fun `test subprocess call with shell mode list ref arg`(){
        var code = """
            import subprocess
            subprocess.call([x], shell=True)
        """.trimIndent()
        testCodeString(code, 1)
    }

    @Test
    fun `test subprocess call with shell mode string literal arg`(){
        var code = """
            import subprocess
            subprocess.call('test', shell=True)
        """.trimIndent()
        testCodeString(code, 0)
    }

    @Test
    fun `test subprocess call with shell mode list literal arg`(){
        var code = """
            import subprocess
            subprocess.call(['test', 'x'], shell=True)
        """.trimIndent()
        testCodeString(code, 0)
    }

    @Test
    fun `test subprocess call with shell mixed list arg`(){
        var code = """
            import subprocess
            subprocess.call(['test', x], shell=True)
        """.trimIndent()
        testCodeString(code, 1)
    }

    @Test
    fun `test subprocess call with escaped arg`(){
        var code = """
            import subprocess
            import shlex
            subprocess.call(shlex.quote(x), shell=True)
        """.trimIndent()
        testCodeString(code, 0)
    }

    @Test
    fun `test subprocess call with escaped list arg`(){
        var code = """
            import subprocess
            import shlex
            subprocess.call([shlex.quote(x)], shell=True)
        """.trimIndent()
        testCodeString(code, 0)
    }

    @Test
    fun `test subprocess Popen with shell mode`(){
        var code = """
            import subprocess
            subprocess.Popen(shell=True)
        """.trimIndent()
        testCodeString(code, 1)
    }

    @Test
    fun `test subprocess run with shell mode`(){
        var code = """
            import subprocess
            subprocess.run(shell=True)
        """.trimIndent()
        testCodeString(code, 1)
    }

    @Test
    fun `test normal subprocess call`(){
        var code = """
            import subprocess
            subprocess.call()
        """.trimIndent()
        testCodeString(code, 0)
    }

    private fun testCodeString(code: String, times: Int = 1){
        val mockHolder = mock<ProblemsHolder> {
            on { registerProblem(any<PsiElement>(), eq(Checks.SubprocessShellCheck.getDescription()), any<LocalQuickFix>()) } doAnswer {}
        }
        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("test.py", PythonFileType.INSTANCE.language, code);
            val mockLocalSession = mock<LocalInspectionToolSession> {
                on { file } doReturn (testFile)
            }
            assertNotNull(testFile)
            val testVisitor = SubprocessShellModeInspection().buildVisitor(mockHolder, true, mockLocalSession) as PyInspectionVisitor

            val expr: @NotNull MutableCollection<PyCallExpression> = PsiTreeUtil.findChildrenOfType(testFile, PyCallExpression::class.java)
            assertNotNull(expr)
            expr.forEach { e ->
                testVisitor.visitPyCallExpression(e)
            }
            Mockito.verify(mockHolder, Mockito.times(times)).registerProblem(any<PsiElement>(), eq(Checks.SubprocessShellCheck.getDescription()), any<LocalQuickFix>())
            Mockito.verify(mockLocalSession, Mockito.times(1)).file
        }
    }
}