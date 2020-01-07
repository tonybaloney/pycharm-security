package security

object Checks {
    val PyyamlUnsafeLoadCheck = CheckType("YML100", "Use of unsafe yaml load. Allows instantiation of arbitrary objects. Consider yaml.safe_load().")
    val FlaskDebugModeCheck = CheckType("FLK100", "Flask app appears to be run with debug=True, which exposes the Werkzeug debugger and allows the execution of arbitrary code.")
    val RequestsNoVerifyCheck = CheckType("RQ100", "Setting verify=False when using requests bypasses SSL verification and leaves requests susceptible to MITM attacks.")

    class CheckType(var Code: String, var Message: String) {
        override fun toString(): String {
            return "$Code: $Message"
        }
    }
}