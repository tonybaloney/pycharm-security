package security.validators

val PasswordVariableNames = arrayOf("password", "PASSWORD", "passwd", "secret", "token")

val BadSSLProtocols = arrayOf("ssl.PROTOCOL_SSLv23", "ssl.PROTOCOL_SSLv2", "ssl.PROTOCOL_SSLv3", "ssl.PROTOCOL_TLSv1", "ssl.PROTOCOL_TLSv1_1")

val ShellApis = arrayOf(
        "os.system", "posix.system",
        "posix.popen", "posix.popen2", "posix.popen3", "posix.popen4",
        "os.popen", "os.popen2", "os.popen3", "os.popen4",
        "popen2.popen2", "popen2.popen3", "popen2.popen4", "popen2.Popen3", "popen2.Popen4",
        "commands.getoutut", "commands.getstatusoutput")

val SpawnShellApis = arrayOf(
        "os.execl", "os.execle", "os.execlp", "os.execlpe", "os.execv", "os.execve", "os.execvp", "os.execvpe",
        "os.spawnl", "os.spawnle", "os.spawnlp", "os.spawnlpe", "os.spawnv", "os.spawnve", "os.spawnvp", "os.spawnvpe"
)