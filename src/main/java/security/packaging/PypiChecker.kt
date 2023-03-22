package security.packaging

import com.jetbrains.python.packaging.PyPackage
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.TimeoutCancellationException
import java.net.SocketTimeoutException


class PypiChecker : BasePackageChecker() {
    var baseUrl = "https://pypi.org"

    class PyPiIssue (val record: VulnerabilityRecord, pyPackage: PyPackage): PackageIssue(pyPackage = pyPackage) {
        override fun getMessage(): String {
            return "${record.id} found in ${pyPackage.name} impacting version ${pyPackage.version} installed in ${pyPackage.location}. <br/>See <a href='${record.link}'>${record.link}</a> for details"
        }
    }

    data class VulnerabilityRecord (
            val id: String,
            val aliases: List<String>?,
            val details: String,
            val fixed_in: List<String>?,
            val link: String,
            val source: String
    )

    data class PyPiPackageApiResponse(
            val info: Any,
            val last_serial: Int,
            val releases: Any,
            val urls: Any,
            val vulnerabilities: List<VulnerabilityRecord>?
    )

    private suspend fun load(packageName: String, packageVersion: String): PyPiPackageApiResponse? {
        val client = HttpClient(Apache) {
            install(ContentNegotiation) {
                gson {
                    serializeNulls()
                    disableHtmlEscaping()
                }
            }
            defaultRequest {
                headers {
                    header("Content-Type", "application/json; charset=utf-8")
                }
            }
            engine {
                connectTimeout = 60_000
                connectionRequestTimeout = 60_000
                socketTimeout = 60_000
            }
        }

        try {
            return client.get("$baseUrl/pypi/$packageName/$packageVersion/json").body()
        } catch (t: TimeoutCancellationException){
            throw PackageCheckerLoadException("Timeout connecting to PyPi API.")
        } catch (t: SocketTimeoutException){
            throw PackageCheckerLoadException("Timeout on socket.")
        } catch (t: ServerResponseException){
            throw PackageCheckerLoadException("Server error on PyPi API.")
        }
    }

    override fun hasMatch(pythonPackage: PyPackage?): Boolean {
        return true // Hardcode to prevent it being called twice
    }

    override suspend fun getMatches (pythonPackage: PyPackage?): List<PyPiIssue> {
        if (pythonPackage==null) return listOf()
        val records: ArrayList<PyPiIssue> = ArrayList()
        val data = load(pythonPackage.name.lowercase(), pythonPackage.version) ?: return records
        if (data.vulnerabilities == null) return records

        data.vulnerabilities.forEach { issue ->
            records.add(PyPiIssue(issue, pythonPackage))
        }

        return records
    }
}