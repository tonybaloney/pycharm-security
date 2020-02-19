package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyCallExpression
import security.Checks
import security.fixes.TempfileMksFixer
import security.helpers.SecurityVisitor
import security.helpers.calleeMatches
import security.helpers.qualifiedNameMatches
import security.helpers.skipDocstring

class TempfileMktempInspection : PyInspection() {
    val check = Checks.TempfileMktempCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            if (skipDocstring(node)) return

            if (!calleeMatches(node, "mktemp")) return
            if (!qualifiedNameMatches(node, "tempfile.mktemp")) return
            holder.registerProblem(node, Checks.TempfileMktempCheck.getDescription(), TempfileMksFixer())
        }
    }
}