package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TryExceptContinueInspectionTest: SecurityTestTask() {
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
        assertFalse(TryExceptContinueInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test stmt in no except`(){
        var code = """
            try:
                x = 1
                
        """.trimIndent()
        testTryExceptStatement(code, 0, Checks.TryExceptContinueCheck, "test_foo.py", TryExceptContinueInspection())
    }

    @Test
    fun `test stmt no statements in except`(){
        var code = """
            try:
                x = 1
            except:
        """.trimIndent()
        testTryExceptStatement(code, 0, Checks.TryExceptContinueCheck, "test_foo.py", TryExceptContinueInspection())
    }

    @Test
    fun `test stmt in test file`(){
        var code = """
            try:
                x = 1
            except:
                continue
        """.trimIndent()
        testTryExceptStatement(code, 0, Checks.TryExceptContinueCheck, "test_foo.py", TryExceptContinueInspection())
    }

    @Test
    fun `test stmt in non test file`(){
        var code = """
            try:
                x = 1
            except:
                continue
        """.trimIndent()
        testTryExceptStatement(code, 1, Checks.TryExceptContinueCheck, "my_file.py", TryExceptContinueInspection())
    }

    @Test
    fun `test stmt with 2 excepts`(){
        var code = """
            try:
                x = 1
            except RuntimeException:
                continue
            except Exception:
                continue
        """.trimIndent()
        testTryExceptStatement(code, 1, Checks.TryExceptContinueCheck, "my_file.py", TryExceptContinueInspection())
    }

    @Test
    fun `test stmt with comments`(){
        var code = """
            try:
                x = 1
            except Exception:
                # do nothing
                continue
        """.trimIndent()
        testTryExceptStatement(code, 1, Checks.TryExceptContinueCheck, "my_file.py", TryExceptContinueInspection())
    }

    @Test
    fun `test stmt with actual statements`(){
        var code = """
            try:
                x = 1
            except Exception:
                x = 2
                continue
        """.trimIndent()
        testTryExceptStatement(code, 0, Checks.TryExceptContinueCheck, "my_file.py", TryExceptContinueInspection())
    }
}