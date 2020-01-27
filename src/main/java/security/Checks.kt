package security

object Checks {
    val PyyamlUnsafeLoadCheck = CheckType("YML100", "Use of unsafe yaml load. Allows instantiation of arbitrary objects. Consider yaml.safe_load().")
    val FlaskDebugModeCheck = CheckType("FLK100", "Flask app appears to be run with debug=True, which exposes the Werkzeug debugger and allows the execution of arbitrary code.")
    val RequestsNoVerifyCheck = CheckType("RQ100", "Setting verify=False when using requests bypasses SSL verification and leaves requests susceptible to MITM attacks.")
    val HttpxNoVerifyCheck = CheckType("RQ101", "Setting verify=False when using httpx bypasses SSL verification and leaves requests susceptible to MITM attacks.")
    val SubprocessShellCheck = CheckType("PR100", "Calling subprocess commands with shell=True can leave the host shell open to local code execution or remote code execution attacks.")
    val TempfileMktempCheck = CheckType("TMP100", "The way that tempfile.mktemp creates temporary files is insecure and leaves it open to attackers replacing the file contents. Use tempfile.mkstemp instead.")
    val DjangoDebugModeCheck = CheckType("DJG100", "Running Django in Debug mode is highly insecure and should only be used for local development purposes.")
    val DjangoCsrfMiddlewareCheck = CheckType("DJG200", "Django middleware is missing CsrfViewMiddleware, which blocks cross-site request forgery.")
    val DjangoClickjackMiddlewareCheck = CheckType("DJG201", "Django middleware is missing XFrameOptionsMiddleware, which blocks clickjacking.")
    val InsecureHashAlgorithms = CheckType("HL100", "MD4, MD5, SHA, and SHA1 hashing algorithms have cryptographically weak algorithms and should not be used for obfuscating or protecting data.")
    val LengthAttackHashAlgorithms = CheckType("HL101", "MD5, SHA-1, RIPEMD-160, Whirlpool, and the SHA-256 / SHA-512 hash algorithms are all vulnerable to length-extension attacks and should not be used for obfuscating or protecting data. Use within a HMAC is not vulnerable.")
    val TimingAttackCheck = CheckType("PW100", "Matching inputs, secrets or tokens using the == operator is vulnerable to timing attacks. Use compare_digest() instead.")
    val JinjaAutoinspectCheck = CheckType("JJ100", "Jinja does not inspect or sanitize input by default, leaving rendered templates open to XSS. Use autoinspect=True.")


    class CheckType(var Code: String, var Message: String) {
        override fun toString(): String {
            return "$Code: $Message"
        }

        fun getDescription(): String {
            return this.toString() // TODO : Expand
        }
    }
}