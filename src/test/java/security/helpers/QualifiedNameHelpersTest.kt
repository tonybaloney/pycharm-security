package security.helpers

import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.types.TypeEvalContext
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QualifiedNameHelpersTest: SecurityTestTask() {
    private lateinit var dummyAnnotation: Annotation

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
        val code = """
            import math
            math.floor(1.9)
        """.trimIndent()
        assertEquals(getQualifiedName(code), "math.floor")
    }

    @Test
    fun `test direct reference no arguments`(){
        val code = """
            import math
            math.floor()
        """.trimIndent()
        assertEquals(getQualifiedName(code), "math.floor")
    }

    @Test
    fun `test double brackets reference no arguments`(){
        val code = """
            import math
            math.floor()()
        """.trimIndent()
        assertEquals(getQualifiedName(code), "math.floor")
    }

    @Test
    fun `test non named call`(){
        val code = """
            import math
            _()
        """.trimIndent()
        assertEquals(getQualifiedName(code), "_")
    }

    @Test
    fun `test resolved callee`(){
        val code = """
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
        val code = """
            class x:
                @staticmethod
                def meth():
                    pass
                    
            y = x
            y.meth()
        """.trimIndent()
        assertEquals(getQualifiedName(code), "meth")
    }

    @Test
    fun `test fully resolved callee`(){
        val code = """
            class x:
                @staticmethod
                def meth():
                    pass
                    
            y = x
            y.meth()
        """.trimIndent()
        assertEquals(getQualifiedName(code), "meth")
    }

    @Test
    fun `test alias`(){
        val code = """
            import x as y
            y.meth()
        """.trimIndent()
        assertEquals(getQualifiedName(code), "y.meth")
    }

    private fun getQualifiedName(code: String): String?{
        var name: String? = null
        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("test.py", PythonFileType.INSTANCE.language, code)
            assertNotNull(testFile)

            val expr: MutableCollection<PyCallExpression> = PsiTreeUtil.findChildrenOfType(testFile, PyCallExpression::class.java)
            assertNotNull(expr)
            expr.forEach { e ->
                name = QualifiedNameHelpers.getQualifiedName(e, TypeEvalContext.codeAnalysis(project, testFile))
            }
        }
        return name
    }
}