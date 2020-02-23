package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyStringLiteralExpression
import security.Checks
import security.helpers.*
import security.registerProblem

class DjangoExtraSqlInspection : PyInspection() {
    val check = Checks.DjangoExtraSqlCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        override fun visitPyCallExpression(node: PyCallExpression) {
            if (skipDocstring(node)) return
            if (!calleeMatches(node, "extra")) return
            if (!qualifiedNameMatches(node, "django.db.models.query.QuerySet.extra")) return

            val keywordArgumentsToInspect = arrayOf("where", "select", "tables", "order_by", "params")
            keywordArgumentsToInspect
                    .filter { node.getKeywordArgument(it) != null }
                    .map { node.getKeywordArgument(it) }
                    .map { PsiTreeUtil.findChildrenOfType(it, PyStringLiteralExpression::class.java) }
                    .forEach { strings ->
                        strings.forEach {
                            if (inspectDjangoSqlTemplate(it)) holder.registerProblem(it, Checks.DjangoExtraSqlCheck)
                        }
                    }
        }
    }
}