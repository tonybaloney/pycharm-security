package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StandardLibraryXmlInspectionTest: SecurityTestTask() {
    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown(){
        super.tearDown()
    }

    @Test
    fun `verify description is not empty`(){
        assertFalse(StandardLibraryXmlInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test invalid import`(){
        var code = """
            from import x
        """.trimIndent()
        testFromImportStatement(code, 0, Checks.StandardLibraryXmlCheck, "test_foo.py", StandardLibraryXmlInspection())
    }

    @Test
    fun `test from import`(){
        var code = """
            from xml.dom.minidom import parse, parseString
        """.trimIndent()
        testFromImportStatement(code, 1, Checks.StandardLibraryXmlCheck, "test_foo.py", StandardLibraryXmlInspection())
    }

    @Test
    fun `test normal import minidom`(){
        var code = """
            import xml.dom.minidom
        """.trimIndent()
        testImportStatement(code, 1, Checks.StandardLibraryXmlCheck, "test_foo.py", StandardLibraryXmlInspection())
    }

    @Test
    fun `test normal safe namespace`(){
        var code = """
            import defusedxml.minidom
        """.trimIndent()
        testImportStatement(code, 0, Checks.StandardLibraryXmlCheck, "test_foo.py", StandardLibraryXmlInspection())
    }

    @Test
    fun `test alias import`(){
        var code = """
            import xml.etree.ElementTree as ET
        """.trimIndent()
        testImportStatement(code, 1, Checks.StandardLibraryXmlCheck, "test_foo.py", StandardLibraryXmlInspection())
    }

    @Test
    fun `test multiple import`(){
        var code = """
            import xml.etree.ElementTree, xml.dom.minidom
        """.trimIndent()
        testImportStatement(code, 2, Checks.StandardLibraryXmlCheck, "test_foo.py", StandardLibraryXmlInspection())
    }

    @Test
    fun `test nested import`(){
        var code = """
            def foo():
                import xml.etree.ElementTree as ET
        """.trimIndent()
        testImportStatement(code, 1, Checks.StandardLibraryXmlCheck, "test_foo.py", StandardLibraryXmlInspection())
    }
}