package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TimingAttackInspectionTest: SecurityTestTask() {
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
        assertFalse(TimingAttackInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test match of password comparator left`(){
        var code = """
            password = "BANANA"
            if password == "BANANA":
                pass
        """.trimIndent()
        testBinaryExpression(code, 1, Checks.TimingAttackCheck, "test.py", TimingAttackInspection())
    }

    @Test
    fun `test match of password comparator right`(){
        var code = """
            password = "BANANA"
            if "BANANA" == password:
                pass
        """.trimIndent()
        testBinaryExpression(code, 1, Checks.TimingAttackCheck, "test.py", TimingAttackInspection())
    }

    @Test
    fun `test skip of normal comparator`(){
        var code = """
            var = "BANANA"
            if "BANANA" == var:
                pass
        """.trimIndent()
        testBinaryExpression(code, 0, Checks.TimingAttackCheck, "test.py", TimingAttackInspection())
    }

    @Test
    fun `test skip of is None`(){
        var code = """
            var = "BANANA"
            if "BANANA" is None:
                pass
        """.trimIndent()
        testBinaryExpression(code, 0, Checks.TimingAttackCheck, "test.py", TimingAttackInspection())
    }

    @Test
    fun `test skip of plus`(){
        var code = """
            var = "BANANA"
            if "BANANA" + hammock:
                pass
        """.trimIndent()
        testBinaryExpression(code, 0, Checks.TimingAttackCheck, "test.py", TimingAttackInspection())
    }

    @Test
    fun `test include of not equal`(){
        var code = """
            var = "BANANA"
            if "BANANA" != password:
                pass
        """.trimIndent()
        testBinaryExpression(code, 1, Checks.TimingAttackCheck, "test.py", TimingAttackInspection())
    }
}