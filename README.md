# PyCharm Python Security plugin

![](src/main/resources/META-INF/pluginIcon.svg)

A plugin to run security checks for common flaws in Python code and suggest quick fixes.

## Current checks

* Use of `yaml.load()` can cause arbitrary code execution
* Use of `debug=True` when instantiating flask applications
* Use of `verify=False` when making HTTP requests

