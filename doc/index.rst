PyCharm Python Security plugin
==============================

Pycharm-security is a plugin for PyCharm, or JetBrains IDEs with the Python plugin.

The plugin looks at your Python code for common security vulnerabilities and suggests fixes.

.. image:: https://readthedocs.org/projects/pycharm-security/badge/?version=latest
    :target: https://pycharm-security.readthedocs.io/en/latest/?badge=latest
    :alt: Documentation Status
.. image:: https://github.com/tonybaloney/pycharm-security/workflows/CI/badge.svg
    :target: https://github.com/tonybaloney/pycharm-security/actions
    :alt: GitHub CI Status
.. image:: https://img.shields.io/jetbrains/plugin/v/13609-python-security.svg
    :target: https://plugins.jetbrains.com/plugin/13609-python-security
    :alt: Plugin Downloads
.. image:: https://img.shields.io/jetbrains/plugin/d/13609-python-security.svg
    :target: https://plugins.jetbrains.com/plugin/13609-python-security
    :alt: Plugin Version
.. image:: https://img.shields.io/docker/cloud/build/anthonypjshaw/pycharm-security
    :target: https://hub.docker.com/r/anthonypjshaw/pycharm-security
    :alt: Docker Status
.. image:: https://codecov.io/gh/tonybaloney/pycharm-security/branch/master/graph/badge.svg
    :target: https://codecov.io/gh/tonybaloney/pycharm-security
    :alt: Coverage Status

.. toctree::
   :maxdepth: 1
   :caption: Contents:

   installation
   usage
   github
   checks/index
   fixes/index
   development
   bandit
   django

Features
~~~~~~~~

* Over 20 builtin code checks giving your contextual security warnings in your code
* Misconfiguration warnings for Django and Flask web frameworks
* Cross-Site-Scripting detection for both Jinja2 and Mako templating engines
* SQL Injection detection in all Python string formats
* Automatic reporting of known vulnerabilities and CVEs in your installed Python Packages within PyCharm
* Detection of security flaws and misconfiguration in 3rd party libraries like Jinja2, Paramiko and Mako
* Can be used to scan large code bases with inspection profiles
* Configurable alert levels and warning suppression by file, line, or project
* Scan code in your CI/CD using Docker

.. image:: _static/screenshot.png

Release History
~~~~~~~~~~~~~~~

See `Release History <https://github.com/tonybaloney/pycharm-security/blob/master/HISTORY.md>`_ for the release history.

Contributing
~~~~~~~~~~~~

If you would like to alter or add new checks and fixes, see the :ref:`development` page.

If you have bugs or issues with the existing functionality, please report them in `GitHub Issues <https://github.com/tonybaloney/pycharm-security/issues>`_.

License
~~~~~~~

This plugin is MIT Licensed.

Credits
~~~~~~~

Credit to the PyUp.io team for SafetyDB. This plugin uses SafetyDB whilst scanning packages.

SafetyDB is licensed under `Creative Commons Attribution-NonCommercial 4.0 International <https://creativecommons.org/licenses/by/4.0/>`_
