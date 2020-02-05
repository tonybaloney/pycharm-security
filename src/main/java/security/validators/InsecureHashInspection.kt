package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyStringLiteralExpression
import security.Checks
import security.helpers.QualifiedNames.getQualifiedName

class InsecureHashInspection : PyInspection() {
    val check = Checks.InsecureHashAlgorithms

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {

        val badHashAlgorithms = arrayOf("md4", "md5", "sha", "sha1")
        val lengthAttackHashAlgorithms = arrayOf("md5", "sha1", "ripemd160", "sha256", "sha512", "whirlpool")

        override fun visitPyCallExpression(node: PyCallExpression) {
            checkHashAlgorithmsByNew(node, badHashAlgorithms, Checks.InsecureHashAlgorithms)
            checkHashAlgorithmsByNew(node, lengthAttackHashAlgorithms, Checks.LengthAttackHashAlgorithms)
            checkHashAlgorithmsByImport(node, badHashAlgorithms, Checks.InsecureHashAlgorithms)
            checkHashAlgorithmsByImport(node, lengthAttackHashAlgorithms, Checks.LengthAttackHashAlgorithms)
        }

        fun checkHashAlgorithmsByNew(node: PyCallExpression, algorithms: Array<String>, check: Checks.CheckType) {
            val calleeName = node.callee?.name ?: return
            if (calleeName != "new") return
            val qualifiedName = getQualifiedName(node) ?: return
            if (qualifiedName.startsWith("hashlib.").not()) return
            if (node.arguments.isEmpty()) return
            val nameKwArg = node.getKeywordArgument("name")
            val firstArg = node.arguments[0]
            if (nameKwArg != null && nameKwArg is PyStringLiteralExpression) {
                if (listOf(*algorithms).contains((nameKwArg).stringValue))
                    holder?.registerProblem(node, check.getDescription())
            } else if (firstArg is PyStringLiteralExpression) {
                if (listOf(*algorithms).contains((firstArg).stringValue))
                    holder?.registerProblem(node, check.getDescription())
            }
        }

        fun checkHashAlgorithmsByImport(node: PyCallExpression, algorithms: Array<String>, check: Checks.CheckType) {
            val calleeName = node.callee?.name ?: return
            if (listOf(*algorithms).contains(calleeName).not()) return
            val qualifiedName = getQualifiedName(node) ?: return
            if (qualifiedName.startsWith("hashlib."))
                holder?.registerProblem(node, check.getDescription())
        }
    }
}