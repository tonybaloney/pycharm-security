# Release History

## 1.24.1

* Fix a bug raising a runtime exception (PsiInvalidElementException)

## 1.24.0

* Support for 2021.1 release of PyCharm

## 1.23.1

* Fix corrupt package

## 1.23.0

* PyUp.io december update

## 1.22.2

* Enable 203* series (2020.3)

## 1.22.1

* Update to 2020.2 

## 1.22.0

* Use 202.6397.50 (2020.2) in CI/CD action

## 1.21.0

* Fixed null pointer exception when SDK not properly initialized
* Updated SafetyDB to July 2020 update

## 1.20.0

* Massive speed improvement to the package checker action by checking multiple packages asynchronously
* Added support for PyCharm 2020.2 series
* Sped up security package scanning

## 1.19.0

* Added (DJG300) and (DJG301) for inspection of Django authorization mixins in class-based views.
* Fixed #118 : Fixers don't work for Mako and Jinja2 in edge case (@koxudaxi)

## 1.18.0

* Added [SQL200](doc/checks/SQL200.md) for inspection of insecure SQLalchemy method calls

## 1.17.0 

* Added a source path argument to the GitHub action
* Added a result output to the GitHub action

## 1.16.0

* Use PyCharm 2020 as the primary build target

## 1.15.1

* Fix crash on scanning package security [issues#105(issues/105)

## 1.15.0

* Updated SafetyDB to latest version (April 2020)

## 1.14.0

* Reduced size of Docker image [pull#98](pull/98)
* Bugfix on TRY100 raising false-positives. Fixes [issues#88](issues/88) - [pull#97](pull/97)
* Added [STR100](doc/checks/STR100.md) for insecure format strings

## 1.13.0

* Extended the behaviour of [DJG102](doc/checks/DJG102.md) to not raise a warning when the safe string input is a string literal [pull#87](pull/87)
* Added support for [snyk.io](https://snyk.io) as the vulnerability database. Snyk offers an up to date and in-depth database of Python package issues.

## 1.12.0

* Added [DJG103](doc/checks/DJG103.md) to look for use of the Django `extra()` API on a query set with quoted parameters.
* Extended [DJG102](doc/checks/DJG101.md) to include quoted templated variable names, e.g. `'%(variable)s'`
* Added [DJG104](doc/checks/DJG104.md) to inspect usage of the Django Expression APIs with dangerous templates vulnerable to SQL injection.
* Modified [TRY100](doc/checks/TRY100.md) to only verify `Exception` and no exception specified.

## 1.11.0

* Added configuration option to ignore code inside docstrings (enabled by default)
* Fix matching bug for packages which were installed with mixed-case names

## 1.10.0

* Added [SH100](doc/checks/SH100.md) to check for 17 potential shell injection commands
* Added [SH101](doc/checks/SH101.md) to check for 16 potentially risky spawned process commands

## 1.9.0

* Added a configuration panel to customise how your packages are checked
* Enabled support for PyUp.io subscriptions via an API key

## 1.8.1

* Updated SafetyDB to the "February 2020" release
* Extended [PR100](doc/checks/PR100.md) to include older subprocess APIs, "check_call()" and "check_output()"

## 1.8.0

* Inspection descriptions in the IDE (within the Code Inspection window, Inspection Results and Right-Click on annotation) have full details
* Docker Image and GitHub action does deeper inspection using packages defined within a project
* Added paramiko shell injection inspection [PAR101](doc/checks/PAR101.md)
* Added SSL wrap socket with no version check [SSL100](doc/checks/SSL100.md)
* Added SSL wrap socket with insecure protocol check [SSL101](doc/checks/SSL101.md)

## 1.7.1

* Fix on Pyyaml inspector looking for `'loader'` keyword argument instead of `'Loader'`.
* Fix on plugin XML having wrong standard library short name
* Fix on pickle not matching aliases imports

## 1.7.0

* Added pickle load inspection [PIC100](doc/checks/PIC100.md)
* Added django safe strings inspection [DJG102](doc/checks/DJG102.md)
* Added hardcoded temp path read or write inspection [TMP101](doc/checks/TMP101.md)
* Added XML standard library DoS inspection [XML100](doc/checks/XML100.md)
* Added XML RPC dotted paths inspection [XML200](doc/checks/XML200.md)

## 1.6.0 

* Dockerfile compiles from source, so 'latest' docker image is from master and each tag is correctly set [issue#41](issues/41)
* Annotations descriptions have links to the documentation [issue#43](issues/43)
* GitHub Action now supports "failure on warning"
* GitHub Action now always uses latest image
* GitHub Action supports setting path to custom inspection XML file

## 1.5.0

* Github actions now have annotations

## 1.4.5

* Updated documentation for GitHub actions. Made path optional

## 1.4.4

* Fixed bug in BindAllInterfacesInspection where a call to `bind()` with no arguments would raise an NPE [issue#36](issues/36)

## 1.4.3

* Added github action support (alpha)

## 1.4.2

* Fixed a bug where packages that had a vulnerability in safetydb but no CVE record would raise a NPE to PyCharm [issue#33](issues/33)

## 1.4.1

* Changed [YML100](doc/checks/YML100.md) to not match when `loader=SafeLoader` is used
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

