package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.*
import security.Checks
import security.helpers.SecurityVisitor
import security.helpers.skipDocstring

class SqlInjectionInspection : PyInspection() {
    val check = Checks.SqlInjectionCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        // Double-word SQL commands (high-certainty)
        val certainlySqlStartingStrings = arrayOf("INSERT INTO ", "DELETE FROM", "ALTER TABLE ", "DROP DATABASE ", "CREATE DATABASE ")
        // Double-word SQL commands (low-certainty)
        val possiblySqlCommandPairs = mapOf("SELECT " to " FROM ", "UPDATE " to " SET ")

        fun looksLikeSql(str: String) : Boolean {
            // Quickly respond to double-worded SQL statements
            if (certainlySqlStartingStrings.any { str.uppercase().startsWith(it) }) return true

            // SELECT must contain FROM, and UPDATE must contain SET
            possiblySqlCommandPairs.forEach { pair ->
                if (str.uppercase().startsWith(pair.key) && str.uppercase().contains(pair.value))
                    return true
            }
            return false
        }

        override fun visitPyFormattedStringElement(node: PyFormattedStringElement) {
            if (skipDocstring(node)) return

            // F-string
            if (!looksLikeSql(node.content)) return
            holder.registerProblem(node, Checks.SqlInjectionCheck.getDescription())
        }

        override fun visitPyStringLiteralExpression(node: PyStringLiteralExpression) {
            if (skipDocstring(node)) return

            if (!looksLikeSql(node.stringValue)) return

            // .Format() string
            if (node.parent is PyReferenceExpression){
                if ((node.parent as PyReferenceExpression).name != "format") return
                if (node.parent.parent == null) return
                if (node.parent.parent !is PyCallExpression) return
                holder.registerProblem(node, Checks.SqlInjectionCheck.getDescription())
            }

            // % format string
            if (node.parent is PyBinaryExpression) {
                if ((node.parent as PyBinaryExpression).operator.toString() != "Py:PERC") return
                holder.registerProblem(node, Checks.SqlInjectionCheck.getDescription())
            }
        }
    }
}