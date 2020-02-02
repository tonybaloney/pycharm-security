package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PyyamlLoadInspectionTest: SecurityTestTask() {
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
        assertFalse(PyyamlLoadInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test yaml load`(){
        var code = """
            import yaml
            yaml.load()
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.PyyamlUnsafeLoadCheck, "test.py", PyyamlLoadInspection())
    }

    @Test
    fun `test yaml load with args`(){
        var code = """
            import yaml
            yaml.load(f)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.PyyamlUnsafeLoadCheck, "test.py", PyyamlLoadInspection())
    }

    @Test
    fun `test not yaml qn load`(){
        var code = """
            import yaml
            waml.load()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.PyyamlUnsafeLoadCheck, "test.py", PyyamlLoadInspection())
    }

    @Test
    fun `test yaml not load`(){
        var code = """
            import yaml
            yaml.loooad()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.PyyamlUnsafeLoadCheck, "test.py", PyyamlLoadInspection())
    }

    @Test
    fun `test yaml safe_load`(){
        var code = """
            import yaml
            yaml.safe_load()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.PyyamlUnsafeLoadCheck, "test.py", PyyamlLoadInspection())
    }

    @Test
    fun `test yaml load with args and safeloader skips`(){
        var code = """
            import yaml
            yaml.load(f, loader=yaml.SafeLoader)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.PyyamlUnsafeLoadCheck, "test.py", PyyamlLoadInspection())
    }

    @Test
    fun `test yaml load with args and invalid safeloader fires`(){
        var code = """
            import yaml
            yaml.load(f, loader=None)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.PyyamlUnsafeLoadCheck, "test.py", PyyamlLoadInspection())
    }

    @Test
    fun `test yaml load with args and normal loader fires`(){
        var code = """
            import yaml
            yaml.load(f, loader=yaml.Loader)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.PyyamlUnsafeLoadCheck, "test.py", PyyamlLoadInspection())
    }
}