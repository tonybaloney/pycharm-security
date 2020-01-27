package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InsecureHashInspectionTest: SecurityTestTask() {
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
        assertFalse(InsecureHashInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test new hash with insecure algorithm`(){
        var code = """
            import hashlib
            hashlib.new('sha')
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.InsecureHashAlgorithms, "test.py", InsecureHashInspection())
    }

    @Test
    fun `test new hash with insecure algorithm kwarg`(){
        var code = """
            import hashlib
            hashlib.new(name='sha')
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.InsecureHashAlgorithms, "test.py", InsecureHashInspection())
    }

    @Test
    fun `test new hash with more-secure algorithm`(){
        var code = """
            import hashlib
            hashlib.new('blake2')
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.InsecureHashAlgorithms, "test.py", InsecureHashInspection())
    }

    @Test
    fun `test new hash with type import`(){
        var code = """
            import hashlib
            hashlib.sha()
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.InsecureHashAlgorithms, "test.py", InsecureHashInspection())
    }

    @Test
    fun `test new hash with non-string argument`(){
        var code = """
            import hashlib
            hashlib.new(1)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.InsecureHashAlgorithms, "test.py", InsecureHashInspection())
    }

    @Test
    fun `test new hash with length-attack algorithm kwarg`(){
        var code = """
            import hashlib
            hashlib.new(name='whirlpool')
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.LengthAttackHashAlgorithms, "test.py", InsecureHashInspection())
    }

    @Test
    fun `test new hash with length-attack algorithm type import`(){
        var code = """
            import hashlib
            hashlib.whirlpool()
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.LengthAttackHashAlgorithms, "test.py", InsecureHashInspection())
    }

    @Test
    fun `test new hash with length-attack algorithm`(){
        var code = """
            import hashlib
            hashlib.new('whirlpool')
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.LengthAttackHashAlgorithms, "test.py", InsecureHashInspection())
    }

    @Test
    fun `test secure algorithm`(){
        var code = """
            import hashlib
            hashlib.new('blake2')
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.InsecureHashAlgorithms, "test.py", InsecureHashInspection())
    }

    @Test
    fun `test not new method`(){
        var code = """
            import hashlib
            hashlib.pew('blake2')
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.InsecureHashAlgorithms, "test.py", InsecureHashInspection())
    }

    @Test
    fun `test no args`(){
        var code = """
            import hashlib
            hashlib.new()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.InsecureHashAlgorithms, "test.py", InsecureHashInspection())
    }

    @Test
    fun `test first arg non string literal`(){
        var code = """
            import hashlib
            hashlib.new(1)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.InsecureHashAlgorithms, "test.py", InsecureHashInspection())
    }

    @Test
    fun `test named arg non string literal`(){
        var code = """
            import hashlib
            hashlib.new(name=1)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.InsecureHashAlgorithms, "test.py", InsecureHashInspection())
    }
}