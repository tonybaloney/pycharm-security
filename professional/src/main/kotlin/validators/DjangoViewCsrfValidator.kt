package validators

import com.intellij.codeInspection.InspectionManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.django.lang.template.inspection.DjangoInspectionVisitorAdapter
import com.jetbrains.django.lang.template.inspection.DjangoLocalInspectionTool


class DjangoViewCsrfValidator : DjangoLocalInspectionTool() {
    override fun createInspectionVisitor(inspectionManager: InspectionManager?): PsiElementVisitor {
        return DjangoViewCsrfVisitor(inspectionManager!!)
    }

    private class DjangoViewCsrfVisitor internal constructor(manager: InspectionManager) : DjangoInspectionVisitorAdapter(manager) {
        override fun visitElement(element: PsiElement?) {
            val a = 0
            super.visitElement(element)
        }
    }
}