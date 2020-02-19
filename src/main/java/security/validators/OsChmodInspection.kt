package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyBinaryExpression
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyNumericLiteralExpression
import com.jetbrains.python.psi.PyReferenceExpression
import security.Checks
import security.helpers.SecurityVisitor
import security.helpers.calleeMatches
import security.helpers.skipDocstring
import java.nio.file.attribute.PosixFilePermission

fun getPosixPermissions(permValue: Int): Set<PosixFilePermission>? {
    // I don't think a version of this exists in the JDK stdlib?!
    val permissions = arrayListOf<PosixFilePermission>()
    if ((1 and permValue) > 0) permissions.add(PosixFilePermission.OWNER_EXECUTE)
    if (2 and permValue > 0) permissions.add(PosixFilePermission.OWNER_WRITE)
    if (4 and permValue > 0) permissions.add(PosixFilePermission.OWNER_READ)
    if (8 and permValue > 0) permissions.add(PosixFilePermission.GROUP_EXECUTE)
    if (16 and permValue > 0) permissions.add(PosixFilePermission.GROUP_WRITE)
    if (32 and permValue > 0) permissions.add(PosixFilePermission.GROUP_READ)
    if (64 and permValue > 0) permissions.add(PosixFilePermission.OTHERS_EXECUTE)
    if (128 and permValue > 0) permissions.add(PosixFilePermission.OTHERS_WRITE)
    if (256 and permValue > 0) permissions.add(PosixFilePermission.OTHERS_READ)
    return permissions.toSet()
}

class OsChmodInspection : PyInspection() {
    val check = Checks.ChmodInsecurePermissionsCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        val badPermissions = setOf(PosixFilePermission.OTHERS_EXECUTE, PosixFilePermission.OTHERS_WRITE, PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_EXECUTE)
        val pythonStatValues: HashMap<String, PosixFilePermission> = hashMapOf(
                "S_IRUSR" to PosixFilePermission.OWNER_READ,
                "S_IWUSR" to PosixFilePermission.OWNER_WRITE,
                "S_IXUSR" to PosixFilePermission.OWNER_EXECUTE,
                "S_IRGRP" to PosixFilePermission.GROUP_READ,
                "S_IWGRP" to PosixFilePermission.GROUP_WRITE,
                "S_IXGRP" to PosixFilePermission.GROUP_EXECUTE,
                "S_IROTH" to PosixFilePermission.OTHERS_READ,
                "S_IWOTH" to PosixFilePermission.OTHERS_WRITE,
                "S_IXOTH" to PosixFilePermission.OTHERS_EXECUTE
                )

        private fun isBad(ref: PyReferenceExpression): Boolean {
            if (!pythonStatValues.containsKey(ref.referencedName)) return false
            return (badPermissions.contains(pythonStatValues[ref.referencedName]))
        }

        private fun hasBad(expr: PyBinaryExpression) : Boolean {
            if (expr.operator.toString() != "Py:OR") return false
            if (expr.leftExpression is PyReferenceExpression){
                if (isBad(expr.leftExpression as PyReferenceExpression))
                    return true
            }
            if (expr.rightExpression is PyReferenceExpression){
                if (isBad(expr.rightExpression as PyReferenceExpression))
                    return true
            } else if (expr.rightExpression is PyBinaryExpression){
                return hasBad(expr.rightExpression as PyBinaryExpression)
            }
            return false
        }

        override fun visitPyCallExpression(node: PyCallExpression) {
            if (skipDocstring(node)) return
            if (!calleeMatches(node, "chmod")) return

            if (node.arguments.isEmpty() || node.arguments.size <= 1) return
            var modeArg = node.getKeywordArgument("mode")
            if (modeArg == null)
                modeArg = node.arguments[1]

            // Reference expression to stat.xxx
            if (modeArg is PyReferenceExpression){
                if (isBad(modeArg))
                    holder.registerProblem(node, Checks.ChmodInsecurePermissionsCheck.getDescription())
            }
            else if (modeArg is PyNumericLiteralExpression) {
                if (modeArg.longValue == null) return
                val mode = getPosixPermissions(modeArg.longValue!!.toInt()) ?: return
                if (mode.union(badPermissions).isNotEmpty())
                    holder.registerProblem(node, Checks.ChmodInsecurePermissionsCheck.getDescription())
            } else if (modeArg is PyBinaryExpression){
                // Convert Python OR'd values into a set...
                if (hasBad(modeArg))
                    holder.registerProblem(node, Checks.ChmodInsecurePermissionsCheck.getDescription())
            }
        }
    }
}