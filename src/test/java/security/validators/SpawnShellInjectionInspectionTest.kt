package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpawnShellInjectionInspectionTest: SecurityTestTask() {
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
        assertFalse(SpawnShellInjectionInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test bad call`(){
        val code = """
            import os
            os.execle(x, y, z)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.SpawnShellInjectionCheck, "test.py", SpawnShellInjectionInspection())
    }

    @Test
    fun `test no arguments call`(){
        val code = """
            import os
            os.execle()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.SpawnShellInjectionCheck, "test.py", SpawnShellInjectionInspection())
    }

    @Test
    fun `test other call`(){
        val code = """
            import os
            os.path()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.SpawnShellInjectionCheck, "test.py", SpawnShellInjectionInspection())
    }
}