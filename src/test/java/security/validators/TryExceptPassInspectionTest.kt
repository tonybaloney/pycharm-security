package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TryExceptPassInspectionTest: SecurityTestTask() {
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
        assertFalse(TryExceptPassInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test stmt in no except`(){
        val code = """
            try:
                x = 1
                
        """.trimIndent()
        testTryExceptStatement(code, 0, Checks.TryExceptPassCheck, "test_foo.py", TryExceptPassInspection())
    }

    @Test
    fun `test stmt no statements in except`(){
        val code = """
            try:
                x = 1
            except:
        """.trimIndent()
        testTryExceptStatement(code, 0, Checks.TryExceptPassCheck, "test_foo.py", TryExceptPassInspection())
    }

    @Test
    fun `test stmt in test file`(){
        val code = """
            try:
                x = 1
            except:
                pass
        """.trimIndent()
        testTryExceptStatement(code, 0, Checks.TryExceptPassCheck, "test_foo.py", TryExceptPassInspection())
    }

    @Test
    fun `test stmt in non test file`(){
        val code = """
            try:
                x = 1
            except:
                pass
        """.trimIndent()
        testTryExceptStatement(code, 1, Checks.TryExceptPassCheck, "my_file.py", TryExceptPassInspection())
    }

    @Test
    fun `test stmt with 2 excepts`(){
        val code = """
            try:
                x = 1
            except RuntimeException:
                pass
            except Exception:
                pass
        """.trimIndent()
        testTryExceptStatement(code, 1, Checks.TryExceptPassCheck, "my_file.py", TryExceptPassInspection())
    }

    @Test
    fun `test stmt with comments`(){
        val code = """
            try:
                x = 1
            except Exception:
                # do nothing
                pass
        """.trimIndent()
        testTryExceptStatement(code, 1, Checks.TryExceptPassCheck, "my_file.py", TryExceptPassInspection())
    }

    @Test
    fun `test stmt with actual statements`(){
        val code = """
            try:
                x = 1
            except Exception:
                x = 2
                pass
        """.trimIndent()
        testTryExceptStatement(code, 0, Checks.TryExceptPassCheck, "my_file.py", TryExceptPassInspection())
    }
}