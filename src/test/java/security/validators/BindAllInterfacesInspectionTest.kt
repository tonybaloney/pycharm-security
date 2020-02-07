package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BindAllInterfacesInspectionTest: SecurityTestTask() {
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
        assertFalse(BindAllInterfacesInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test bind no args`(){
        var code = """
            bind()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.BindAllInterfacesCheck, "test.py", BindAllInterfacesInspection())
    }

    @Test
    fun `test not bind no args`(){
        var code = """
            mind()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.BindAllInterfacesCheck, "test.py", BindAllInterfacesInspection())
    }

    @Test
    fun `test 0000 string`(){
        var code = """
            bind("0.0.0.0")
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.BindAllInterfacesCheck, "test.py", BindAllInterfacesInspection())
    }

    @Test
    fun `test other string`(){
        var code = """
            bind("1.2.3.4")
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.BindAllInterfacesCheck, "test.py", BindAllInterfacesInspection())
    }
    @Test
    fun `test ipv6 string`(){
        var code = """
            bind("::")
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.BindAllInterfacesCheck, "test.py", BindAllInterfacesInspection())
    }

    @Test
    fun `test tuple ipv4`(){
        var code = """
            bind(("0.0.0.0", 80))
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.BindAllInterfacesCheck, "test.py", BindAllInterfacesInspection())
    }

    @Test
    fun `test tuple ipv6`(){
        var code = """
            bind(("::", 80))
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.BindAllInterfacesCheck, "test.py", BindAllInterfacesInspection())
    }

    @Test
    fun `test tuple other`(){
        var code = """
            bind(("1.0.0.0", 80))
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.BindAllInterfacesCheck, "test.py", BindAllInterfacesInspection())
    }
}