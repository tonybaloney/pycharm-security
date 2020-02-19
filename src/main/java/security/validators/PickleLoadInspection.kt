package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyCallExpression
import security.Checks
import security.helpers.SecurityVisitor
import security.helpers.qualifiedNameMatches
import security.helpers.skipDocstring

class PickleLoadInspection : PyInspection() {
    val check = Checks.PickleLoadCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            if (skipDocstring(node)) return
            val pickleLoadNames = arrayOf("pickle.load", "pickle.loads", "cPickle.load", "cPickle.loads", "pickle._load", "pickle._loads", "cPickle._load", "cPickle._loads")
            if (qualifiedNameMatches(node, pickleLoadNames))
                holder.registerProblem(node, Checks.PickleLoadCheck.getDescription())
        }
    }
}