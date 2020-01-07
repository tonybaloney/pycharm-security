# PyCharm Python Security plugin

![](src/main/resources/META-INF/pluginIcon.svg)

A plugin to run security checks for common flaws in Python code and suggest quick fixes.

![](src/main/resources/META-INF/screenshot.png)

## Current checks

* Use of `yaml.load()` can cause arbitrary code execution. Suggests and has a "Quick Fix" to replace with `safe_load()` using existing arguments
* Use of `debug=True` when instantiating flask applications
* Use of `verify=False` when making HTTP requests using the `requests` package
* Use of `verify=False` when making HTTP requests using the `httpx` package
* Use of `shell=True` when running `subprocess.call` from the standard library
* Use of `tempfile.mktemp`
* Setting `DEBUG = True` in a `settings.py` file (assumed Django project settings)

## Release History

### master

### 1.0.3

* Added django debug mode check
* Added `tempfile.mktemp` check with fixer to replace `tempfile.mkstemp` with existing arguments
* Added subprocess.call(shell=true) check
* Added httpx no-verify check
* Added requests no-verify check

### 1.0.2

* Added flask debug mode check
* Added pyyaml load check

