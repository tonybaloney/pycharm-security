package security.validators

val PasswordVariableNames = arrayOf("password", "PASSWORD", "passwd", "secret", "token")

val BadSSLProtocols = arrayOf("ssl.PROTOCOL_SSLv23", "ssl.PROTOCOL_SSLv2", "ssl.PROTOCOL_SSLv3", "ssl.PROTOCOL_TLSv1", "ssl.PROTOCOL_TLSv1_1")

val ShellApis = arrayOf(
        "os.system",
        "os.popen", "os.popen2", "os.popen3", "os.popen4",
        "popen2.popen2", "popen2.popen3", "popen2.popen4", "popen2.Popen3", "popen2.Popen4",
        "commands.getoutut", "commands.getstatusoutput")