package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyCallExpression
import security.Checks
import security.fixes.TempfileMksFixer
import security.helpers.QualifiedNames.getQualifiedName

class TempfileMktempInspection : PyInspection() {

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            val calleeName = node.callee?.name ?: return
            if (calleeName != "mktemp") return
            val qualifiedName = getQualifiedName(node) ?: return
            if (qualifiedName != "tempfile.mktemp") return
            holder?.registerProblem(node, Checks.TempfileMktempCheck.Message, TempfileMksFixer())
        }
    }
}