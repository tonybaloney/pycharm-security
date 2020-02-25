package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyKeywordArgument
import com.jetbrains.python.psi.PyStringLiteralExpression
import security.Checks
import security.helpers.SecurityVisitor
import security.helpers.calleeMatches
import security.helpers.qualifiedNameStartsWith
import security.helpers.skipDocstring

class DjangoSafeStringInspection : PyInspection() {
    val check = Checks.DjangoSafeStringCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        val methodNames = arrayOf("SafeString", "mark_safe", "SafeBytes", "SafeUnicode", "SafeText", "do_mark_safe")
        val methodKwargs = mapOf("mark_safe" to "s", "do_mark_safe" to "value")
        override fun visitPyCallExpression(node: PyCallExpression) {
            if (node.arguments.isEmpty()) return
            if (skipDocstring(node)) return
            if (!calleeMatches(node, methodNames)) return
            if (!qualifiedNameStartsWith(node, arrayOf("django.utils.safestring", "jinja2.filters"))) return
            var arg = node.arguments[0]
            if (arg is PyKeywordArgument){
                if(!methodKwargs.containsKey(node.callee?.name)) return
                if(methodKwargs.get(node.callee?.name) != arg.keyword) return
                arg = arg.valueExpression
            }
            if (arg is PyStringLiteralExpression) return
            holder.registerProblem(node, Checks.DjangoSafeStringCheck.getDescription(), ProblemHighlightType.WEAK_WARNING)
        }
    }
}