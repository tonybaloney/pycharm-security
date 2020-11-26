package security.validators

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
        val code = """
            secret = "my secret key"
        """.trimIndent()
        testCodeAssignmentStatement(code, 1, Checks.HardcodedPasswordCheck, "test.py", HardcodedPasswordInspection())
    }

    @Test
    fun `test hardcoded password`(){
        val code = """
            password = "my password"
        """.trimIndent()
        testCodeAssignmentStatement(code, 1, Checks.HardcodedPasswordCheck, "test.py", HardcodedPasswordInspection())
    }

    @Test
    fun `test does not fire on call expression`(){
        val code = """
            password = get_password()
        """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.HardcodedPasswordCheck, "test.py", HardcodedPasswordInspection())
    }

    @Test
    fun `test no left hand`(){
        val code = """
            = "my password"
        """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.HardcodedPasswordCheck, "test.py", HardcodedPasswordInspection())
    }

    @Test
    fun `test no right hand`(){
        val code = """
           password = 
        """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.HardcodedPasswordCheck, "test.py", HardcodedPasswordInspection())
    }
}