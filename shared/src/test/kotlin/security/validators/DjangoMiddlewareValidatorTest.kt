package security.validators

import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.psi.PyAssignmentStatement
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import org.jetbrains.annotations.NotNull
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers.contains
import org.mockito.Mockito
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DjangoMiddlewareValidatorTest: SecurityTestTask() {
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
    fun `test django settings file with no csrf`(){
        var code = """
            MIDDLEWARE = [
                'django.middleware.security.SecurityMiddleware',
                'django.contrib.sessions.middleware.SessionMiddleware',
                'django.middleware.common.CommonMiddleware',
                'django.contrib.auth.middleware.AuthenticationMiddleware',
                'django.contrib.messages.middleware.MessageMiddleware',
                'django.middleware.clickjacking.XFrameOptionsMiddleware',
            ]
        """.trimIndent()
        testCodeString(code, 1, Checks.DjangoCsrfMiddlewareCheck)
    }

    @Test
    fun `test django settings file no clickjack`(){
        var code = """
            MIDDLEWARE = [
                'django.middleware.security.SecurityMiddleware',
                'django.contrib.sessions.middleware.SessionMiddleware',
                'django.middleware.common.CommonMiddleware',
                'django.middleware.csrf.CsrfViewMiddleware',
                'django.contrib.auth.middleware.AuthenticationMiddleware',
                'django.contrib.messages.middleware.MessageMiddleware',
            ]
        """.trimIndent()
        testCodeString(code, 1, Checks.DjangoClickjackMiddlewareCheck)
    }

    @Test
    fun `test django settings with empty middleware`(){
        var code = """
            MIDDLEWARE = [
            ]
        """.trimIndent()
        testCodeString(code, 1, Checks.DjangoClickjackMiddlewareCheck)
        testCodeString(code, 1, Checks.DjangoCsrfMiddlewareCheck)
    }

    @Test
    fun `test django settings with all requirements`(){
        var code = """
            MIDDLEWARE = [
                'django.middleware.security.SecurityMiddleware',
                'django.contrib.sessions.middleware.SessionMiddleware',
                'django.middleware.common.CommonMiddleware',
                'django.middleware.csrf.CsrfViewMiddleware',
                'django.contrib.auth.middleware.AuthenticationMiddleware',
                'django.contrib.messages.middleware.MessageMiddleware',
                'django.middleware.clickjacking.XFrameOptionsMiddleware',
            ]
        """.trimIndent()
        testCodeString(code, 0, Checks.DjangoClickjackMiddlewareCheck)
    }

    private fun testCodeString(code: String, times: Int = 1, check: Checks.CheckType){
        val mockHolder = mock<AnnotationHolder> {
            on { createWarningAnnotation(any<PsiElement>(), contains("DJG")) } doReturn(dummyAnnotation);
        }
        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("settings.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            val testValidator = DjangoMiddlewareValidator()
            testValidator.holder = mockHolder

            val expr: @NotNull MutableCollection<PyAssignmentStatement> = PsiTreeUtil.findChildrenOfType(testFile, PyAssignmentStatement::class.java)
            assertNotNull(expr)
            expr.forEach { e ->
                testValidator.visitPyAssignmentStatement(e)
            }
            Mockito.verify(mockHolder, Mockito.times(times)).createWarningAnnotation(any<PsiElement>(), eq(check.toString()))
        }
    }
}