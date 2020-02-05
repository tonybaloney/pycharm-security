package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DjangoDebugModeSettingsInspectorTest : SecurityTestTask() {

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
        assertFalse(DjangoDebugModeSettingsInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test django settings file with debug mode on`(){
        var code = """
            DEBUG = True
        """.trimIndent()
        testCodeAssignmentStatement(code, 1, Checks.DjangoDebugModeCheck, "settings.py", DjangoDebugModeSettingsInspection())
    }

    @Test
    fun `test django settings file with debug mode off`(){
        var code = """
            DEBUG = False
        """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.DjangoDebugModeCheck, "settings.py", DjangoDebugModeSettingsInspection())
    }

    @Test
    fun `test django settings with no debug mode`(){
        var code = """
            X = 1
        """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.DjangoDebugModeCheck, "settings.py", DjangoDebugModeSettingsInspection())
    }

    @Test
    fun `test debug true in another file name`(){
        var code = """
            DEBUG = True
        """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.DjangoDebugModeCheck, "test.py", DjangoDebugModeSettingsInspection())
    }

    @Test
    fun `test no left hand`(){
        var code = """
            = True
        """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.DjangoDebugModeCheck, "test.py", DjangoDebugModeSettingsInspection())
    }

    @Test
    fun `test no right hand`(){
        var code = """
           DEBUG = 
        """.trimIndent()
        testCodeAssignmentStatement(code, 0, Checks.DjangoDebugModeCheck, "test.py", DjangoDebugModeSettingsInspection())
    }
}