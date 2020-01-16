package security

object Checks {
    val PyyamlUnsafeLoadCheck = CheckType("YML100", "Use of unsafe yaml load. Allows instantiation of arbitrary objects. Consider yaml.safe_load().")
    val FlaskDebugModeCheck = CheckType("FLK100", "Flask app appears to be run with debug=True, which exposes the Werkzeug debugger and allows the execution of arbitrary code.")
    val RequestsNoVerifyCheck = CheckType("RQ100", "Setting verify=False when using requests bypasses SSL verification and leaves requests susceptible to MITM attacks.")
    val HttpxNoVerifyCheck = CheckType("RQ101", "Setting verify=False when using httpx bypasses SSL verification and leaves requests susceptible to MITM attacks.")
    val SubprocessCallShellCheck = CheckType("PR100", "Calling subprocess.call with shell=True can leave the host shell open to local code execution or remote code execution attacks.")
    val TempfileMktempCheck = CheckType("TMP100", "The way that tempfile.mktemp creates temporary files is insecure and leaves it open to attackers replacing the file contents. Use tempfile.mkstemp instead.")
    val DjangoDebugModeCheck = CheckType("DJG100", "Running Django in Debug mode is highly insecure and should only be used for local development purposes.")
    val InsecureHashAlgorithms = CheckType("HL100", "md4, md5, sha, and sha1 hashing algorithms have cryptographically weak algorithms and should not be used for obfuscating or protecting data.")
    val LengthAttackHashAlgorithms = CheckType("HL101", "MD5, SHA-1, RIPEMD-160, Whirlpool and the SHA-256 / SHA-512 hash algorithms all vulnerable to length-extension attacks and should not be used for obfuscating or protecting data.")

    class CheckType(var Code: String, var Message: String) {
        override fun toString(): String {
            return "$Code: $Message"
        }
    }
}