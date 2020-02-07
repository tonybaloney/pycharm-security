package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParamikoExecCommandInspectionTest: SecurityTestTask() {
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
        assertFalse(ParamikoExecCommandInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test shell string literal is ok`(){
        var code = """
            import paramiko.client
            client = paramiko.client.SSHClient()
            client.exec_command('rm -rf /')
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ParamikoExecCommandCheck, "test.py", ParamikoExecCommandInspection())
    }

    @Test
    fun `test no args`(){
        var code = """
            import paramiko.client
            client = paramiko.client.SSHClient()
            client.exec_command()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ParamikoExecCommandCheck, "test.py", ParamikoExecCommandInspection())
    }

    @Test
    fun `test invalid first arg`(){
        var code = """
            import paramiko.client
            client = paramiko.client.SSHClient()
            client.exec_command(None)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ParamikoExecCommandCheck, "test.py", ParamikoExecCommandInspection())
    }

    @Test
    fun `test call not quote arg`(){
        var code = """
            import paramiko.client
            client = paramiko.client.SSHClient()
            client.exec_command(meep())
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ParamikoExecCommandCheck, "test.py", ParamikoExecCommandInspection())
    }

    @Test
    fun `test shell string format is bad`(){
        var code = """
            import paramiko.client
            client = paramiko.client.SSHClient()
            client.exec_command('rm -rf / {}'.format(xx))
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.ParamikoExecCommandCheck, "test.py", ParamikoExecCommandInspection())
    }

    @Test
    fun `test channel format is bad`(){
        var code = """
            import paramiko.client
            client = paramiko.client.SSHClient()
            channel = client.invoke_shell()
            channel.exec_command('rm -rf / {}'.format(xx))
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.ParamikoExecCommandCheck, "test.py", ParamikoExecCommandInspection())
    }

    @Test
    fun `test paramiko not imported`(){
        var code = """
            import blahblah.client
            client = blahblah.client.SSHClient()
            client.exec_command('rm -rf / {}'.format(xx))
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ParamikoExecCommandCheck, "test.py", ParamikoExecCommandInspection())
    }

    @Test
    fun `test quoted input is ok`(){
        var code = """
            import paramiko.client
            import shlex
            client = paramiko.client.SSHClient()
            client.exec_command(shlex.quote(xx))
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ParamikoExecCommandCheck, "test.py", ParamikoExecCommandInspection())
    }
}