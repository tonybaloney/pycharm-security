package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParamikoHostKeyBypassInspectionTest: SecurityTestTask() {
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
        assertFalse(ParamikoHostkeyBypassInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test no args`(){
        val code = """
            import paramiko.client
            client = paramiko.client.SSHClient()
            client.set_missing_host_key_policy()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ParamikoHostkeyBypassCheck, "test.py", ParamikoHostkeyBypassInspection())
    }

    @Test
    fun `test RejectPolicy`(){
        val code = """
            import paramiko.client
            client = paramiko.client.SSHClient()
            client.set_missing_host_key_policy(paramiko.client.RejectPolicy)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ParamikoHostkeyBypassCheck, "test.py", ParamikoHostkeyBypassInspection())
    }

    @Test
    fun `test WarningPolicy`(){
        val code = """
            import paramiko.client
            client = paramiko.client.SSHClient()
            client.set_missing_host_key_policy(paramiko.client.WarningPolicy)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.ParamikoHostkeyBypassCheck, "test.py", ParamikoHostkeyBypassInspection())
    }

    @Test
    fun `test AutoAddPolicy`(){
        val code = """
            import paramiko.client
            client = paramiko.client.SSHClient()
            client.set_missing_host_key_policy(paramiko.client.AutoAddPolicy)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.ParamikoHostkeyBypassCheck, "test.py", ParamikoHostkeyBypassInspection())
    }
}