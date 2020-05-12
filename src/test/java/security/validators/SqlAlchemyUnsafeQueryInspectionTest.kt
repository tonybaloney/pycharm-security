package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SqlAlchemyUnsafeQueryInspectionTest: SecurityTestTask() {
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
        assertFalse(SqlAlchemyUnsafeQueryInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test unsafe text`() {
        var code = """
            import sqlalchemy
            
            sqlalchemy.text(data)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.SqlAlchemyUnsafeQueryCheck, "test.py", SqlAlchemyUnsafeQueryInspection())
    }

    @Test
    fun `test safe argument`() {
        var code = """
            import sqlalchemy
            
            sqlalchemy.text(data)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.SqlAlchemyUnsafeQueryCheck, "test.py", SqlAlchemyUnsafeQueryInspection())
    }

    @Test
    fun `test complex text`() {
        var code = """
            import sqlalchemy
            
            session.query(User).filter(User.id == 1).filter(text(part)).all()
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.SqlAlchemyUnsafeQueryCheck, "test.py", SqlAlchemyUnsafeQueryInspection())
    }

    @Test
    fun `test unsafe suffix`() {
        var code = """
            import sqlalchemy
            
            select([users.c.name]).where(users.c.id == 1).suffix_with(suffix, dialect="sqlite")
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.SqlAlchemyUnsafeQueryCheck, "test.py", SqlAlchemyUnsafeQueryInspection())
    }

    @Test
    fun `test text not sqlalchemy`(){
        var code = """
            import bicycle
            
            select([users.c.name]).where(users.c.id == 1).suffix_with(suffix, dialect="sqlite")
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.SqlAlchemyUnsafeQueryCheck, "test.py", SqlAlchemyUnsafeQueryInspection())
    }

    @Test
    fun `test sqlalchemy not text`(){
        var code = """
            import sqlalchemy
            
            vext(x)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.SqlAlchemyUnsafeQueryCheck, "test.py", SqlAlchemyUnsafeQueryInspection())
    }
}