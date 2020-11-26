package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StandardShellInjectionInspectionTest: SecurityTestTask() {
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
        assertFalse(StandardShellInjectionInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test os_system call`(){
        val code = """
            import os
            os.system("ls {0}".format(x))
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.ShellInjectionCheck, "test.py", StandardShellInjectionInspection())
    }

    @Test
    fun `test popen2 list call`(){
        val code = """
            import popen2
            popen2.popen3(["ls", x])
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.ShellInjectionCheck, "test.py", StandardShellInjectionInspection())
    }

    @Test
    fun `test os_system call constant`(){
        val code = """
            import os
            os.system("ls foo")
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ShellInjectionCheck, "test.py", StandardShellInjectionInspection())
    }

    @Test
    fun `test os_system call quoted`(){
        val code = """
            import os
            import shlex
            os.system(shlex.quote(x))
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ShellInjectionCheck, "test.py", StandardShellInjectionInspection())
    }

    @Test
    fun `test popen2 list constant`(){
        val code = """
            import popen2
            popen2.popen3(["ls", "x"])
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ShellInjectionCheck, "test.py", StandardShellInjectionInspection())
    }

    @Test
    fun `test popen2 list quoted`(){
        val code = """
            import popen2
            import shlex
            popen2.popen3(["ls", shlex.quote(x)])
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ShellInjectionCheck, "test.py", StandardShellInjectionInspection())
    }

    @Test
    fun `test system no args`(){
        val code = """
            import os
            os.system()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ShellInjectionCheck, "test.py", StandardShellInjectionInspection())
    }

    @Test
    fun `test other function`(){
        val code = """
            import os
            os.path()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ShellInjectionCheck, "test.py", StandardShellInjectionInspection())
    }
}