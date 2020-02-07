package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyReferenceExpression
import security.Checks
import security.fixes.PyyamlSafeLoadFixer
import security.helpers.QualifiedNames.getQualifiedName
import security.helpers.SecurityVisitor

class PyyamlLoadInspection : PyInspection() {
    val check = Checks.PyyamlUnsafeLoadCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            val calleeName = node.callee?.name ?: return
            if (calleeName != "load") return
            val qualifiedName = getQualifiedName(node) ?: return
            if (qualifiedName != "yaml.load") return
            // Inspect loader kwarg
            val loaderArg = node.getKeywordArgument("Loader")
            if (loaderArg != null && loaderArg is PyReferenceExpression)
                if (loaderArg.referencedName == "SafeLoader") return

            holder.registerProblem(node, Checks.PyyamlUnsafeLoadCheck.getDescription(), PyyamlSafeLoadFixer())
        }
    }
}