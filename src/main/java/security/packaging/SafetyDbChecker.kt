package security.packaging

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.jetbrains.python.packaging.PyPackage
import java.io.IOException
import java.io.Reader
import java.net.URL

class SafetyDbChecker : BasePackageChecker {
    private lateinit var index: SafetyDbIndex
    private lateinit var database: SafetyDbDatabase
    var baseUrl = "https://pyup.io/aws/safety/pycharm/2.0.0"

    class SafetyDbIssue (val record: SafetyDbRecord, pyPackage: PyPackage): PackageIssue(pyPackage = pyPackage) {
        override fun getMessage(): String {
            var message: String
            if (record.cve.isNullOrEmpty()){
                message = record.advisory
            } else {
                message = "${record.advisory}<br>See <a href='https://cve.mitre.org/cgi-bin/cvename.cgi?name=${record.cve}'>${record.cve}</a>"
            }
            message += " Package is installed in ${pyPackage.location}."
            return message
        }
    }

    data class SafetyDbRecord(
        val specs: List<String>,
        val type: String,
        val cve: String?,
        val advisory: String,
        val id: String,
        val transitive: Boolean,
        val more_info_path: String
    )

    data class SafetyDbMeta(
        val advisory: String,
        val timestamp: Int,
        val last_updated: String,
        val base_domain: String?,
        val attribution: String?
    )

    data class SafetyDbIndex(
        val meta: SafetyDbMeta?,
        val vulnerable_packages: Map<String?, List<String>>?
    )
    data class SafetyDbDatabase(
        val meta: SafetyDbMeta?,
        val vulnerable_packages: Map<String?, List<SafetyDbRecord>>?
    )

    constructor(apiKey: String? = null) {
        val fullUrl = URL("$baseUrl/insecure_full.json")
        val indexUrl = URL("$baseUrl/insecure.json")
        val fullConnection = fullUrl.openConnection()
        val indexConnection = indexUrl.openConnection()
        if (!apiKey.isNullOrEmpty()) {
            fullConnection.setRequestProperty("X-Api-Key", apiKey)
            indexConnection.setRequestProperty("X-Api-Key", apiKey)
        }
        fullConnection.setRequestProperty("schema-version", "2.0.0")
        indexConnection.setRequestProperty("schema-version", "2.0.0")
        fullConnection.setRequestProperty("User-Agent", "PyCharm Security Extension")
        indexConnection.setRequestProperty("User-Agent", "PyCharm Security Extension")
        try {
            val fullReader = fullConnection.getInputStream().reader()
            val indexReader = indexConnection.getInputStream().reader()
            load(fullReader, indexReader)
        } catch (io: IOException){
            if (io.message.isNullOrEmpty().not())
                throw PackageCheckerLoadException(io.message!!)
            else
                throw PackageCheckerLoadException("Could not load data from SafetyDB API")
        }catch (io: com.google.gson.JsonSyntaxException){
            throw PackageCheckerLoadException("Could not load data from SafetyDB API, JSON file is corrupted")
        }
    }

    constructor (databaseReader: Reader, lookupReader: Reader ) {
        load(databaseReader, lookupReader)
    }

    private fun load(databaseReader: Reader, lookupReader: Reader) {
        val gson = GsonBuilder().create()
        val recordLookupType = object : TypeToken<SafetyDbIndex>() {}.type
        index = gson.fromJson(lookupReader, recordLookupType)
        if (index.vulnerable_packages == null) {
            throw PackageCheckerLoadException("Could not load data from SafetyDB API")
        }

        val recordDatabaseType = object : TypeToken<SafetyDbDatabase>() {}.type
        database = gson.fromJson(databaseReader, recordDatabaseType)
        if (database.vulnerable_packages == null) {
            throw PackageCheckerLoadException("Could not load data from SafetyDB API")
        }
    }

    override fun hasMatch(pythonPackage: PyPackage?): Boolean{
        if (pythonPackage==null) return false
        for (record in index.vulnerable_packages?.get(pythonPackage.name.lowercase()) ?: return false){
            val specs = parseVersionSpecs(record) ?: continue
            if (specs.all { it != null && it.matches(pythonPackage.version) })
                return true
        }
        return false
    }

    override suspend fun getMatches (pythonPackage: PyPackage?): List<SafetyDbIssue> {
        if (pythonPackage==null) return listOf()
        val records: ArrayList<SafetyDbIssue> = ArrayList()
        for (record in database.vulnerable_packages?.get(pythonPackage.name.lowercase()) ?: error("Package not in database")){
            for (spec in record.specs) {
                val specs = parseVersionSpecs(spec) ?: continue
                if (specs.all { it != null && it.matches(pythonPackage.version) }) {
                    records.add(SafetyDbIssue(record, pythonPackage))
                    break
                }
            }
        }
        return records.toList()
    }
}