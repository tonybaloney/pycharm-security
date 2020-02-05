package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyPassStatement
import com.jetbrains.python.psi.PyStatementList
import com.jetbrains.python.psi.PyTryExceptStatement
import security.Checks

class TryExceptPassInspection : PyInspection() {
    val check = Checks.TryExceptPassCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {
        override fun visitPyTryExceptStatement(node: PyTryExceptStatement?) {
            if (node == null) return
            if (node.containingFile.name.contains("test")) return
            if (node.exceptParts.isEmpty()) return

            // Raise a warning for the specific except block
            for (part in node.exceptParts){
                if (part.children.isEmpty()) continue
                val statements = part.children.filterIsInstance<PyStatementList>()
                if (statements.isNullOrEmpty()) continue
                // Check except block contains something other than comments and a pass statement
                if (statements.first().statements.any{ it !is PyPassStatement && it !is PsiComment}) continue
                holder?.registerProblem(part, Checks.TryExceptPassCheck.getDescription(), ProblemHighlightType.WEAK_WARNING)
            }
        }
    }
}