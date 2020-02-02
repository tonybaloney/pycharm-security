# Release History

## 1.4.1

* Altered [PW100](doc/checks/PW100.md) to only match on == and != operators, reducing false positives

## 1.4.0

* Added [DJG101](doc/checks/DJG101.md) Using quoted, parametrized literal will bypass Django SQL Injection protection

## 1.3.0

* Added [TRY100](doc/checks/TRY100.md) check for try..except..pass statements
* Added [TRY101](doc/checks/TRY101.md) check for try..except..continue statements
* Added [AST100](doc/checks/AST100.md) check for assert usage outside of a test
* Added [NET100](doc/checks/NET100.md) check unspecified binding 
* Added [PAR100](doc/checks/PAR100.md) check for host key bypass in paramiko ssh client usage
* Added [OS100](doc/checks/OS100.md) check calls to `os.chmod()` for dangerous POSIX permissions

## 1.2.0

* Added SQL injection with Python formatting check [SQL100](doc/checks/SQL100.md)
* Support for PyCharm 2020.1

## 1.1.0

* Added new hardcoded password check [PW100](doc/checks/PW100.md)
* Added new builtin exec check [EX100](doc/checks/EX100.md)
* Added new mako unescaped input check [MK100](doc/checks/MK100.md)
* Added new mako HTML escape quick fix
* Fixed minor bug in Flask debug mode check

## 1.0.15

* All fixes can now be run in batch mode
* Added [Jinja2 unescaped Template Validator](doc/checks/JJ100.md)
* Added [Jinja2 unconditional escape fixer](doc/fixes/jinja2unconditional.md)

## 1.0.14

* All checks are now local inspections, so within the Code Inspection tool, they will show as "Python Security"
* Users can now alter the severity of any particular check and mute for a given project, file or IDE

## 1.0.13

* Added [Django CSRF Middleware Validator](doc/checks/DJG200.md)
* Added [Django Clickjack Middleware Validator](doc/checks/DJG201.md)
* Added Django Middleware Fixer
* Fixed bug where function references would be unsafely cast to a PyReferenceExpression and cause a fault

## 1.0.12

* Added [Shell Escape Fixer](doc/fixes/shellescapefixer.md), recommended by [PR100](doc/checks/PR100.md)
* Modified the shell injection validator to match subprocess.call, .run and .Popen
* Modified the shell injection validator to ignore string literals or lists of literals

## 1.0.11

* Annotations "Read Documentation" fix will go to the new documentation site instead of GitHub.

## 1.0.10

* PW100 uses `secrets.compare_digest` if the Python version is 3.7+
* Fixed bug in test suite (doesn't affect plugin)

## 1.0.9

* Added a documentation action to all recommendations
* Added a timing attack fixer for using hmac.compare_digest
* Added a timing attack test for comparing a password string
* Added hashlib test for cryptographically weak algorithm usage
* Added hashlib check for algorithms vulnerable to length-attacks

## 1.0.8 

* Notification summarising package scan, even when no issues are found
* Issues warning notification when no Python SDK is configured
* Various minor bug fixes

## 1.0.7

* Fixed a bug when instantiating the vulnerability database at startup. Raised by @m-aciek [#3](https://github.com/tonybaloney/pycharm-security/issues/3)

## 1.0.6 

* Fixed error when checking incomplete statements. Raised by @jugmac00 [#1](https://github.com/tonybaloney/pycharm-security/issues/1)

## 1.0.5

* Package checker works with specific (PEP440) version ranges.

## 1.0.4

* Checks installed packages against safetydb and alerts for any known vulnerabilities

## 1.0.3

* Added django debug mode check
* Added `tempfile.mktemp` check with fixer to replace `tempfile.mkstemp` with existing arguments
* Added subprocess.call(shell=true) check
* Added httpx no-verify check
* Added requests no-verify check

## 1.0.2

* Added flask debug mode check
* Added pyyaml load check

