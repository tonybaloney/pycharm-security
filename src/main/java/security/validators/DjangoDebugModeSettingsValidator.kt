package security.validators

import com.jetbrains.python.psi.PyAssignmentStatement
import com.jetbrains.python.validation.PyAnnotator
import security.Checks

class DjangoDebugModeSettingsValidator: PyAnnotator() {
    override fun visitPyAssignmentStatement(node: PyAssignmentStatement?) {
        if (node?.containingFile?.name != "settings.py")
            return;
        if (node?.leftHandSideExpression?.text  != "DEBUG")
            return;
        if (node?.assignedValue == null)
            return;
        if (node?.assignedValue?.textMatches("True")!!.not())
            return;
        holder.createWarningAnnotation(node, Checks.DjangoDebugModeCheck.toString())
    }
}