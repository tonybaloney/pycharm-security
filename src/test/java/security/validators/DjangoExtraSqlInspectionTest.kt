package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DjangoExtraSqlInspectionTest: SecurityTestTask() {
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
        assertFalse(DjangoExtraSqlInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test quoted string`(){
        var code = """
            import django.db.models.query
            django.db.models.query.QuerySet.extra(
                select={'val': "select col from sometable where othercol = '%s'"},
                select_params=(someparam,),
            )
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.DjangoExtraSqlCheck, "test.py", DjangoExtraSqlInspection())
    }

    @Test
    fun `test format non quoted string`(){
        var code = """
            import django.db.models.query
            django.db.models.query.QuerySet.extra(
                select={'val': "select col from sometable where othercol = %s"},
                select_params=(someparam,),
            )
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoExtraSqlCheck, "test.py", DjangoExtraSqlInspection())
    }

    @Test
    fun `test model raw with missing quote at beginning`(){
        var code = """
            import django.db.models.query
            django.db.models.query.QuerySet.extra(
                select={'val': "%s'"},
                select_params=(someparam,),
            )
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoExtraSqlCheck, "test.py", DjangoExtraSqlInspection())
    }

    @Test
    fun `test model raw with missing quote at end`(){
        var code = """
            import django.db.models.query
            django.db.models.query.QuerySet.extra(
                select={'val': "'%s"},
                select_params=(someparam,),
            )
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoExtraSqlCheck, "test.py", DjangoExtraSqlInspection())
    }

    @Test
    fun `test model raw with nothing else`(){
        var code = """
            import django.db.models.query
            django.db.models.query.QuerySet.extra()
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoExtraSqlCheck, "test.py", DjangoExtraSqlInspection())
    }

    @Test
    fun `test extra not query set`(){
        var code = """
            extra(
                select={'val': "select col from sometable where othercol = '%s'"},
                select_params=(someparam,),
            )
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoExtraSqlCheck, "test.py", DjangoExtraSqlInspection())
    }

    @Test
    fun `test not extra not query set`(){
        var code = """
            not_extra(
                select={'val': "select col from sometable where othercol = '%s'"},
                select_params=(someparam,),
            )
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoExtraSqlCheck, "test.py", DjangoExtraSqlInspection())
    }
}