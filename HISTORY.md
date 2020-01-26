# Release History

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

