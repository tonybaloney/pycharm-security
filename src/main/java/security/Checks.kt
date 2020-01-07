package security

object Checks {
    val PyyamlUnsafeLoadCheck = CheckType("YML100", "Use of unsafe yaml load. Allows instantiation of arbitrary objects. Consider yaml.safe_load().")
    val FlaskDebugModeCheck = CheckType("FLK100", "Flask app appears to be run with debug=True, which exposes the Werkzeug debugger and allows the execution of arbitrary code.")
    val RequestsNoVerifyCheck = CheckType("RQ100", "Setting verify=False when using requests bypasses SSL verification and leaves requests susceptible to MITM attacks.")
    val HttpxNoVerifyCheck = CheckType("RQ101", "Setting verify=False when using httpx bypasses SSL verification and leaves requests susceptible to MITM attacks.")
    val SubprocessCallShellCheck = CheckType("PR100", "Calling subprocess.call with shell=True can leave the host shell open to local code execution or remote code execution attacks.")

    class CheckType(var Code: String, var Message: String) {
        override fun toString(): String {
            return "$Code: $Message"
        }
    }
}