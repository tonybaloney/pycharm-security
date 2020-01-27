# Supported Checks

* [PW100](checks/PW100.md) Matching inputs, secrets or tokens using the == operator is vulnerable to timing attacks. Use compare_digest() instead.
* [HL100](checks/HL100.md) MD4, MD5, SHA, and SHA1 hashing algorithms should not be used for obfuscating or protecting data
* [HL101](checks/HL101.md) MD5, SHA-1, RIPEMD-160, Whirlpool and the SHA-256 / SHA-512 hash algorithms all vulnerable to length-extension attacks and should not be used for obfuscating or protecting data
* [YML100](checks/YML100.md) Use of `yaml.load()` can cause arbitrary code execution. Suggests and has a "Quick Fix" to replace with `safe_load()` using existing arguments
* [FLK100](checks/FLK100.md) Use of `debug=True` when instantiating flask applications
* [RQ100](checks/RQ100.md) Use of `verify=False` when making HTTP requests using the `requests` package
* [RQ101](checks/RQ101.md) Use of `verify=False` when making HTTP requests using the `httpx` package
* [PR100](checks/PR100.md) Use of `shell=True` when running `subprocess.call` from the standard library
* [TMP100](checks/TMP100.md) Use of `tempfile.mktemp`
* [DJG100](checks/DJG100.md) Setting `DEBUG = True` in a `settings.py` file (assumed Django project settings)
* [JJ100](checks/JJ100.md) Use of Jinja2 without autoescaped input
