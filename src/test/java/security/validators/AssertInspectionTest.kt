package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AssertInspectionTest: SecurityTestTask() {
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
        assertFalse(AssertInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test assert in test file`(){
        var code = """
            assert 1 == 1
        """.trimIndent()
        testAssertStatement(code, 0, Checks.AssertCheck, "test_foo.py", AssertInspection())
    }

    @Test
    fun `test assert in non test file`(){
        var code = """
            assert 1 == 1
        """.trimIndent()
        testAssertStatement(code, 1, Checks.AssertCheck, "my_file.py", AssertInspection())
    }
}