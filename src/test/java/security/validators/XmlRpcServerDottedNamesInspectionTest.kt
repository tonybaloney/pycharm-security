package security.validators

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.Checks
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class XmlRpcServerDottedNamesInspectionTest: SecurityTestTask() {
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
        assertFalse(XmlRpcServerDottedNamesInspection().staticDescription.isNullOrEmpty())
    }

    @Test
    fun `test xml rpc server instance with dotted names enabled`(){
        val code = """
            import xmlrpc.server

            with xmlrpc.server.SimpleXMLRPCServer(('localhost', 8000),) as server:
                server.register_instance(i, allow_dotted_names=True)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.XmlRpcServerDottedNamesCheck, "test.py", XmlRpcServerDottedNamesInspection())
    }

    @Test
    fun `test xml rpc server instance with dotted names disabled`(){
        val code = """
            import xmlrpc.server

            with xmlrpc.server.SimpleXMLRPCServer(('localhost', 8000),) as server:
                server.register_instance(i, allow_dotted_names=False)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.XmlRpcServerDottedNamesCheck, "test.py", XmlRpcServerDottedNamesInspection())
    }

    @Test
    fun `test xml rpc server instance with dotted names not specified`(){
        val code = """
            import xmlrpc.server

            with xmlrpc.server.SimpleXMLRPCServer(('localhost', 8000),) as server:
                server.register_instance(i)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.XmlRpcServerDottedNamesCheck, "test.py", XmlRpcServerDottedNamesInspection())
    }

    @Test
    fun `test xml rpc server instance with dotted names positional enabled`(){
        val code = """
            import xmlrpc.server

            with xmlrpc.server.SimpleXMLRPCServer(('localhost', 8000),) as server:
                server.register_instance(i, True)
        """.trimIndent()
        testCodeCallExpression(code, 1, Checks.XmlRpcServerDottedNamesCheck, "test.py", XmlRpcServerDottedNamesInspection())
    }

    @Test
    fun `test xml rpc server instance with dotted names positional disabled`(){
        val code = """
            import xmlrpc.server

            with xmlrpc.server.SimpleXMLRPCServer(('localhost', 8000),) as server:
                server.register_instance(i, False)
        """.trimIndent()
        testCodeCallExpression(code, 0, Checks.XmlRpcServerDottedNamesCheck, "test.py", XmlRpcServerDottedNamesInspection())
    }
}