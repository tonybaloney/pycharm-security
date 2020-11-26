package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MakoTemplateInspectionTest: SecurityTestTask() {
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
        assertFalse(MakoTemplateInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test mako template no autoescape`(){
        val code = """
            import mako.template
            t = mako.Template("my template")
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.MakoTemplateFilterCheck, "test.py", MakoTemplateInspection())
    }

    @Test
    fun `test mako with existin autoescape to list`(){
        val code = """
            import mako.template
            env = mako.template.Template("SDFSDF", default_filters=['h'])
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.MakoTemplateFilterCheck, "test.py", MakoTemplateInspection())
    }

    @Test
    fun `test not mako template call`(){
        val code = """
            env = Template("SDFSDF", default_filters=['h'])
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.MakoTemplateFilterCheck, "test.py", MakoTemplateInspection())
    }

    @Test
    fun `test mako not template call`(){
        val code = """
            import mako.template
            env = Templateeeeee("SDFSDF", default_filters=['h'])
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.MakoTemplateFilterCheck, "test.py", MakoTemplateInspection())
    }
}