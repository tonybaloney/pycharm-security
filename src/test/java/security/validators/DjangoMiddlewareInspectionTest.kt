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
        testCodeAssignmentStatement(code, 1, Checks.DjangoCsrfMiddlewareCheck, "settings.py", DjangoMiddlewareInspection())
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
        testCodeAssignmentStatement(code, 1, Checks.DjangoClickjackMiddlewareCheck, "settings.py", DjangoMiddlewareInspection())
    }

    @Test
    fun `test django settings with empty middleware`(){
        var code = """
            MIDDLEWARE = [
            ]
        """.trimIndent()
        testCodeAssignmentStatement(code, 1, Checks.DjangoClickjackMiddlewareCheck, "settings.py", DjangoMiddlewareInspection())
        testCodeAssignmentStatement(code, 1, Checks.DjangoCsrfMiddlewareCheck, "settings.py", DjangoMiddlewareInspection())
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
        testCodeAssignmentStatement(code, 0, Checks.DjangoClickjackMiddlewareCheck, "settings.py", DjangoMiddlewareInspection())
    }
}