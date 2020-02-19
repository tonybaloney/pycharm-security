package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyBoolLiteralExpression
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyReferenceExpression
import security.Checks
import security.helpers.SecurityVisitor
import security.helpers.calleeMatches
import security.helpers.hasImportedNamespace
import security.helpers.skipDocstring

class FlaskDebugModeInspection : PyInspection() {
    val check = Checks.FlaskDebugModeCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            if (skipDocstring(node)) return
            if (!calleeMatches(node, "run")) return
            if (!hasImportedNamespace(node.containingFile, "flask")) return
            if (node.firstChild !is PyReferenceExpression) return
            if ((node.firstChild as PyReferenceExpression).asQualifiedName().toString() != "app.run") return
            val debugArg = node.getKeywordArgument("debug") ?: return
            if (debugArg !is PyBoolLiteralExpression) return
            if (!debugArg.value) return
            holder.registerProblem(node, Checks.FlaskDebugModeCheck.getDescription())
        }
    }
}