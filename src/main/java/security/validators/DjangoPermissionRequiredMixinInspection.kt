package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyAssignmentStatement
import com.jetbrains.python.psi.PyClass
import com.jetbrains.python.psi.PyFunction
import com.jetbrains.python.psi.types.TypeEvalContext
import security.Checks
import security.helpers.SecurityVisitor
import security.registerProblem

class DjangoPermissionRequiredMixinInspection : PyInspection() {
    val check = Checks.DjangoPermissionRequiredMixinCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        val mixinName = "django.contrib.auth.mixins.PermissionRequiredMixin"

        override fun visitPyClass(node: PyClass) {
            val typeContext = TypeEvalContext.codeAnalysis(node.project, node.containingFile)
            val superClasses= node.getSuperClasses(typeContext)

            if (!superClasses.filter { it.qualifiedName != null }
                             .any { mixinName == it.qualifiedName })
                return

            if (superClasses.first().qualifiedName != mixinName)
                holder.registerProblem(node, Checks.DjangoPermissionRequiredMixinOrderCheck)

            val hasRequiredPermissionClassProperty = node.statementList.children.
                    filter { it is PyAssignmentStatement &&
                             it.isAssignmentTo("permission_required") &&
                             it.assignedValue != null }.any()
            val hasHasPermissionFunction = node.statementList.children.
                    filter { it is PyFunction &&
                             it.name == "has_permission" }.any()
            val hasGetRequiredPermissionFunction = node.statementList.children.
                    filter { it is PyFunction &&
                            it.name == "get_permission_required" }.any()

            if (!hasRequiredPermissionClassProperty && !hasHasPermissionFunction && !hasGetRequiredPermissionFunction)
                holder.registerProblem(node, Checks.DjangoPermissionRequiredMixinCheck)
        }
    }
}
