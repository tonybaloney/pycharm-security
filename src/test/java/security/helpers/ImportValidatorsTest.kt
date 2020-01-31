package security.helpers

import com.intellij.openapi.application.ApplicationManager
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.psi.PyFile
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.SecurityTestTask
import security.helpers.ImportValidators.hasImportedNamespace

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImportValidatorsTest: SecurityTestTask() {

    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown(){
        super.tearDown()
    }

    @Test
    fun `test empty file`(){
        var code = """
            
        """.trimIndent()
        assertFalse(testHasImport(code, "django"))
    }

    @Test
    fun `test file with no imports`(){
        var code = """
            x = 1
        """.trimIndent()
        assertFalse(testHasImport(code, "django"))
    }

    @Test
    fun `test simple import`(){
        var code = """
            import django
        """.trimIndent()
        assertTrue(testHasImport(code, "django"))
    }

    @Test
    fun `test from import`(){
        var code = """
            from django.db import DbConnection
        """.trimIndent()
        assertTrue(testHasImport(code, "django"))
    }

    private fun testHasImport(code: String, importName: String): Boolean{
        var hasImport: Boolean = false
        ApplicationManager.getApplication().runReadAction {
            val testFile = this.createLightFile("test.py", PythonFileType.INSTANCE.language, code);
            assertNotNull(testFile)
            hasImport = hasImportedNamespace(testFile as PyFile, importName)
        }
        return hasImport
    }
}