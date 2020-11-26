package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PickleLoadInspectionTest: SecurityTestTask() {
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
        assertFalse(PickleLoadInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test pickle load`() {
        val code = """
            import pickle
            
            pickle.load(data)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.PickleLoadCheck, "test.py", PickleLoadInspection())
    }

    @Test
    fun `test pickle loads`() {
        val code = """
            import pickle
            
            pickle.loads(data)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.PickleLoadCheck, "test.py", PickleLoadInspection())
    }

    @Test
    fun `test cpickle load`() {
        val code = """
            import cPickle
            
            cPickle.loads(data)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.PickleLoadCheck, "test.py", PickleLoadInspection())
    }

    @Test
    fun `test cpickle loads`() {
        val code = """
            import cPickle
            
            cPickle.load(data)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.PickleLoadCheck, "test.py", PickleLoadInspection())
    }

    @Test
    fun `test load not pickle`(){
        val code = """
            import bicycle
            
            bicycle.load(data)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.PickleLoadCheck, "test.py", PickleLoadInspection())
    }

    @Test
    fun `test pickle not load`(){
        val code = """
            import pickle
            
            pickle.toad(data)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.PickleLoadCheck, "test.py", PickleLoadInspection())
    }
}