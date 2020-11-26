package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpxNoVerifyInspectionTest: SecurityTestTask() {
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
        assertFalse(HttpxNoVerifyInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test the get method with no verify`() {
        val code = """
            import httpx
            
            httpx.get(url, verify=False)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.HttpxNoVerifyCheck, "test.py", HttpxNoVerifyInspection())
    }

    @Test
    fun `test the get method with verify`() {
        val code = """
            import httpx
            
            httpx.get(url, verify=True)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.HttpxNoVerifyCheck, "test.py", HttpxNoVerifyInspection())
    }

    @Test
    fun `test the post method with no verify`() {
        val code = """
            import httpx
            
            httpx.post(url, verify=False)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.HttpxNoVerifyCheck, "test.py", HttpxNoVerifyInspection())
    }

    @Test
    fun `test the post method with verify`() {
        val code = """
            import httpx
            
            httpx.post(url, verify=True)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.HttpxNoVerifyCheck, "test.py", HttpxNoVerifyInspection())
    }

    @Test
    fun `test the options method with no verify`() {
        val code = """
            import httpx
            
            httpx.options(url, verify=False)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.HttpxNoVerifyCheck, "test.py", HttpxNoVerifyInspection())
    }

    @Test
    fun `test the put method with no verify`() {
        val code = """
            import httpx
            
            httpx.put(url, verify=False)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.HttpxNoVerifyCheck, "test.py", HttpxNoVerifyInspection())
    }

    @Test
    fun `test the patch method with no verify`() {
        val code = """
            import httpx
            
            httpx.patch(url, verify=False)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.HttpxNoVerifyCheck, "test.py", HttpxNoVerifyInspection())
    }

    @Test
    fun `test httpx import with get and no arguments`() {
        val code = """
            from httpx import get
            
            get(url)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.HttpxNoVerifyCheck, "test.py", HttpxNoVerifyInspection())
    }

    @Test
    fun `test httpx import with get and a verify true argument`() {
        val code = """
            from httpx import get
            
            get(url, verify=True)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.HttpxNoVerifyCheck, "test.py", HttpxNoVerifyInspection())
    }

    @Test
    fun `test wrong qualified path`() {
        val code = """
            import httpx
            
            httpxxxx.patch(url, verify=False)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.HttpxNoVerifyCheck, "test.py", HttpxNoVerifyInspection())
    }

    @Test
    fun `test non boolean literal verify`() {
        val code = """
            import httpx
            
            httpx.patch(url, verify='banana')
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.HttpxNoVerifyCheck, "test.py", HttpxNoVerifyInspection())
    }
}