package security.helpers

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.jetbrains.python.inspections.PyInspectionVisitor


open class SecurityVisitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : PyInspectionVisitor(holder, session) {
    override fun getHolder(): ProblemsHolder {
        return super.getHolder()!!
    }
}