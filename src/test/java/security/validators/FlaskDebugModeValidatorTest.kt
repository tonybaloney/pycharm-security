package security.validators

import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.impl.PsiManagerImpl
import com.intellij.testFramework.PlatformLiteFixture
import com.jetbrains.python.PythonFileType
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import security.Checks

class FlaskDebugModeValidatorTest: PlatformLiteFixture() {
    private lateinit var fileFactory: PsiFileFactoryImpl
    lateinit var dummyAnnotation: Annotation

    @BeforeEach
    override fun setUp() {
        this.initApplication()
        super.setUp()
        this.dummyAnnotation = Annotation(0, 0, HighlightSeverity.WARNING, "", "")
        val manager = PsiManagerImpl(this.myProject)
        this.fileFactory = PsiFileFactoryImpl(manager)
    }

    @Test
    fun testFlaskDebugRaisesAnnotation() {
        val testPy = """
            import flask
            app = flask.Flask(__name__)
            app.run(debug=True)
        """.trimIndent()
        val testFile = this.fileFactory.createFileFromText("test.py", PythonFileType.INSTANCE, testPy)
        val mockHolder = mock<AnnotationHolder> {
            on { createWarningAnnotation(any<PsiElement>(), eq(Checks.FlaskDebugModeCheck.toString())) } doReturn(dummyAnnotation);
        }

        val testValidator = FlaskDebugModeValidator()
        testValidator.holder = mockHolder
        testValidator.visitFile(testFile)
        verify(mockHolder, times(1)).createWarningAnnotation(any<PsiElement>(), eq(Checks.FlaskDebugModeCheck.toString()))
    }
}