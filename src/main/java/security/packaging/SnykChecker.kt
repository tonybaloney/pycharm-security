package security.packaging

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jetbrains.python.packaging.PyPackage
import java.io.IOException
import java.net.URL


class SnykChecker (val apiKey: String, val orgId: String ): BasePackageChecker() {
    var baseUrl = "https://snyk.io/api/v1"

    data class SnykOrgRecord(
        val name: String?,
        val id: String?
    )

    class SnykIssue (val record: SnykRecord): PackageIssue {
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

    private fun load(packageName: String, packageVersion: String): SnykTestApiResponse? {
        val fullUrl = URL("$baseUrl/test/pip/$packageName/$packageVersion?org=$orgId")

        val fullConnection = fullUrl.openConnection()
        fullConnection.setRequestProperty("Authorization", "token $apiKey")
        fullConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8")

        try {
            val fullReader = fullConnection.getInputStream().reader()
            val responseType = object : TypeToken<SnykTestApiResponse>() {}.type
            return Gson().fromJson(fullReader, responseType)
        } catch (io: IOException){
            if (io.message.isNullOrEmpty().not())
                throw PackageCheckerLoadException(io.message!!)
            else
                throw PackageCheckerLoadException("Could not load data from Snyk API")
        }
    }

    override fun hasMatch(pythonPackage: PyPackage): Boolean{
        val data = load(pythonPackage.name.toLowerCase(), pythonPackage.version)
        return data?.ok?.not() ?: false
    }

    override fun getMatches (pythonPackage: PyPackage): List<SnykIssue> {
        val records: ArrayList<SnykIssue> = ArrayList()
        val data = load(pythonPackage.name.toLowerCase(), pythonPackage.version) ?: return records
        if (data.ok) return records
        if (data.issues == null) return records

        data.issues.vulnerabilities.forEach { issue ->
            records.add(SnykIssue(issue))
        }

        return records
    }
}