package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StrFormatInspectionTest: SecurityTestTask() {
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
        assertFalse(StrFormatInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test format string`(){
        val code = """
            def format_event(format_string, event):
                return format_string.format(event=event)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.StrFormatInspectionCheck, "test.py", StrFormatInspection())
    }

    @Test
    fun `test format f-string`(){
        val code = """
            def format_event(format_string, event):
                return f'Event {format_string}'.format(event=event)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.StrFormatInspectionCheck, "test.py", StrFormatInspection())
    }

    @Test
    fun `test format falsePositiveString`(){
        val code = """
            'Event: {}'.format(event=event)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.StrFormatInspectionCheck, "test.py", StrFormatInspection())
    }

    @Test
    fun `test format class string`(){
        val code = """
            class Formatter:
                def __init___(self, formatter):
                    self.formatter = formatter
                def format_event(self, event):
                    return self.formatter.format(event)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.StrFormatInspectionCheck, "test.py", StrFormatInspection())
    }

    @Test
    fun `test format class falsePositiveString`(){
        val code = """
            class Formatter:
                def format(self, item):
                    return str(item)
                def format_event(self, event):
                    return self.format(event)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.StrFormatInspectionCheck, "test.py", StrFormatInspection())
    }
}