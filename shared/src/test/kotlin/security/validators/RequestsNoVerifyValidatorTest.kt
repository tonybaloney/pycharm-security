package security.validators

import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiElement
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.psi.PyCallExpression
import com.nhaarman.mockitokotlin2.*
import org.jetbrains.annotations.NotNull
import org.junit.jupiter.api.*
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestsNoVerifyValidatorTest: SecurityTestTask() {
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
        val mockHolder = mock<AnnotationHolder> {
            on { createWarningAnnotation(any<PsiElement>(), eq(Checks.RequestsNoVerifyCheck.toString())) } doReturn(dummyAnnotation);
        }
        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("test.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile);
            val expr: @NotNull PsiElement = testFile.children[2].children[0]
            assertNotNull(expr)

            val testValidator = RequestsNoVerifyValidator()
            testValidator.holder = mockHolder
            testValidator.visitPyCallExpression(expr as PyCallExpression)
            verify(mockHolder, times(times)).createWarningAnnotation(any<PsiElement>(), eq(Checks.RequestsNoVerifyCheck.toString()))
        }
    }
}