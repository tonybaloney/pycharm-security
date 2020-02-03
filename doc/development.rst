.. _development:

Development
===========

This project is designed for IntelliJ IDEA and requires gradle.

Important gradle targets are:

* `:test` - Run the test suite
* `:runIde` - Start PyCharm with the plugin in debug mode
* `:jacocoTestReport` - Run test coverage
* `:verifyPlugin` - Run plugin verification before publishing

Creating a Validator
--------------------

Creating a check type
+++++++++++++++++++++

1. Create a CheckType singleton in `Checks` with the code and description
2. Create a markdown page with the code inside `docs/checks`

Example:

.. code-block:: kotlin

    val MyCheck = CheckType("XX1000", "What you're doing is bad for this reason.")

Creating a validator class
++++++++++++++++++++++++++

First, determine the element type you're looking for.

For example, if you're looking for a call expression (function call or method call)

1. Create a new class inside `security.validators`
2. Copy a similar validator
3. Override the `visitPyxxx` function

All validators are a series of [guard clauses](https://refactoring.com/catalog/replaceNestedConditionalWithGuardClauses.html) then finally a call to `holder.create(node, check)` once all the criteria have been met:

Linking the validator to the plugin
+++++++++++++++++++++++++++++++++++

Inside `src/main/java/resources/META-INF/plugin.xml` add a new `localInspection` tag inside the `extensions` with the name of your class.

.. code-block:: xml

    <extensions defaultExtensionNs="com.intellij">
     ...
     <localInspection language="Python" enabledByDefault="true" groupName="Python Security" hasStaticDescription="true" displayName="OS100: Call to os.chmod setting permission values" shortName="OsChmodInspection" implementationClass="security.validators.OsChmodInspection" />

    </extensions>

Next, start the development IDE using the `:runIde` target and debug in Gradle.

In the editor, try writing code and seeing if it triggers your code using breakpoints.

Testing the validator
+++++++++++++++++++++

The annotator is mocked and the number of calls is verified to see if the warning window was raised.

The basic boiler plate for a test is:

.. code-block:: kotlin

    package security.validators

    import org.junit.jupiter.api.AfterAll
    import org.junit.jupiter.api.BeforeAll
    import org.junit.jupiter.api.Test
    import org.junit.jupiter.api.TestInstance
    import security.Checks
    import security.SecurityTestTask

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class MyNewInspectionTest: SecurityTestTask() {
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
            assertFalse(MyNewInspection().staticDescription.isNullOrEmpty())
        }
    }

Then, think of positive and negative scenarios to check for and create a test for each.
All validators can be tested with a code string and a call to one of the inline functions in `SecurityTestTask`.

For example, to test your `visitPyCallExpression` override, use `testCodeCallExpression` with the code, the expected number of triggers (e.g. 1), the expected Check, the test module name, and the inspection type:

.. code-block:: kotlin

        @Test
        fun `test yaml load`(){
            var code = """
                import yaml
                yaml.load()
            """.trimIndent()
            testCodeCallExpression(code, 1, Checks.MyNewCheck, "test.py", MyNewInspection())
        }

Run the test code and also run it with coverage to see whether you're catching all guard clauses.

    Note that inside unit tests, the qualified names are never resolved to their packages because the test framework does not have the Python standard library loaded.

Creating a fixer
----------------

Fixers are used to replace elements inside the document tree.

Create a new kotlin class inside the `security.fixes` package.

Use the following boiler-plate as an example fixer:

.. code-block:: kotlin

    package security.fixes

    import com.intellij.codeInsight.intention.HighPriorityAction
    import com.intellij.codeInsight.intention.IntentionAction
    import com.intellij.codeInspection.LocalQuickFix
    import com.intellij.codeInspection.ProblemDescriptor
    import com.intellij.openapi.application.ApplicationManager
    import com.intellij.openapi.editor.Editor
    import com.intellij.openapi.project.Project
    import com.intellij.psi.PsiFile
    import com.intellij.util.IncorrectOperationException
    import com.jetbrains.python.psi.*

    class MyNewFixer : LocalQuickFix, IntentionAction, HighPriorityAction {
        override fun getText(): String {
            return name
        }

        override fun getFamilyName(): String {
            return "Text to show in UI"
        }

        override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
            // Add any custom inspections to check if this fixer applies
            return true
        }

        @Throws(IncorrectOperationException::class)
        override fun invoke(project: Project, editor: Editor, file: PsiFile) {
            ...
        }

        override fun startInWriteAction(): Boolean {
            return true
        }

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            return
        }
    }

