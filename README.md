# PyCharm Python Security plugin

![](src/main/resources/META-INF/pluginIcon.svg)

A plugin to run security checks for common flaws in Python code and suggest quick fixes.

![](src/main/resources/META-INF/screenshot.png)

## Current checks

* Use of `yaml.load()` can cause arbitrary code execution. Suggests and has a "Quick Fix" to replace with `safe_load()` using existing arguments
* Use of `debug=True` when instantiating flask applications
* Use of `verify=False` when making HTTP requests using the `requests` package
* Use of `verify=False` when making HTTP requests using the `httpx` package

