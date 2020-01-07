package security;

public class Checks {
    public static class CheckType {
        public String Code;
        public String Message;
        public CheckType(String code, String message){
            this.Code = code;
            this.Message = message;
        }

        @Override
        public String toString() {
            return this.Code + ": " + this.Message;
        }
    }

    public static final CheckType PyyamlUnsafeLoadCheck = new CheckType("YML100", "Use of unsafe yaml load. Allows instantiation of arbitrary objects. Consider yaml.safe_load().");

    public static final CheckType FlaskDebugModeCheck = new CheckType("FLK100", "Flask app appears to be run with debug=True, which exposes the Werkzeug debugger and allows the execution of arbitrary code.");

    public static final CheckType RequestsNoVerifyCheck = new CheckType("RQ100", "Setting verify=False when using requests bypasses SSL verification and leaves requests susceptible to MITM attacks.");

}
