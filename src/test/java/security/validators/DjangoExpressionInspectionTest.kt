package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DjangoExpressionInspectionTest: SecurityTestTask() {
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
        assertFalse(DjangoExpressionInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test quoted string`(){
        var code = """
            from django.db.models import F, Func
            queryset.annotate(field_lower=django.db.models.Func(F('field'), function='LOWER', template="'%(function)s'(%(expressions)s)"))
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.DjangoExpressionCheck, "test.py", DjangoExpressionInspection())
    }

    @Test
    fun `test quoted string in class`(){
        var code = """
            from django.db.models import F, Func
            class ConcatPair(django.db.models.Func):
                function = 'CONCAT'
            
                def as_mysql(self, compiler, connection, **extra_context):
                    return django.db.models.Func.as_sql(
                        compiler, connection,
                        function='CONCAT_WS',
                        template="'%s'",
                        **extra_context
                    )
                    """.trimIndent()
        testCodeCallExpression(code, 1, Checks.DjangoExpressionCheck, "test.py", DjangoExpressionInspection())
    }

    @Test
    fun `test non quoted string`(){
        var code = """
            from django.db.models import F, Func
            queryset.annotate(field_lower=django.db.models.Func(F('field'), function='LOWER', template="%(function)s(%(expressions)s)"))
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoExpressionCheck, "test.py", DjangoExpressionInspection())
    }

    @Test
    fun `test no template argument`(){
        var code = """
            from django.db.models import F, Func
            queryset.annotate(field_lower=django.db.models.Func(F('field'), function='LOWER'))
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoExpressionCheck, "test.py", DjangoExpressionInspection())
    }

    @Test
    fun `test template argument not string literal`(){
        var code = """
            from django.db.models import F, Func
            queryset.annotate(field_lower=django.db.models.Func(F('field'), function='LOWER', template=xxx))
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.DjangoExpressionCheck, "test.py", DjangoExpressionInspection())
    }

//    @Test
////    fun `test expression child`(){
////        var code = """
////            import django.db.models
////            class Example(django.db.models.Expression):
////                function = 'EXAMPLE'
////                template = "%(function)('%(special)s')"
////        """.trimIndent()
////        testCodeClass(code, 1, Checks.DjangoExpressionCheck, "test.py", DjangoExpressionInspection())
////    }

    @Test
    fun `test expression child not child of target`(){
        var code = """
            from django.db.models import Expression
            class Example(SomethingElse):
                function = 'EXAMPLE'
                template = "%(function)('%(special)s')"
        """.trimIndent()
        testCodeClass(code, 0, Checks.DjangoExpressionCheck, "test.py", DjangoExpressionInspection())
    }

    @Test
    fun `test no template attribute`(){
        var code = """
            from django.db.models import Expression
            class Example(django.db.models.Expression):
                function = 'EXAMPLE'
        """.trimIndent()
        testCodeClass(code, 0, Checks.DjangoExpressionCheck, "test.py", DjangoExpressionInspection())
    }

    @Test
    fun `test template attribute not string literal`(){
        var code = """
            from django.db.models import Expression
            class Example(django.db.models.Expression):
                function = 'EXAMPLE'
                template = x()
        """.trimIndent()
        testCodeClass(code, 0, Checks.DjangoExpressionCheck, "test.py", DjangoExpressionInspection())
    }
}