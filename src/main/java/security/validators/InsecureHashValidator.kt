package security.validators

import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyStringLiteralExpression
import com.jetbrains.python.validation.PyAnnotator
import security.Checks
import security.helpers.QualifiedNames.getQualifiedName

class InsecureHashValidator : PyAnnotator() {
    val badHashAlgorithms = arrayOf("md4", "md5", "sha", "sha1")
    val lengthAttackHashAlgorithms = arrayOf("md5", "sha1", "ripemd160", "sha256", "sha512", "whirlpool")

    override fun visitPyCallExpression(node: PyCallExpression) {
        checkHashAlgorithmsByNew(node, badHashAlgorithms, Checks.InsecureHashAlgorithms)
        checkHashAlgorithmsByNew(node, lengthAttackHashAlgorithms, Checks.LengthAttackHashAlgorithms)
        checkHashAlgorithmsByImport(node, badHashAlgorithms, Checks.InsecureHashAlgorithms)
        checkHashAlgorithmsByImport(node, lengthAttackHashAlgorithms, Checks.LengthAttackHashAlgorithms)
    }

    fun checkHashAlgorithmsByNew(node: PyCallExpression, algorithms: Array<String>, check: Checks.CheckType){
        val calleeName = node.callee?.name ?: return
        if (calleeName != "new") return
        val qualifiedName = getQualifiedName(node) ?: return
        if (qualifiedName.startsWith("hashlib.").not()) return
        if (node.arguments.isEmpty()) return
        var nameKwArg = node.getKeywordArgument("name")
        var firstArg = node.arguments[0]
        try {
            if (nameKwArg != null) {
                if (listOf(*algorithms).contains((nameKwArg as PyStringLiteralExpression).stringValue).not()) return
            } else {
                if (listOf(*algorithms).contains((firstArg as PyStringLiteralExpression).stringValue).not()) return
            }
            holder.createWarningAnnotation(node, check.toString())
        } catch (c: ClassCastException){
            return
        }
    }

    fun checkHashAlgorithmsByImport(node: PyCallExpression, algorithms: Array<String>, check: Checks.CheckType){
        val calleeName = node.callee?.name ?: return
        if (listOf(*algorithms).contains(calleeName).not()) return
        val qualifiedName = getQualifiedName(node) ?: return
        if (qualifiedName.startsWith("hashlib.").not()) return
        holder.createWarningAnnotation(node, check.toString())
    }
}