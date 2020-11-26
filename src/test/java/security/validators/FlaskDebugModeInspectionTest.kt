package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks.FlaskDebugModeCheck
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FlaskDebugModeInspectionTest: SecurityTestTask() {
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
        assertFalse(FlaskDebugModeInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test flask app debug mode on`(){
        val code = """
            from flask import Flask
            
            app = Flask()
            app.run(debug=True)
        """.trimIndent()
        testCodeCallExpression(code, 1, FlaskDebugModeCheck, "test.py", FlaskDebugModeInspection())
    }

    @Test
    fun `test flask app with debug mode off`(){
        val code = """
            from flask import Flask
            
            app = Flask()
            app.run(debug=False)
        """.trimIndent()
        testCodeCallExpression(code, 0, FlaskDebugModeCheck, "test.py", FlaskDebugModeInspection())
    }

    @Test
    fun `test flask app with debug mode not bool`(){
        val code = """
            from flask import Flask
            
            app = Flask()
            app.run(debug='banana')
        """.trimIndent()
        testCodeCallExpression(code, 0, FlaskDebugModeCheck, "test.py", FlaskDebugModeInspection())
    }

    @Test
    fun `test flask with no mention of debug mode`(){
        val code = """
            from flask import Flask
            
            app = Flask()
            app.run()
        """.trimIndent()
        testCodeCallExpression(code, 0, FlaskDebugModeCheck, "test.py", FlaskDebugModeInspection())
    }

    @Test
    fun `test flask not run method`(){
        val code = """
            from flask import Flask
            
            app = Flask()
            app.fun()
        """.trimIndent()
        testCodeCallExpression(code, 0, FlaskDebugModeCheck, "test.py", FlaskDebugModeInspection())
    }

    @Test
    fun `test flask not app run`(){
        val code = """
            from flask import Flask
            
            app = Flask()
            blah.run()
        """.trimIndent()
        testCodeCallExpression(code, 0, FlaskDebugModeCheck, "test.py", FlaskDebugModeInspection())
    }

    @Test
    fun `test flask not imported`(){
        val code = """
            from banana import make
            
            app = Flask()
            blah.run()
        """.trimIndent()
        testCodeCallExpression(code, 0, FlaskDebugModeCheck, "test.py", FlaskDebugModeInspection())
    }

    @Test
    fun `test not reference parent`(){
        val code = """
            from flask import Flask
            "1".run()
        """.trimIndent()
        testCodeCallExpression(code, 0, FlaskDebugModeCheck, "test.py", FlaskDebugModeInspection())
    }
}