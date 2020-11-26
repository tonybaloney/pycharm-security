package security.validators

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
        val code = """
            import ssl
            
            ssl.wrap_pocket(ssl_version=ssl.PROTOCOL_SSLv3)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.SslBadProtocolsCheck, "test.py", SslWrapSocketInspection())
    }

    @Test
    fun `test not ssl qn`() {
        val code = """
            import banana
            
            banana.wrap_socket(ssl_version=ssl.PROTOCOL_SSLv3)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.SslBadProtocolsCheck, "test.py", SslWrapSocketInspection())
    }

    @Test
    fun `test bad protocol`() {
        val code = """
            import ssl
            
            ssl.wrap_socket(ssl_version=ssl.PROTOCOL_SSLv3)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.SslBadProtocolsCheck, "test.py", SslWrapSocketInspection())
    }

    @Test
    fun `test no default`() {
        val code = """
            import ssl
            
            ssl.wrap_socket()
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.SslWrapSocketNoVersionCheck, "test.py", SslWrapSocketInspection())
    }

    @Test
    fun `test no default old python`() {
        val code = """
            import ssl
            
            ssl.wrap_socket()
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.SslWrapSocketNoVersionCheck, "test.py", SslWrapSocketInspection())
    }

    @Test
    fun `test none version`() {
        val code = """
            import ssl
            
            ssl.wrap_socket(ssl_version=None)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.SslWrapSocketNoVersionCheck, "test.py", SslWrapSocketInspection())
    }

    @Test
    fun `test good setting`() {
        val code = """
            import ssl
            
            ssl.wrap_socket(ssl_version=ssl.PROTOCOL_TLS)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.SslBadProtocolsCheck, "test.py", SslWrapSocketInspection())
    }

    @Test
    fun `test not reference`() {
        val code = """
            import ssl
            
            ssl.wrap_socket(ssl_version=1)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.SslBadProtocolsCheck, "test.py", SslWrapSocketInspection())
    }
}