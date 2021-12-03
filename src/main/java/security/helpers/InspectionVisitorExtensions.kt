package security.helpers

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.jetbrains.python.inspections.PyInspectionVisitor
import com.jetbrains.python.psi.types.TypeEvalContext
import security.helpers.TypeEvalContextHelper.getTypeEvalContext


open class SecurityVisitor(holder: ProblemsHolder, val session: LocalInspectionToolSession) : PyInspectionVisitor(holder, getTypeEvalContext(session)) {
    override fun getHolder(): ProblemsHolder {
        return super.getHolder()!!
    }

    val typeEvalContext: TypeEvalContext
        get() {
            return getTypeEvalContext(session)
        }
}