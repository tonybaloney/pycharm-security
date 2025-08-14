# GitHub Copilot Instructions for PyCharm Security Plugin

## Project Overview

This repository contains the **PyCharm Python Security Plugin**, a comprehensive security analysis tool for Python code that helps developers identify and fix common security vulnerabilities. The plugin integrates with PyCharm IDE and is also available as a GitHub Action and Docker container.

## Architecture & Technology Stack

- **Language**: Java 17 + Kotlin for the PyCharm plugin development
- **Build System**: Gradle with IntelliJ plugin
- **Framework**: IntelliJ Platform SDK for PyCharm plugin development
- **Testing**: JUnit 5 with Kotlin test framework
- **Documentation**: Sphinx with Markdown support
- **CI/CD**: GitHub Actions with Docker deployment

## Project Structure

```
src/main/java/security/
├── Checks.kt                    # Central registry of all security check types
├── validators/                  # Individual security inspection implementations
├── fixes/                      # Quick fix implementations for detected issues
├── helpers/                    # Utility classes and helper functions
├── packaging/                  # Package vulnerability checking logic
└── settings/                   # Plugin configuration and settings

src/test/java/security/
├── validators/                 # Test cases for security inspections
└── SecurityTestTask.kt         # Base test class for all security tests

doc/
├── checks/                     # Documentation for each security check
├── fixes/                      # Documentation for quick fixes
└── *.md                       # User guides and development docs
```

## Core Development Patterns

### Creating New Security Validators

1. **Define Check Type**: Add a new `CheckType` in `Checks.kt`:
   ```kotlin
   val MyNewCheck = CheckType("ABC100", "Brief description of the security issue")
   ```

2. **Create Validator Class**: In `src/main/java/security/validators/`:
   ```kotlin
   class MyNewValidatorInspection : PyInspection() {
       val check = Checks.MyNewCheck
       
       override fun getStaticDescription(): String? {
           return check.getStaticDescription()
       }
       
       override fun buildVisitor(holder: ProblemsHolder,
                                 isOnTheFly: Boolean,
                                 session: LocalInspectionToolSession): PsiElementVisitor = Visitor(holder, session)
       
       private class Visitor(holder: ProblemsHolder, session: LocalInspectionToolSession) : SecurityVisitor(holder, session) {
           override fun visitPyCallExpression(node: PyCallExpression) {
               if (skipDocstring(node)) return
               // Guard clauses to filter relevant calls
               if (!calleeMatches(node, arrayOf("target_function"))) return
               if (!qualifiedNameStartsWith(node, "target.module.", typeEvalContext)) return
               if (!hasVulnerablePattern(node)) return
               
               // Report the issue
               holder.registerProblem(node, Checks.MyNewCheck.getDescription())
           }
       }
   }
   ```

3. **Register in plugin.xml**: Add the inspection to `src/main/resources/META-INF/plugin.xml`:
   ```xml
   <localInspection 
       language="Python" 
       enabledByDefault="true" 
       groupName="Python Security" 
       hasStaticDescription="true" 
       displayName="ABC100: Brief description of the security issue." 
       shortName="MyNewValidatorInspection" 
       implementationClass="security.validators.MyNewValidatorInspection" />
   ```

4. **Add Tests**: Create test class in `src/test/java/security/validators/`:
   ```kotlin
   class MyNewValidatorInspectionTest : SecurityTestTask() {
       @Test
       fun `test vulnerable pattern detected`() {
           val code = """
               vulnerable_function('unsafe_input')
           """.trimIndent()
           testCodeCallExpression(code, 1, Checks.MyNewCheck, "test.py", MyNewValidatorInspection())
       }
   }
   ```

5. **Add Documentation**: Create `doc/checks/ABC100.md` with this structure:
   ```markdown
   # ABC100
   
   Brief description of the security issue and why it's dangerous.
   
   ## Example
   
   ```python
   # Vulnerable code example
   vulnerable_function('unsafe_input')
   ```
   
   ## Fixes
   
   Explanation of how to fix the issue and secure alternatives.
   ```

### Security Check Categories

- **YMLxxx**: YAML processing security issues
- **FLKxxx**: Flask framework security issues  
- **RQxxx**: HTTP request library security issues (requests, httpx)
- **PRxxx**: Process/subprocess security issues
- **TMPxxx**: Temporary file security issues
- **DJGxxx**: Django framework security issues  
- **HLxxx**: Hashing/cryptography security issues
- **PWxxx**: Password/credential security issues
- **JJxxx**: Jinja2 template security issues
- **EXxxx**: Code execution vulnerabilities
- **MKxxx**: Mako template security issues
- **SQLxxx**: SQL injection vulnerabilities
- **ASTxxx**: Code structure security issues
- **TRYxxx**: Exception handling security issues
- **PARxxx**: Paramiko SSH library security issues
- **NETxxx**: Network binding security issues
- **OSxxx**: Operating system command security issues
- **PICxxx**: Pickle serialization security issues
- **XMLxxx**: XML processing security issues
- **SSLxxx**: SSL/TLS security issues
- **STRxxx**: String formatting security issues
- **SHxxx**: Shell command security issues

