package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TempfileMktempInspectionTest: SecurityTestTask() {
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
        assertFalse(TempfileMktempInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test temp file with insecure make`(){
        val code = """
            import tempfile
            tempfile.mktemp()
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.TempfileMktempCheck, "test.py", TempfileMktempInspection())
    }

    @Test
    fun `test temp file with make (safe) temp`(){
        val code = """
            import tempfile
            tempfile.mkstemp()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.TempfileMktempCheck, "test.py", TempfileMktempInspection())
    }

    @Test
    fun `test temp file with other lib`(){
        val code = """
            import nottempfile
            nottempfile.mktemp()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.TempfileMktempCheck, "test.py", TempfileMktempInspection())
    }
}