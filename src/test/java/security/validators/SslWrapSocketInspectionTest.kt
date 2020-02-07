package security.validators

import com.jetbrains.python.PythonLanguage
import com.jetbrains.python.psi.LanguageLevel
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SslWrapSocketInspectionTest: SecurityTestTask() {
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
        assertFalse(SslWrapSocketInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test no wrap socket`() {
        var code = """
            import ssl
            
            ssl.wrap_pocket(ssl_version=ssl.PROTOCOL_SSLv3)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.SslBadProtocolsCheck, "test.py", SslWrapSocketInspection())
    }

    @Test
    fun `test not ssl qn`() {
        var code = """
            import banana
            
            banana.wrap_socket(ssl_version=ssl.PROTOCOL_SSLv3)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.SslBadProtocolsCheck, "test.py", SslWrapSocketInspection())
    }

    @Test
    fun `test bad protocol`() {
        var code = """
            import ssl
            
            ssl.wrap_socket(ssl_version=ssl.PROTOCOL_SSLv3)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.SslBadProtocolsCheck, "test.py", SslWrapSocketInspection())
    }

    @Test
    fun `test no default`() {
        var code = """
            import ssl
            
            ssl.wrap_socket()
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.SslWrapSocketNoVersionCheck, "test.py", SslWrapSocketInspection())
    }

    @Test
    fun `test no default old python`() {
        var code = """
            import ssl
            
            ssl.wrap_socket()
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.SslWrapSocketNoVersionCheck, "test.py", SslWrapSocketInspection())
    }

    @Test
    fun `test none version`() {
        var code = """
            import ssl
            
            ssl.wrap_socket(ssl_version=None)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.SslWrapSocketNoVersionCheck, "test.py", SslWrapSocketInspection())
    }

    @Test
    fun `test good setting`() {
        var code = """
            import ssl
            
            ssl.wrap_socket(ssl_version=ssl.PROTOCOL_TLS)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.SslBadProtocolsCheck, "test.py", SslWrapSocketInspection())
    }

    @Test
    fun `test not reference`() {
        var code = """
            import ssl
            
            ssl.wrap_socket(ssl_version=1)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.SslBadProtocolsCheck, "test.py", SslWrapSocketInspection())
    }
}