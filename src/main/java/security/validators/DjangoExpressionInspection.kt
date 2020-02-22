package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyAssignmentStatement
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyClass
import com.jetbrains.python.psi.PyStringLiteralExpression
import com.jetbrains.python.psi.types.TypeEvalContext
import security.Checks
import security.helpers.*
import security.registerProblem

class DjangoExpressionInspection : PyInspection() {
    val check = Checks.DjangoExpressionCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        val expressionTypes = arrayOf("Func", "Aggregate", "Window", "Expression", "Transform")
        val extraMethods = arrayOf("as_sql")
        val namespace = "django.db.models"

        override fun visitPyCallExpression(node: PyCallExpression) {
            if (skipDocstring(node)) return
            if (!calleeMatches(node, expressionTypes + extraMethods)) return
            if (!qualifiedNameStartsWith(node, namespace)) return

            if (node.arguments.isNullOrEmpty()) return
            val templateStatement = node.getKeywordArgument("template") ?: return
            if (templateStatement !is PyStringLiteralExpression) return
            if (inspectDjangoSqlTemplate(templateStatement))
                holder.registerProblem(templateStatement, Checks.DjangoExpressionCheck)
        }

        override fun visitPyClass(node: PyClass) {
            val typeContext = TypeEvalContext.codeAnalysis(node.project, node.containingFile)
            val superClasses= node.getSuperClasses(typeContext)
            // See if it inherits from any of the expression types APIs
            if (!superClasses.filter { it.qualifiedName != null }
                            .filter { it.qualifiedName!!.startsWith(namespace) }
                            .any { expressionTypes.contains(it.name) })
                return
            val templateStatements = node.statementList.children.
                    filter { it is PyAssignmentStatement && it.isAssignmentTo("template") && it.assignedValue != null && inspectDjangoSqlTemplate(it.assignedValue!!)}
            for (statement in templateStatements)
                holder.registerProblem(statement as PyAssignmentStatement, Checks.DjangoExpressionCheck)
        }
    }
}
