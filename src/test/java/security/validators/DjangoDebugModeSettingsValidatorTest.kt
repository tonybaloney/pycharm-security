package security.validators

import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.testFramework.PlatformLiteFixture
import com.jetbrains.python.psi.PyAssignmentStatement
import com.jetbrains.python.psi.PyExpression
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DjangoDebugModeSettingsValidatorTest: PlatformLiteFixture() {
    lateinit var dummyAnnotation: Annotation
    @BeforeEach
    override fun setUp() {
        this.initApplication()
        this.dummyAnnotation = Annotation(0, 0, HighlightSeverity.WARNING, "", "")
        super.setUp()
    }

    @Test
    fun testDjangoExampleRaisesAnnotation() {
        /// Happy Path test. Mock out all the important assertions
        val mockHolder = mock<AnnotationHolder> {
            on { createWarningAnnotation(any<PsiElement>(), any<String>()) } doReturn(dummyAnnotation);
        }
        val mockContainingFile = mock<PsiFile> {
            on { name } doReturn("settings.py")
        }
        val mockLeftHandSideExpression = mock<PyExpression> {
            on { text } doReturn("DEBUG")
        }
        val mockAssignedExpression = mock<PyExpression> {
            on { textMatches("True") } doReturn(true)
        }
        val mockAssignmentStatement = mock<PyAssignmentStatement> {
            on { containingFile } doReturn(mockContainingFile)
            on { leftHandSideExpression } doReturn(mockLeftHandSideExpression)
            on { assignedValue } doReturn(mockAssignedExpression)
        }

        val testValidator = DjangoDebugModeSettingsValidator()
        testValidator.holder = mockHolder
        testValidator.visitPyAssignmentStatement(mockAssignmentStatement)
        verify(mockHolder, times(1))
    }
}