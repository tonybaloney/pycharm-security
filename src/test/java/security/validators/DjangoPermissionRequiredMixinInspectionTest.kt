package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DjangoPermissionRequiredMixinInspectionTest: SecurityTestTask() {
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
        assertFalse(DjangoPermissionRequiredMixinInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test valid mixin`(){
        var code = """
            from django.contrib.auth.mixins import PermissionRequiredMixin
            from django.views.generic import DetailView
            
            
            class MyView(PermissionRequiredMixin, DetailView):
                permission_required = "core.view_user"
        """.trimIndent()
        testCodeClass(code, 0, Checks.DjangoPermissionRequiredMixinCheck, "test.py", DjangoPermissionRequiredMixinInspection())
    }

    @Test
    fun `test invalid order mixin`(){
        var code = """
            from django.contrib.auth.mixins import PermissionRequiredMixin
            from django.views.generic import DetailView
            
            
            class MyView(DetailView, PermissionRequiredMixin):
                permission_required = "core.view_user"
        """.trimIndent()
        testCodeClass(code, 1, Checks.DjangoPermissionRequiredMixinOrderCheck, "test.py", DjangoPermissionRequiredMixinInspection())
    }

    @Test
    fun `test missing property mixin`(){
        var code = """
            from django.contrib.auth.mixins import PermissionRequiredMixin
            from django.views.generic import DetailView
            
            
            class MyView(PermissionRequiredMixin, DetailView):
                permission_required_xxxx = "core.view_user"
        """.trimIndent()
        testCodeClass(code, 1, Checks.DjangoPermissionRequiredMixinCheck, "test.py", DjangoPermissionRequiredMixinInspection())
    }
}