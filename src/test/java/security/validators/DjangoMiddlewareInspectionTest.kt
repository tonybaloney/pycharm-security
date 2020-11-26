package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DjangoMiddlewareInspectionTest: SecurityTestTask() {
    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown(){
        super.tearDown()
    }

    @Test
    fun `verify description is not empty`(){
        assertFalse(DjangoMiddlewareInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test django settings file with no csrf`(){
        val code = """
            MIDDLEWARE = [
                'django.middleware.security.SecurityMiddleware',
                'django.contrib.sessions.middleware.SessionMiddleware',
                'django.middleware.common.CommonMiddleware',
                'django.contrib.auth.middleware.AuthenticationMiddleware',
                'django.contrib.messages.middleware.MessageMiddleware',
                'django.middleware.clickjacking.XFrameOptionsMiddleware',
            ]
        """.trimIndent()
        testCodeAssignmentStatement(code, 1, Checks.DjangoCsrfMiddlewareCheck, "settings.py", DjangoMiddlewareInspection())
    }

    @Test
    fun `test django settings file no clickjack`(){
        val code = """
            MIDDLEWARE = [
                'django.middleware.security.SecurityMiddleware',
                'django.contrib.sessions.middleware.SessionMiddleware',
                'django.middleware.common.CommonMiddleware',
                'django.middleware.csrf.CsrfViewMiddleware',
                'django.contrib.auth.middleware.AuthenticationMiddleware',
                'django.contrib.messages.middleware.MessageMiddleware',
            ]
        """.trimIndent()
        testCodeAssignmentStatement(code, 1, Checks.DjangoClickjackMiddlewareCheck, "settings.py", DjangoMiddlewareInspection())
    }

    @Test
    fun `test django settings with empty middleware`(){
        val code = """
            MIDDLEWARE = [
            ]
        """.trimIndent()
        testCodeAssignmentStatement(code, 1, Checks.DjangoClickjackMiddlewareCheck, "settings.py", DjangoMiddlewareInspection())
        testCodeAssignmentStatement(code, 1, Checks.DjangoCsrfMiddlewareCheck, "settings.py", DjangoMiddlewareInspection())
    }

    @Test
    fun `test django settings with all requirements`() {
        val code = """
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
        testCodeAssignmentStatement(code, 0, Checks.DjangoClickjackMiddlewareCheck, "settings.py", DjangoMiddlewareInspection())
    }

    @Test
    fun `test another assignment type`(){
        val code = """
            MUDDLE_WARE = [
            ]
        """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.DjangoClickjackMiddlewareCheck, "settings.py", DjangoMiddlewareInspection())

    }

    @Test
    fun `test another file name`(){
        val code = """
                MIDDLEWARE = [
                ]
            """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.DjangoClickjackMiddlewareCheck, "file.py", DjangoMiddlewareInspection())
    }

    @Test
    fun `test no left hand `(){
        val code = """
                 = [
                ]
            """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.DjangoClickjackMiddlewareCheck, "settings.py", DjangoMiddlewareInspection())
    }

    @Test
    fun `test no right hand`(){
        val code = """
                MIDDLEWARE = 
            """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.DjangoClickjackMiddlewareCheck, "settings.py", DjangoMiddlewareInspection())
    }

    @Test
    fun `test value is not list literal`(){
        val code = """
                MIDDLEWARE = 'banana'
            """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.DjangoClickjackMiddlewareCheck, "settings.py", DjangoMiddlewareInspection())
    }
}