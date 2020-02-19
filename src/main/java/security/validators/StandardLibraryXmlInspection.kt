package security.validators

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.python.inspections.PyInspection
import com.jetbrains.python.psi.PyFromImportStatement
import com.jetbrains.python.psi.PyImportStatement
import security.Checks
import security.helpers.SecurityVisitor
import security.helpers.skipDocstring

class StandardLibraryXmlInspection : PyInspection() {
    val check = Checks.StandardLibraryXmlCheck

    override fun getStaticDescription(): String? {
        return check.getStaticDescription()
    }

    override fun buildVisitor(holder: ProblemsHolder,
                              isOnTheFly: Boolean,
                              session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)

    private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
        val vulnerableNamespaces = arrayOf("xml.sax", "xml.etree", "xml.dom.minidom", "xml.dom.pulldom", "xmlrpc.server")

        private fun match(qname: String): Boolean {
            return (listOf(*vulnerableNamespaces).any{ qname.startsWith(it) })
        }

        override fun visitPyFromImportStatement(node: PyFromImportStatement) {
            if (skipDocstring(node)) return

            if (node.importSourceQName == null) return
            if (node.importSourceQName!!.toString().isEmpty()) return
            if (match(node.importSourceQName!!.toString()))
                holder.registerProblem(node, Checks.StandardLibraryXmlCheck.getDescription())
        }

        override fun visitPyImportStatement(node: PyImportStatement) {
            if (skipDocstring(node)) return

            if (node.fullyQualifiedObjectNames.isEmpty()) return
            if (node.fullyQualifiedObjectNames.any { match(it) })
                holder.registerProblem(node, Checks.StandardLibraryXmlCheck.getDescription())
        }
    }
}