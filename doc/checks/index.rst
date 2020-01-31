List of Checks
==============

General and Best Practice
~~~~~~~~~~~~~~~~~~~~~~~~~

.. toctree::
    :maxdepth: 1

    AST100 : Use of `assert` statement in non-test file <AST100>
    TRY100 : Use of a `try` ... `except` block where the except block does not contain anything other than comments and a `pass` statement <TRY100>
    TRY101 : Use of a `try` ... `except` block where the except block does not contain anything other than comments and a `continue` statement <TRY101>
    OS100 : Calls to `os.chmod()` with dangerous POSIX permissions <OS100>
    PR100 : Use of `shell=True` when running `subprocess.call` from the standard library <PR100>
    TMP100 : Use of insecure `tempfile.mktemp` <TMP100>
    EX100 : Use of builtin `exec()` function for non-string literal <EX100>
    SQL100 : Possible SQL injection with Python string formatting <SQL100>

Passwords and Security
~~~~~~~~~~~~~~~~~~~~~~

.. toctree::
   :maxdepth: 1

   PW100 : Matching inputs, secrets or tokens using the == operator is vulnerable to timing attacks. Use compare_digest() instead. <PW100>
   PW101 : Hardcoded passwords, secrets or keys detected <PW101>

Encryption and Hashing
~~~~~~~~~~~~~~~~~~~~~~

.. toctree::
    :maxdepth: 1

    HL100 : MD4, MD5, SHA, and SHA1 hashing algorithms should not be used for obfuscating or protecting data <HL100>
    HL101 : MD5, SHA-1, RIPEMD-160, Whirlpool and the SHA-256 / SHA-512 hash algorithms all vulnerable to length-extension attacks and should not be used for obfuscating or protecting data <HL101>

Django Web Framework
~~~~~~~~~~~~~~~~~~~~

.. toctree::
    :maxdepth: 1

    DJG100 : Setting `DEBUG = True` in a `settings.py` file (assumed Django project settings) <DJG100>
    DJG101 : Use of `RawSQL` cursor with quoted parameters will expose the query to SQL injection <DJG101>
    DJG200 : Django middleware is missing `CsrfViewMiddleware`, which blocks cross-site request forgery <DJG200>
    DJG201 : Django middleware is missing `XFrameOptionsMiddleware`, which blocks clickjacking. <DJG201>

Deserialization
~~~~~~~~~~~~~~~

.. toctree::
    :maxdepth: 1

    YML100 (`pyyaml`) : Use of `yaml.load()` can cause arbitrary code execution. Suggests and has a "Quick Fix" to replace with `safe_load()` using existing arguments <YML100>

Flask Web Framework
~~~~~~~~~~~~~~~~~~~

.. toctree::
    :maxdepth: 1

    FLK100 : Use of `debug=True` when instantiating flask applications <FLK100>

SSL and Networking
~~~~~~~~~~~~~~~~~~

.. toctree::
    :maxdepth: 1

    NET100 : Socket binding to unspecified IPv4 or IPv6 address <NET100>
    RQ100 (`requests`) : Use of `verify=False` when making HTTP requests using the `requests` package <RQ100>
    RQ101 (`httpx`) : Use of `verify=False` when making HTTP requests using the `httpx` package <RQ101>
    PAR100 (`paramiko`) : Host key inspection bypass using the `paramiko` SSH library <PAR100>

Templating Engines
~~~~~~~~~~~~~~~~~~

.. toctree::
    :maxdepth: 1

    JJ100 (`jinja2`) : Use of Jinja2 without autoescaped input <JJ100>
    MK100 (`mako`) : Use of Mako template without escaped input <MK100>
