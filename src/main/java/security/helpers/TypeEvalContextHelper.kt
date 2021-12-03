package security.helpers

import com.intellij.codeInspection.LocalInspectionToolSession
import com.jetbrains.python.psi.types.TypeEvalContext

object TypeEvalContextHelper {
    fun getTypeEvalContext(session: LocalInspectionToolSession): TypeEvalContext {
        return TypeEvalContext.codeAnalysis(session.file.project, session.file)
    }
}