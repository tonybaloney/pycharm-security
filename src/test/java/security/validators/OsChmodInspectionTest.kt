package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OsChmodInspectionTest: SecurityTestTask() {
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
        assertFalse(OsChmodInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test with octal bad`(){
        val code = """
            import os
            os.chmod('x', 0o777)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.ChmodInsecurePermissionsCheck, "test.py", OsChmodInspection())
    }

    @Test
    fun `test with octal good`(){
        val code = """
            import os
            os.chmod('x', 0o300)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ChmodInsecurePermissionsCheck, "test.py", OsChmodInspection())
    }

    @Test
    fun `test with single statref bad`(){
        val code = """
            import os
            import stat
            os.chmod('x', stat.S_IXGRP)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.ChmodInsecurePermissionsCheck, "test.py", OsChmodInspection())
    }

    @Test
    fun `test with single statref good`(){
        val code = """
            import os
            os.chmod('x', stat.S_IRGRP)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ChmodInsecurePermissionsCheck, "test.py", OsChmodInspection())
    }

    @Test
    fun `test with single binary or good`(){
        val code = """
            import os
            os.chmod('x', stat.S_IRGRP | stat.S_IRGRP)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ChmodInsecurePermissionsCheck, "test.py", OsChmodInspection())
    }

    @Test
    fun `test with single binary or bad`(){
        val code = """
            import os
            os.chmod('x', stat.S_IRGRP | stat.S_IXGRP)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.ChmodInsecurePermissionsCheck, "test.py", OsChmodInspection())
    }

    @Test
    fun `test with multi binary or good`(){
        val code = """
            import os
            os.chmod('x', stat.S_IRGRP | stat.S_IRGRP | stat.S_IRUSR)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.ChmodInsecurePermissionsCheck, "test.py", OsChmodInspection())
    }

    @Test
    fun `test with multi binary or bad`(){
        val code = """
            import os
            os.chmod('x', stat.S_IRGRP | stat.S_IRUSR | stat.S_IXGRP)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.ChmodInsecurePermissionsCheck, "test.py", OsChmodInspection())
    }
}