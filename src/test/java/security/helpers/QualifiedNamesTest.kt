package security.helpers

import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.nameResolver.NameResolverTools
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.impl.PyTypeProvider
import com.jetbrains.python.psi.resolve.PyResolveContext
import com.jetbrains.python.psi.types.TypeEvalContext
import org.jetbrains.annotations.NotNull
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QualifiedNamesTest: SecurityTestTask() {
    lateinit var dummyAnnotation: Annotation

    @BeforeAll
    override fun setUp() {
        super.setUp()
        this.dummyAnnotation = Annotation(0, 0, HighlightSeverity.WARNING, "", "")
    }

    @AfterAll
    override fun tearDown(){
        super.tearDown()
    }

    @Test
    fun `test direct reference`(){
        var code = """
            import math
            math.floor(1.9)
        """.trimIndent()
        assertEquals(getQualifiedName(code), "math.floor")
    }

    @Test
    fun `test direct reference no arguments`(){
        var code = """
            import math
            math.floor()
        """.trimIndent()
        assertEquals(getQualifiedName(code), "math.floor")
    }

    @Test
    fun `test double brackets reference no arguments`(){
        var code = """
            import math
            math.floor()()
        """.trimIndent()
        assertEquals(getQualifiedName(code), "math.floor")
    }

    @Test
    fun `test non named call`(){
        var code = """
            import math
            _()
        """.trimIndent()
        assertEquals(getQualifiedName(code), "_")
    }

    @Test
    fun `test resolved callee`(){
        var code = """
            class x:
                @staticmethod
                def meth():
                    pass
                    
            x.meth()
        """.trimIndent()
        assertEquals(getQualifiedName(code), "meth")
    }

    @Test
    fun `test fully resolved callee with context`(){
        var code = """
            class x:
                @staticmethod
                def meth():
                    pass
                    
            y = x
            y.meth()
        """.trimIndent()
        assertEquals(getQualifiedName(code, false), "meth")
    }

    @Test
    fun `test fully resolved callee`(){
        var code = """
            class x:
                @staticmethod
                def meth():
                    pass
                    
            y = x
            y.meth()
        """.trimIndent()
        assertEquals(getQualifiedName(code, true), "meth")
    }

    @Test
    fun `test alias`(){
        var code = """
            import x as y
            y.meth()
        """.trimIndent()
        assertEquals(getQualifiedName(code, true), "y.meth")
    }

    private fun getQualifiedName(code: String, resolveContext: Boolean = false): String?{
        var name: String? = null
        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("test.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)

            if (resolveContext){
                val typeEvalContext = TypeEvalContext.codeCompletion(this.project, testFile)
                QualifiedNames.resolveContext = PyResolveContext.defaultContext().withTypeEvalContext(typeEvalContext)
            }

            val expr: @NotNull MutableCollection<PyCallExpression> = PsiTreeUtil.findChildrenOfType(testFile, PyCallExpression::class.java)
            assertNotNull(expr)
            expr.forEach { e ->
                name = QualifiedNames.getQualifiedName(e)
            }
        }
        return name
    }
}