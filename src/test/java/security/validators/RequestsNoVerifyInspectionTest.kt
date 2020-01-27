package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestsNoVerifyInspectionTest: SecurityTestTask() {
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
        assertFalse(RequestsNoVerifyInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test the get method with no verify`() {
        var code = """
            import requests
            
            requests.get(url, verify=False)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.RequestsNoVerifyCheck, "test.py", RequestsNoVerifyInspection())
    }

    @Test
    fun `test the get method with verify`() {
        var code = """
            import requests
            
            requests.get(url, verify=True)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.RequestsNoVerifyCheck, "test.py", RequestsNoVerifyInspection())
    }

    @Test
    fun `test the post method with no verify`() {
        var code = """
            import requests
            
            requests.post(url, verify=False)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.RequestsNoVerifyCheck, "test.py", RequestsNoVerifyInspection())
    }

    @Test
    fun `test the post method with verify`() {
        var code = """
            import requests
            
            requests.post(url, verify=True)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.RequestsNoVerifyCheck, "test.py", RequestsNoVerifyInspection())
    }

    @Test
    fun `test the options method with no verify`() {
        var code = """
            import requests
            
            requests.options(url, verify=False)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.RequestsNoVerifyCheck, "test.py", RequestsNoVerifyInspection())
    }

    @Test
    fun `test the put method with no verify`() {
        var code = """
            import requests
            
            requests.put(url, verify=False)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.RequestsNoVerifyCheck, "test.py", RequestsNoVerifyInspection())
    }

    @Test
    fun `test the patch method with no verify`() {
        var code = """
            import requests
            
            requests.patch(url, verify=False)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.RequestsNoVerifyCheck, "test.py", RequestsNoVerifyInspection())
    }

    @Test
    fun `test requests import with get and no arguments`() {
        var code = """
            from requests import get
            
            get(url)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.RequestsNoVerifyCheck, "test.py", RequestsNoVerifyInspection())
    }

    @Test
    fun `test requests import with get and a verify true argument`() {
        var code = """
            from requests import get
            
            get(url, verify=True)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.RequestsNoVerifyCheck, "test.py", RequestsNoVerifyInspection())
    }


    @Test
    fun `test wrong qualified path`() {
        var code = """
            import requests
            
            requestssss.patch(url, verify=False)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.RequestsNoVerifyCheck, "test.py", RequestsNoVerifyInspection())
    }

    @Test
    fun `test non boolean literal verify`() {
        var code = """
            import requests
            
            requests.patch(url, verify='banana')
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.RequestsNoVerifyCheck, "test.py", RequestsNoVerifyInspection())
    }
}