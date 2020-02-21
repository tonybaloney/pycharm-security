package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyDictLiteralExpression
import security.Checks
import security.helpers.*

class DjangoExtraSqlInspection : PyInspection() {
    val check = Checks.DjangoExtraSqlCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        val methodNames = arrayOf("extra")
        override fun visitPyCallExpression(node: PyCallExpression) {
            if (skipDocstring(node)) return
            if (!calleeMatches(node, methodNames)) return
            if (!hasImportedNamespace(node.containingFile, "django")) return

            // Look at the where argument
            if (node.getKeywordArgument("where") != null){
                val whereArg = node.getKeywordArgument("where")
                if (whereArg is PyDictLiteralExpression) {
                    whereArg.elements
                            .filter { it.value != null }
                            .forEach { inspectStatement(it.value!!, holder, Checks.DjangoExtraSqlCheck) }
                }
            }
        }
    }
}