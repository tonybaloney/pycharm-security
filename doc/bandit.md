# Comparison with Bandit

The pycharm-security plugin has some significant differences to Bandit:

 - It is fully integrated into PyCharm (and other Jetbrains IDE products)
 - It uses the Intellij PSI Tree instead of the Python AST. Your code is checked as you type. There is no need to re-run the scanner after modifying files.
 - Many of the checks offer quick fixes
 - It scans your installed packages in the projects against SafetyDB, bandit only looks at your code
 - Many of the checks use the context of the code to reduce false-positives, where bandit would otherwise raise an alert

## Equivalent Checks

This plugin is still lacking some of the checks that are offered in Bandit.

List of bandit plugins that have equivalent checks in pycharm-security:

| Plugin                                                    | Status                           | Check                      |
|-----------------------------------------------------------|----------------------------------|----------------------------|
| app_debug.py (Flask Debug)                                | Yes                              | [FLK100](checks/FLK100.md) |
| asserts.py                                                | Yes                              | [AST100](checks/AST100.md) |
| crypto_request_no_cert_validation.py (requests no verify) | Yes, for both httpx and requests | [RQ100](checks/RQ100.md))  |
| django_sql_injection.py                                   | No                               |                            |
| django_xss.py                                             | No                               |                            |
| exec.py                                                   | Yes                              | [EX100](checks/EX100.md)   |
| general_bad_file_permissions.py                           | Yes                              | [OS100](checks/OS100.md)   |
| general_bind_all_interfaces.py                            | Yes                              | [NET100](checks/NET100.md) |
| general_hardcoded_password.py                             | Yes                              | [PW101](checks/PW101.md)   |
| general_hardcoded_tmp.py                                  | No                               |                            |
| hashlib_new_insecure_functions.py                         | Yes                              | [HL100](checks/HL100.md)   |
| injection_paramiko.py                                     | No                               |                            |
| injection_shell.py                                        | Yes                              | [PR100](checks/PR100.md)   |
| injection_sql.py                                          | Yes                              | [SQL100](checks/SQL100.md) |
| injection_wildcard.py                                     | No                               |                            |
| insecure_ssl_tls.py                                       | No                               |                            |
| jinja2_templates.py                                       | Yes                              | [JJ100](checks/JJ100.md)   |
| mako_templates.py                                         | Yes                              | [MK100](checks/MK100.md)   |
| ssh_no_host_key_verification.py                           | Yes                              | [PAR100](checks/PAR100.md) |
| try_except_continue.py                                    | Yes                              | [TRY101](checks/TRY101.md) |
| try_except_pass.py                                        | Yes                              | [TRY100](checks/TRY100.md) |
| weak_cryptographic_key.py                                 | No                               |                            |
| yaml_load.py                                              | Yes                              | [YML100](checks/YML100.md) |


List of checks that are in this plugin but **not** in bandit:

| Check                      | Purpose                                                                    |
|----------------------------|----------------------------------------------------------------------------|
| [DJG100](checks/DJG100.md) | Django Debug mode enabled                                                  |
| [DJG200](checks/DJG200.md) | Django CSRF Middleware misconfiguration                                    |
| [DJG201](checks/DJG201.md) | Django Clickjacking Middleware misconfiguration                            |
| [HL101](checks/HL101.md)   | Use of hashing algorithms that are susceptible to length-extension attacks |
| [PW100](checks/PW100.md)   | Potential timing attack detection with string comparisons                  |
| [RQ101](checks/RQ101.md)   | No SSL verification on the httpx library                                   |
| [TMP100](checks/TMP100.md) | Use of insecure tmp file creation                                          |
