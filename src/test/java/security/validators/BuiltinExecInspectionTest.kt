package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BuiltinExecInspectionTest: SecurityTestTask() {
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
        assertFalse(BuiltinExecInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test exec no args `(){
        val code = """
            exec()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.BuiltinExecCheck, "test.py", BuiltinExecInspection())
    }

    @Test
    fun `test string literal arg`(){
        val code = """
            exec('do this')
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.BuiltinExecCheck, "test.py", BuiltinExecInspection())
    }

    @Test
    fun `test variable arg`(){
        val code = """
            exec(x)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.BuiltinExecCheck, "test.py", BuiltinExecInspection())
    }

    @Test
    fun `test random name`(){
        val code = """
            xexecec()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.BuiltinExecCheck, "test.py", BuiltinExecInspection())
    }

    @Test
    fun `test qn mismatch`(){
        val code = """
            import some_libary
            some_library.exec(f)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.BuiltinExecCheck, "test.py", BuiltinExecInspection())
    }
}