For the `invoke` function implementation, keep the logic minimal so the fixer can easily be tested.

1. Get old element using one of the functions in the `FixUtil` helper package.
2. Build a new element using a custom function
3. Start a write action on the application and replace the old element with the new element

Ensure you are using the Elvis-Operator on both the old and new element in-case either is null.

.. code-block:: kotlin

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val oldElement = FixerUtil.getCallElementAtCaret(file, editor) ?: return
        val newElement = getNewExpressionAtCaret(file, editor, project) ?: return
        ApplicationManager.getApplication().runWriteAction { oldElement.replace(newElement) }
    }

For a simple function rename, you can use the `FixUtil.getNewCallExpressiontAtCaret` with the old function name and the new name as the 4th and 5th arguments.

.. code-block:: kotlin

    fun getNewExpressionAtCaret(file: PsiFile, editor: Editor, project: Project): PyCallExpression? {
        return getNewCallExpressiontAtCaret(file, editor, project, "mktemp", "mkstemp")
    }

For a more complex example, see the `UseCompareDigestFixer`, which replaces a binary expression with a call expression.

Testing a fixer
+++++++++++++++

To test a fixer, you must inherit your test from the `SecurityTestTask` type and run `setUp()` and `tearDown()` for each class lifecycle. This will set up the application and load all the components into the IOC container.

The purpose of the first test is to look at the hard-coded properties.

The second check can be written multiple times for different code snippets.

It will:

1. Create a PyFile instance from the code string
2. Mock the caret to the fixed position (you have to count the number of characters in the code string, 16 is the 16th character)
3. Mock the editor to pretend the caret is in a fixed position
4. Run the fixer
5. Verify the caret inspection was called once

For step 4, the goal is to have the same logic as in .invoke

.. code-block:: kotlin

    val oldElement = FixerUtil.getCallElementAtCaret(file, editor) ?: return
    val newElement = getNewExpressionAtCaret(file, editor, project) ?: return

So the assertions following should inspect oldElement to make sure it has matched your code snippet.
Then inspect newElement to check it has replaced it correctly.

Full example:

.. code-block:: kotlin

    package security.fixes

    import com.intellij.lang.annotation.Annotation
    import com.intellij.lang.annotation.HighlightSeverity
    import com.intellij.openapi.application.ApplicationManager
    import com.intellij.openapi.editor.CaretModel
    import com.intellij.openapi.editor.Editor
    import com.jetbrains.python.PythonFileType
    import com.nhaarman.mockitokotlin2.doReturn
    import com.nhaarman.mockitokotlin2.mock
    import com.nhaarman.mockitokotlin2.verify
    import org.junit.jupiter.api.*
    import org.mockito.Mockito
    import security.SecurityTestTask

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class MyNewFixerTest: SecurityTestTask() {
        @BeforeAll
        override fun setUp() {
            super.setUp()
        }

        @AfterAll
        override fun tearDown(){
            super.tearDown()
        }

        @Test
        fun `verify fixer properties`(){
            val fixer = MyNewFixer()
            assertTrue(fixer.startInWriteAction())
            assertTrue(fixer.familyName.isNotBlank())
            assertTrue(fixer.name.isNotBlank())
        }

        @Test
        fun `test get call element at caret`(){
            var code = """
                import tempfile
                tempfile.mktemp()
            """.trimIndent()

            val mockCaretModel = mock<CaretModel> {
                on { offset } doReturn 16
            }
            val mockEditor = mock<Editor> {
                on { caretModel } doReturn mockCaretModel
            }

            ApplicationManager.getApplication().runReadAction {
                val testFile = this.createLightFile("app.py", PythonFileType.INSTANCE.language, code);
                assertNotNull(testFile)
                val fixer = MyNewFixer()
                assertTrue(fixer.isAvailable(project, mockEditor, testFile))
                // Repeat the steps in .invoke()
                // Assert parts of oldElement and newElement
            }

            verify(mockEditor, Mockito.times(1)).caretModel
            verify(mockCaretModel, Mockito.times(1)).offset
        }
    }

Linking a fixer to a validator
++++++++++++++++++++++++++++++

Within the validator code, once you have called `createWarningAnnotation`, use the return annotation instance and call `registerFix` against it:

.. code-block:: kotlin

    val annotation = holder.createWarningAnnotation(node, Checks.MyCheck.toString())
    annotation.registerFix((MyNewFixer() as IntentionAction), node.textRange)

You can add one or multiple to this.