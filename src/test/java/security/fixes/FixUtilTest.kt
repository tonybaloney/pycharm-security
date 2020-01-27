package security.fixes

import com.intellij.openapi.application.ApplicationManager
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.psi.PyFile
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FixUtilTest : SecurityTestTask() {
    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    private fun getNewFileForImport(code: String, import_name: String, import_alias: String): PyFile? {
        var testFile: PyFile? = null
        ApplicationManager.getApplication().runReadAction {
            testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code) as PyFile;
            assertNotNull(testFile)
            import(testFile as PyFile, project, import_name, import_alias)

        }
        return testFile
    }

    @Test
    fun `test new import with existing imports and no alias`() {
        var code = """
            import tempfile
            
            a = 1
        """.trimIndent()
        val testFile = getNewFileForImport(code, "test_import", "")
        ApplicationManager.getApplication().runReadAction {
            assertTrue(testFile!!.text.contains("import test_import"))
        }
    }

    @Test
    fun `test new import with existing imports and an alias`() {
        var code = """
            import tempfile
            
            a = 1
        """.trimIndent()
        val testFile = getNewFileForImport(code, "test_import", "alias_name")
        ApplicationManager.getApplication().runReadAction {
            assertTrue(testFile!!.text.contains("import test_import as alias_name"))
        }
    }

    @Test
    fun `test new import with no existing imports and no alias`() {
        var code = """ 
            a = 1
        """.trimIndent()
        val testFile = getNewFileForImport(code, "test_import", "")
        ApplicationManager.getApplication().runReadAction {
            assertTrue(testFile!!.text.contains("import test_import"))
        }
    }

    @Test
    fun `test new import with no existing imports and an alias`() {
        var code = """ 
            a = 1
        """.trimIndent()
        val testFile = getNewFileForImport(code, "test_import", "alias_name")
        ApplicationManager.getApplication().runReadAction {
            assertTrue(testFile!!.text.contains("import test_import as alias_name"))
        }
    }
}