### Helper Functions

The `security.helpers` package provides useful utilities for validators:
- `calleeMatches(node, arrayOf("function_name"))`: Check if call matches function names
- `qualifiedNameStartsWith(node, "module.prefix.", typeEvalContext)`: Check module qualification  
- `skipDocstring(node)`: Skip nodes that are in docstrings
- `SecurityVisitor`: Base visitor class that extends `PyInspectionVisitor`

### Testing Conventions

- Extend `SecurityTestTask` for all security validator tests
- Use `testCodeCallExpression()` for testing call expression validators
- Use `testCodeAssignmentStatement()` for testing assignment validators  
- Test both positive (vulnerable) and negative (safe) cases
- Include edge cases and boundary conditions
- Use descriptive test method names with backticks for readability
- Verify exact number of expected violations with `times` parameter

### Quick Fixes

Quick fixes should be implemented in `src/main/java/security/fixes/`:
- Extend `LocalQuickFix`, `IntentionAction`, and optionally `HighPriorityAction`
- Provide safe alternatives to vulnerable code patterns
- Use `PyElementGenerator` to create replacement code elements
- Include comprehensive tests for fix transformations
- Associate fixes with validators by returning them in `registerProblem()` calls

Example quick fix structure:
```kotlin
class MySafeReplacementFixer : LocalQuickFix, IntentionAction {
    override fun getFamilyName(): String = "Use safe alternative"
    
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement as PyCallExpression
        val generator = PyElementGenerator.getInstance(project)
        val replacement = generator.createCallExpression(LanguageLevel.getDefault(), "safe_function")
        element.replace(replacement)
    }
    
    // Additional IntentionAction methods...
}
```

## Code Style Guidelines

- Use Kotlin for new code where possible
- Follow IntelliJ Platform development conventions
- Use guard clauses for early returns in validators
- Document complex security detection logic
- Prefer composition over inheritance
- Use meaningful variable and method names that reflect security context

## Documentation Standards

- Each security check must have detailed documentation in `doc/checks/`
- Include code examples showing vulnerable and secure patterns
- Explain the security implications and potential impact
- Link to relevant security standards (OWASP, CWE, etc.)
- Update the main documentation index when adding new checks

## Testing Requirements

- All security validators must have comprehensive test coverage
- Test both detection (vulnerable code) and non-detection (safe code)
- Include tests for edge cases and false positives
- Verify quick fixes work correctly and don't introduce new issues
- Use realistic code examples in tests

## GitHub Action Integration

The plugin includes a GitHub Action component:
- Dockerfile and action.yml define the CI/CD integration
- Support for custom inspection profiles
- Configurable failure conditions and reporting
- Integration with GitHub's security advisory features

## Plugin Configuration

The plugin supports various vulnerability databases:
- Bundled SafetyDB for offline scanning
- PyUp.io API integration (subscription required)
- Snyk API integration (subscription required)  
- PyPI vulnerability database

## Development Workflow

1. **Setup**: Import project in IntelliJ IDEA with Kotlin plugin
2. **Build**: Use `./gradlew build` to compile and test
3. **Debug**: Use `./gradlew runIde` to test plugin in PyCharm instance
4. **Test**: Use `./gradlew test` to run test suite
5. **Verify**: Use `./gradlew verifyPlugin` before publishing

## Common Development Tasks

- **Adding new security check**: Follow the validator creation pattern above
- **Updating vulnerability database**: Modify files in `src/main/resources/safety-db/`
- **Improving detection accuracy**: Enhance guard clauses and pattern matching in validators
- **Adding framework support**: Create new validator categories for frameworks
- **Adding quick fixes**: Implement LocalQuickFix classes in `security.fixes` package
- **Documentation updates**: Maintain docs in sync with code changes
- **Testing new validators**: Use SecurityTestTask with mock ProblemsHolder
- **Debugging plugin**: Use `./gradlew runIde` to launch PyCharm with plugin loaded

## Debugging Tips

- Use `println()` statements in validators during development (remove before commit)
- Check `typeEvalContext` for type information when needed
- Test with various Python code patterns to avoid false positives
- Use PyCharm's "Internal Actions" → "View PSI Structure" to understand code structure
- Validate plugin.xml syntax with IntelliJ before testing

## Security Considerations

When developing security validators:
- Minimize false positives while maintaining comprehensive detection
- Consider performance impact of complex pattern matching
- Validate against real-world vulnerable code examples
- Test with various Python versions and coding styles
- Consider context-sensitive analysis for better accuracy

## Contribution Guidelines

- Follow existing code patterns and naming conventions
- Include comprehensive tests for all changes
- Update documentation for new features or changes
- Ensure backward compatibility with existing PyCharm versions
- Test integration with GitHub Action workflow