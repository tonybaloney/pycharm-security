package security.validators

import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.psi.PyCallExpression
import com.nhaarman.mockitokotlin2.*
import org.jetbrains.annotations.NotNull
import org.junit.jupiter.api.*
import org.mockito.Mockito
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SubprocessShellModeValidatorTest: SecurityTestTask() {
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
        val mockHolder = mock<AnnotationHolder> {
            on { createWarningAnnotation(any<PsiElement>(), eq(Checks.SubprocessShellCheck.toString())) } doReturn(dummyAnnotation);
        }
        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("test.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val testValidator = SubprocessShellModeValidator()
            testValidator.holder = mockHolder

            val expr: @NotNull MutableCollection<PyCallExpression> = PsiTreeUtil.findChildrenOfType(testFile, PyCallExpression::class.java)
            assertNotNull(expr)
            expr.forEach { e ->
                testValidator.visitPyCallExpression(e)
            }
            Mockito.verify(mockHolder, Mockito.times(times)).createWarningAnnotation(any<PsiElement>(), eq(Checks.SubprocessShellCheck.toString()))
        }
    }
}