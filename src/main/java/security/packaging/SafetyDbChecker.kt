package security.packaging

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jetbrains.python.packaging.PyPackage
import java.io.IOException
import java.io.Reader
import java.net.URL

class SafetyDbChecker : BasePackageChecker {
    private lateinit var database: Map<String?, List<SafetyDbRecord>>
    private lateinit var lookup: Map<String?, List<String>>

    class SafetyDbIssue (val record: SafetyDbRecord, pyPackage: PyPackage): PackageIssue(pyPackage = pyPackage) {
        override fun getMessage(): String {
            return if (record.cve.isNullOrEmpty()){
                record.advisory
            } else {
                "${record.advisory}<br>See <a href='https://cve.mitre.org/cgi-bin/cvename.cgi?name=${record.cve}'>${record.cve}</a>"
            }
        }
    }

    data class SafetyDbRecord(
        val advisory: String,
        val cve: String?,
        val id: String,
        val specs: List<String>,
        val v: String
    )

    constructor() {
        load(
            this.javaClass.classLoader.getResourceAsStream("safety-db/insecure_full.json").reader(),
            this.javaClass.classLoader.getResourceAsStream("safety-db/insecure.json").reader())
    }

    constructor(apiKey: String, baseUrl: String) {
        val fullUrl = URL("$baseUrl/insecure_full.json")
        val indexUrl = URL("$baseUrl/insecure.json")
        val fullConnection = fullUrl.openConnection()
        val indexConnection = indexUrl.openConnection()
        fullConnection.setRequestProperty("X-Api-Key", apiKey)
        indexConnection.setRequestProperty("X-Api-Key", apiKey)
        try {
            val fullReader = fullConnection.getInputStream().reader()
            val indexReader = indexConnection.getInputStream().reader()
            load(fullReader, indexReader)
        } catch (io: IOException){
            if (io.message.isNullOrEmpty().not())
                throw PackageCheckerLoadException(io.message!!)
            else
                throw PackageCheckerLoadException("Could not load data from SafetyDB API")
        }
    }

    constructor (databaseReader: Reader, lookupReader: Reader ) {
        load(databaseReader, lookupReader)
    }

    private fun load(databaseReader: Reader, lookupReader: Reader) {
        val recordLookupType = object : TypeToken<Map<String?, List<String>>>() {}.type
        lookup = Gson().fromJson<Map<String?, List<String>>>(lookupReader, recordLookupType)

        val recordDatabaseType = object : TypeToken<Map<String?, List<SafetyDbRecord>>>() {}.type
        database = Gson().fromJson<Map<String?, List<SafetyDbRecord>>>(databaseReader, recordDatabaseType)
    }

    override fun hasMatch(pythonPackage: PyPackage): Boolean{
        for (record in lookup[pythonPackage.name.toLowerCase()] ?: return false){
            val specs = parseVersionSpecs(record) ?: continue
            if (specs.all { it != null && it.matches(pythonPackage.version) })
                return true
        }
        return false
    }

    override suspend fun getMatches (pythonPackage: PyPackage): List<SafetyDbIssue> {
        val records: ArrayList<SafetyDbIssue> = ArrayList()
        for (record in database[pythonPackage.name.toLowerCase()] ?: error("Package not in database")){
            val specs = parseVersionSpecs(record.v) ?: continue
            if (specs.all { it != null && it.matches(pythonPackage.version) })
                records.add(SafetyDbIssue(record, pythonPackage))
        }
        return records.toList()
    }
}