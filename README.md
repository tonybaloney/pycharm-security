# PyCharm Python Security plugin

<a href="https://github.com/tonybaloney/pycharm-security"><img alt="GitHub Actions status" src="https://github.com/tonybaloney/pycharm-security/workflows/CI/badge.svg"></a>
[![Downloads](https://img.shields.io/jetbrains/plugin/v/13609-python-security.svg)](https://plugins.jetbrains.com/plugin/13609-python-security)
[![Version](https://img.shields.io/jetbrains/plugin/d/13609-python-security.svg)](https://plugins.jetbrains.com/plugin/13609-python-security)
[![codecov](https://codecov.io/gh/tonybaloney/pycharm-security/branch/master/graph/badge.svg)](https://codecov.io/gh/tonybaloney/pycharm-security)

![](src/main/resources/META-INF/pluginIcon.svg)

A plugin to run security checks for common flaws in Python code and suggest quick fixes.

Available on the [Jetbrains plugin marketplace](https://plugins.jetbrains.com/plugin/13609-python-security)

![](src/main/resources/META-INF/screenshot.png)

## SafetyDB

This plugin will check the installed packages in your Python projects against the SafetyDB and raise a warning for any vulnerabilities.

![](src/main/resources/META-INF/safetydb-screenshot.png)

## Current checks

* [HL100](doc/checks/HL100.md) md4, md5, sha, and sha1 hashing algorithms should not be used for obfuscating or protecting data
* [HL101](doc/checks/HL101.md) MD5, SHA-1, RIPEMD-160, Whirlpool and the SHA-256 / SHA-512 hash algorithms all vulnerable to length-extension attacks and should not be used for obfuscating or protecting data
* [YML100](doc/checks/YML100.md) Use of `yaml.load()` can cause arbitrary code execution. Suggests and has a "Quick Fix" to replace with `safe_load()` using existing arguments
* [FLK100](doc/checks/FLK100.md) Use of `debug=True` when instantiating flask applications
* [RQ100](doc/checks/RQ100.md) Use of `verify=False` when making HTTP requests using the `requests` package
* [RQ101](doc/checks/RQ101.md) Use of `verify=False` when making HTTP requests using the `httpx` package
* [PR100](doc/checks/PR100.md) Use of `shell=True` when running `subprocess.call` from the standard library
* [TMP100](doc/checks/TMP100.md) Use of `tempfile.mktemp`
* [DJG100](doc/checks/DJG100.md) Setting `DEBUG = True` in a `settings.py` file (assumed Django project settings)

## Release History

### 1.0.9

* Added a documentation action to all recommendations
* Added a timing attack fixer for using hmac.compare_digest
* Added a timing attack test for comparing a password string
* Added hashlib test for cryptographically weak algorithm usage
* Added hashlib check for algorithms vulnerable to length-attacks

### 1.0.8 

* Notification summarising package scan, even when no issues are found
* Issues warning notification when no Python SDK is configured
* Various minor bug fixes

### 1.0.7

* Fixed a bug when instantiating the vulnerability database at startup. Raised by @m-aciek [#3](https://github.com/tonybaloney/pycharm-security/issues/3)

### 1.0.6 

* Fixed error when checking incomplete statements. Raised by @jugmac00 [#1](https://github.com/tonybaloney/pycharm-security/issues/1)

### 1.0.5

* Package checker works with specific (PEP440) version ranges.

### 1.0.4

* Checks installed packages against safetydb and alerts for any known vulnerabilities

### 1.0.3

* Added django debug mode check
* Added `tempfile.mktemp` check with fixer to replace `tempfile.mkstemp` with existing arguments
* Added subprocess.call(shell=true) check
* Added httpx no-verify check
* Added requests no-verify check

### 1.0.2

* Added flask debug mode check
* Added pyyaml load check

