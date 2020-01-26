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
import kotlin.check

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestsNoVerifyInspectionTest: SecurityTestTask() {
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
    fun `test the get method with no verify`() {
        var code = """
            import requests
            
            requests.get(url, verify=False)
        """.trimIndent()
        testCodeString(code)
    }

    @Test
    fun `test the get method with verify`() {
        var code = """
            import requests
            
            requests.get(url, verify=True)
        """.trimIndent()
        testCodeString(code, 0)
    }

    @Test
    fun `test the post method with no verify`() {
        var code = """
            import requests
            
            requests.post(url, verify=False)
        """.trimIndent()
        testCodeString(code)
    }

    @Test
    fun `test the post method with verify`() {
        var code = """
            import requests
            
            requests.post(url, verify=True)
        """.trimIndent()
        testCodeString(code, 0)
    }

    @Test
    fun `test the options method with no verify`() {
        var code = """
            import requests
            
            requests.options(url, verify=False)
        """.trimIndent()
        testCodeString(code)
    }

    @Test
    fun `test the put method with no verify`() {
        var code = """
            import requests
            
            requests.put(url, verify=False)
        """.trimIndent()
        testCodeString(code)
    }

    @Test
    fun `test the patch method with no verify`() {
        var code = """
            import requests
            
            requests.patch(url, verify=False)
        """.trimIndent()
        testCodeString(code)
    }

//    @Test
//    fun `test requests import with get and verify false argument`() {
//        var code = """
//            from requests import get
//
//            get(url, verify=False)
//        """.trimIndent()
//        testCodeString(code)
//    }

    @Test
    fun `test requests import with get and no arguments`() {
        var code = """
            from requests import get
            
            get(url)
        """.trimIndent()
        testCodeString(code, 0)
    }

    @Test
    fun `test requests import with get and a verify true argument`() {
        var code = """
            from requests import get
            
            get(url, verify=True)
        """.trimIndent()
        testCodeString(code, 0)
    }

    private fun testCodeString(code: String, times: Int = 1){
        val mockHolder = mock<ProblemsHolder> {
            on { registerProblem(any<PsiElement>(), eq(Checks.RequestsNoVerifyCheck.getDescription())) } doAnswer {}
        }
        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("test.py", PythonFileType.INSTANCE.language, code);
            val mockLocalSession = mock<LocalInspectionToolSession> {
                on { file } doReturn (testFile)
            }
            assertNotNull(testFile)
            val testVisitor = RequestsNoVerifyInspection().buildVisitor(mockHolder, true, mockLocalSession) as PyInspectionVisitor

            val expr: @NotNull MutableCollection<PyCallExpression> = PsiTreeUtil.findChildrenOfType(testFile, PyCallExpression::class.java)
            assertNotNull(expr)
            expr.forEach { e ->
                testVisitor.visitPyCallExpression(e)
            }
            Mockito.verify(mockHolder, Mockito.times(times)).registerProblem(any<PsiElement>(), eq(Checks.RequestsNoVerifyCheck.getDescription()))
            Mockito.verify(mockLocalSession, Mockito.times(1)).file
        }
    }
}