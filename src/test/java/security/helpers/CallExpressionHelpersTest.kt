package security.helpers

import com.jetbrains.python.psi.PyCallExpression
import com.jetbrains.python.psi.PyExpression
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import security.SecurityTestTask

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CallExpressionHelpersTest: SecurityTestTask() {
    @BeforeAll
    override fun setUp() {
        super.setUp()
    }

    @AfterAll
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun `test callee matches no callee`() {
        val node = mock<PyCallExpression> {
            on { callee } doReturn (null)
        }
        assertFalse(calleeMatches(node, "x"))
        verify(node, times(1)).callee
    }

    @Test
    fun `test callee matches null callee name`() {
        val mockCallee = mock<PyExpression> {
            on { name } doReturn(null)
        }
        val node = mock<PyCallExpression> {
            on { callee } doReturn (mockCallee)
        }
        assertFalse(calleeMatches(node, "x"))
        verify(node, times(1)).callee
        verify(mockCallee, times(1)).name
    }

    @Test
    fun `test callee matches callee name positive`() {
        val mockCallee = mock<PyExpression> {
            on { name } doReturn("x")
        }
        val node = mock<PyCallExpression> {
            on { callee } doReturn (mockCallee)
        }
        assertTrue(calleeMatches(node, "x"))
        verify(node, times(1)).callee
        verify(mockCallee, times(1)).name
    }

    @Test
    fun `test callee matches no callee list`() {
        val node = mock<PyCallExpression> {
            on { callee } doReturn (null)
        }
        assertFalse(calleeMatches(node, arrayOf("x")))
        verify(node, times(1)).callee
    }

    @Test
    fun `test callee matches null callee name list`() {
        val mockCallee = mock<PyExpression> {
            on { name } doReturn (null)
        }
        val node = mock<PyCallExpression> {
            on { callee } doReturn (mockCallee)
        }
        assertFalse(calleeMatches(node, arrayOf("x")))
        verify(node, times(1)).callee
        verify(mockCallee, times(1)).name
    }

    @Test
    fun `test callee matches callee name positive list`() {
        val mockCallee = mock<PyExpression> {
            on { name } doReturn("x")
        }
        val node = mock<PyCallExpression> {
            on { callee } doReturn (mockCallee)
        }
        assertTrue(calleeMatches(node, arrayOf("x")))
        verify(node, times(1)).callee
        verify(mockCallee, times(1)).name
    }
}