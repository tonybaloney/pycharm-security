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


class SnykChecker (private val apiKey: String, private val orgId: String ): BasePackageChecker() {
    var baseUrl = "https://snyk.io/api/v1"

    data class SnykOrgRecord(
        val name: String?,
        val id: String?
    )

    class SnykIssue (val record: SnykRecord, pyPackage: PyPackage): PackageIssue(pyPackage = pyPackage) {
        override fun getMessage(): String {
            return "${record.title} (${record.severity} severity) found in ${record.`package`} impacting version ${record.version}. <br/>See <a href='${record.url}'>${record.id}</a> for details"
        }
    }

    data class SnykRecord (
            val id: String,
            val url: String,
            val title: String,
            val type: String,
            val paths: List<String>,
            val `package`: String,
            val version: String,
            val severity: String,
            val language: String,
            val packageManager: String,
            val specs: List<String>,
            val semver: Any
    )

    data class SnykVulnerabilityList(
        val vulnerabilities: List<SnykRecord>
    )

    data class SnykTestApiResponse(
        val ok: Boolean,
        val issues: SnykVulnerabilityList?,
        val dependencyCount: Int,
        val org: SnykOrgRecord?,
        val packageManager: String
    )

    private suspend fun load(packageName: String, packageVersion: String): SnykTestApiResponse? {
        val client = HttpClient(Apache) {
            install(ContentNegotiation) {
                gson {
                    serializeNulls()
                    disableHtmlEscaping()
                }
            }
            defaultRequest {
                headers {
                    header("Authorization", "token $apiKey")
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
            return client.get("$baseUrl/test/pip/$packageName/$packageVersion?org=$orgId").body()
        } catch (t: TimeoutCancellationException){
            throw PackageCheckerLoadException("Timeout connecting to Snyk API.")
        } catch (t: SocketTimeoutException){
            throw PackageCheckerLoadException("Timeout on socket.")
        } catch (t: ServerResponseException){
            throw PackageCheckerLoadException("Server error on Snyk API.")
        }
    }

    override fun hasMatch(pythonPackage: PyPackage?): Boolean {
        return true // Hardcode to prevent it being called twice
    }

    override suspend fun getMatches (pythonPackage: PyPackage?): List<SnykIssue> {
        if (pythonPackage==null) return listOf()
        val records: ArrayList<SnykIssue> = ArrayList()
        val data = load(pythonPackage.name.lowercase(), pythonPackage.version) ?: return records
        if (data.ok) return records
        if (data.issues == null) return records

        data.issues.vulnerabilities.forEach { issue ->
            records.add(SnykIssue(issue, pythonPackage))
        }

        return records
    }
}