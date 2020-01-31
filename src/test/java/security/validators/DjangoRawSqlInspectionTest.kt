package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DjangoRawSqlInspectionTest: SecurityTestTask() {
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
        assertFalse(DjangoRawSqlInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test quoted string`(){
        var code = """
            import django.db.models.expressions
            x = "injectable string"
            django.db.models.expressions.RawSQL("SELECT * FROM foo WHERE ID = '%s'", (x,))
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.DjangoRawSqlCheck, "test.py", DjangoRawSqlInspection())
    }

    @Test
    fun `test format non quoted string`(){
        var code = """
            import django.db.models.expressions
            x = "injectable string"
            django.db.models.expressions.RawSQL("SELECT * FROM foo WHERE ID = %s", (x,))
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoRawSqlCheck, "test.py", DjangoRawSqlInspection())
    }

    @Test
    fun `test some other rawsql method`(){
        var code = """
            RawSQL("SELECT * FROM foo WHERE ID = '%s'", (x,))
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoRawSqlCheck, "test.py", DjangoRawSqlInspection())
    }

    @Test
    fun `test cursor execute with no quotes`(){
        var code = """
            from django.db import connection

            def my_custom_sql(self):
                with connection.cursor() as cursor:
                    cursor.execute("UPDATE bar SET foo = 1 WHERE baz = %s", [self.baz])
                    cursor.execute("SELECT foo FROM bar WHERE baz = %s", [self.baz])
                    row = cursor.fetchone()
                return row
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoRawSqlCheck, "test.py", DjangoRawSqlInspection())
    }

    @Test
    fun `test cursor execute with quotes`(){
        var code = """
            from django.db import connection

            def my_custom_sql(self):
                with connection.cursor() as cursor:
                    cursor.execute("UPDATE bar SET foo = 1 WHERE baz = %s", [self.baz])
                    cursor.execute("SELECT foo FROM bar WHERE baz = '%s'", [self.baz])
                    row = cursor.fetchone()
                return row
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.DjangoRawSqlCheck, "test.py", DjangoRawSqlInspection())
    }

    @Test
    fun `test model raw with quotes`(){
        var code = """
            from django.db import connection
            from .models import User

            def my_view(self):
                User.objects.raw("SELECT * FROM myapp_person WHERE last_name = '%s'", [lname])
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.DjangoRawSqlCheck, "test.py", DjangoRawSqlInspection())
    }

    @Test
    fun `test model raw with no quotes`(){
        var code = """
            from django.db import connection
            from .models import User

            def my_view(self):
                User.objects.raw("SELECT * FROM myapp_person WHERE last_name = %s", [lname])
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoRawSqlCheck, "test.py", DjangoRawSqlInspection())
    }
}