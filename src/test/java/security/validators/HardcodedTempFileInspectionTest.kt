package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HardcodedTempFileInspectionTest: SecurityTestTask() {
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
        assertFalse(HardcodedTempFileInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test temp file open`(){
        var code = """
            open("/tmp/my_path")
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.HardcodedTempFileCheck, "test.py", HardcodedTempFileInspection())
    }

    @Test
    fun `test temp file dynamic path open`(){
        var code = """
            import tempfile
            open(tempfile.mkstemp())
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.HardcodedTempFileCheck, "test.py", HardcodedTempFileInspection())
    }

    @Test
    fun `test open no args`(){
        var code = """
            open()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.HardcodedTempFileCheck, "test.py", HardcodedTempFileInspection())
    }

    @Test
    fun `test open not builtin`(){
        var code = """
            import door
            door.open()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.HardcodedTempFileCheck, "test.py", HardcodedTempFileInspection())
    }

    @Test
    fun `test open normal path`(){
        var code = """
            open('/path/to/normal')
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.HardcodedTempFileCheck, "test.py", HardcodedTempFileInspection())
    }
}