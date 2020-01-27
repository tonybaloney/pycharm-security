package security.validators

import com.jetbrains.python.inspections.PyInspection
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HardcodedPasswordInspectorTest : SecurityTestTask() {

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
        assertFalse(HardcodedPasswordInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test hardcoded secret`(){
        var code = """
            secret = "my secret key"
        """.trimIndent()
        testCodeAssignmentStatement(code, 1, Checks.HardcodedPasswordCheck, "test.py", HardcodedPasswordInspection())
    }

    @Test
    fun `test hardcoded password`(){
        var code = """
            password = "my password"
        """.trimIndent()
        testCodeAssignmentStatement(code, 1, Checks.HardcodedPasswordCheck, "test.py", HardcodedPasswordInspection())
    }

    @Test
    fun `test does not fire on call expression`(){
        var code = """
            password = get_password()
        """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.HardcodedPasswordCheck, "test.py", HardcodedPasswordInspection())
    }

    @Test
    fun `test no left hand`(){
        var code = """
            = "my password"
        """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.HardcodedPasswordCheck, "test.py", HardcodedPasswordInspection())
    }

    @Test
    fun `test no right hand`(){
        var code = """
           password = 
        """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.HardcodedPasswordCheck, "test.py", HardcodedPasswordInspection())
    }
}