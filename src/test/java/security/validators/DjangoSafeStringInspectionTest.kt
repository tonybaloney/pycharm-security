package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DjangoSafeStringInspectionTest: SecurityTestTask() {
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
        assertFalse(DjangoSafeStringInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test mark_safe string`(){
        val code = """
            import django.utils.safestring

            mystr = '<b>Hello World</b>'
            mystr = django.utils.safestring.mark_safe(mystr)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.DjangoSafeStringCheck, "test.py", DjangoSafeStringInspection())
    }

    @Test
    fun `test mark_safe falsePositiveString`(){
        val code = """
            import django.utils.safestring

            mystr = django.utils.safestring.mark_safe('<b>Hello World</b>')
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoSafeStringCheck, "test.py", DjangoSafeStringInspection())
    }

    @Test
    fun `test mark_safe kwargsFalsePositiveString`(){
        val code = """
            import django.utils.safestring

            mystr = django.utils.safestring.mark_safe(s='<b>Hello World</b>')
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoSafeStringCheck, "test.py", DjangoSafeStringInspection())
    }

    @Test
    fun `test SafeString string`(){
        val code = """
            import django.utils.safestring

            mystr = '<b>Hello World</b>'
            mystr = django.utils.safestring.SafeString(mystr)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.DjangoSafeStringCheck, "test.py", DjangoSafeStringInspection())
    }

    @Test
    fun `test SafeText string`(){
        val code = """
            import django.utils.safestring

            mystr = '<b>Hello World</b>'
            mystr = django.utils.safestring.SafeText(mystr)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.DjangoSafeStringCheck, "test.py", DjangoSafeStringInspection())
    }

    @Test
    fun `test SafeUnicode string`(){
        val code = """
            import django.utils.safestring

            mystr = u'<b>Hello World</b>'
            mystr = django.utils.safestring.SafeUnicode(mystr)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.DjangoSafeStringCheck, "test.py", DjangoSafeStringInspection())
    }

    @Test
    fun `test SafeBytes string`(){
        val code = """
            import django.utils.safestring

            mystr = b'<b>Hello World</b>'
            mystr = django.utils.safestring.SafeBytes(mystr)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.DjangoSafeStringCheck, "test.py", DjangoSafeStringInspection())
    }

    @Test
    fun `test no import`(){
        val code = """
            mystr = b'<b>Hello World</b>'
            mystr = SafeBytes(mystr)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoSafeStringCheck, "test.py", DjangoSafeStringInspection())
    }

    @Test
    fun `test other method`(){
        val code = """
            import django.utils.safestring

            mystr = b'<b>Hello World</b>'
            mystr = django.utils.safestring.SomeOtherMethod(mystr)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoSafeStringCheck, "test.py", DjangoSafeStringInspection())
    }
}