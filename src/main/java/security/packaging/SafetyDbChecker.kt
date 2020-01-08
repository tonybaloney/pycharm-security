package security.packaging

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.util.text.StringUtil
import com.jetbrains.python.packaging.PyPackage
import com.jetbrains.python.packaging.pyRequirementVersionSpec
import com.jetbrains.python.packaging.requirement.PyRequirementRelation
import com.jetbrains.python.packaging.requirement.PyRequirementVersionSpec
import java.io.InputStream

class SafetyDbChecker {
    private val database: Map<String?, List<SafetyDbRecord>>
    private val lookup: Map<String?, List<String>>

    data class SafetyDbRecord(
        val advisory: String,
        val cve: String,
        val id: String,
        val specs: List<String>,
        val v: String
    )
    init {
        val insecure_in: InputStream = this.javaClass.classLoader.getResourceAsStream("safety-db/insecure.json")
        val recordLookupType = object : TypeToken<Map<String?, List<String>>>() {}.type
        lookup = Gson().fromJson<Map<String?, List<String>>>(insecure_in.reader(), recordLookupType)

        val insecure_full_in: InputStream = this.javaClass.classLoader.getResourceAsStream("safety-db/insecure_full.json")
        val recordDatabaseType = object : TypeToken<Map<String?, List<SafetyDbRecord>>>() {}.type
        database = Gson().fromJson<Map<String?, List<SafetyDbRecord>>>(insecure_full_in.reader(), recordDatabaseType)
    }

    fun hasMatch(pythonPackage: PyPackage): Boolean{
        for (record in lookup[pythonPackage.name] ?: return false){
            val spec = parseVersionSpec(record) ?: continue
            if (spec.matches(pythonPackage.version))
                return true
        }
        return false
    }

    fun getMatches (pythonPackage: PyPackage): List<SafetyDbRecord> {
        var records: ArrayList<SafetyDbRecord> = ArrayList<SafetyDbRecord>()
        for (record in database[pythonPackage.name] ?: error("Package not in database")){
            val spec = parseVersionSpec(record.v) ?: continue
            if (spec.matches(pythonPackage.version))
                records.add(record)
        }
        return records.toList()
    }
    private fun parseVersionSpec(versionSpec: String): PyRequirementVersionSpec? {
        /// Taken from PyRequirementParser, but that function is Private :-(
        var relation: PyRequirementRelation? = null
        if (versionSpec.startsWith("===")) {
            relation = PyRequirementRelation.STR_EQ
        } else if (versionSpec.startsWith("==")) {
            relation = PyRequirementRelation.EQ
        } else if (versionSpec.startsWith("<=")) {
            relation = PyRequirementRelation.LTE
        } else if (versionSpec.startsWith(">=")) {
            relation = PyRequirementRelation.GTE
        } else if (versionSpec.startsWith("<")) {
            relation = PyRequirementRelation.LT
        } else if (versionSpec.startsWith(">")) {
            relation = PyRequirementRelation.GT
        } else if (versionSpec.startsWith("~=")) {
            relation = PyRequirementRelation.COMPATIBLE
        } else if (versionSpec.startsWith("!=")) {
            relation = PyRequirementRelation.NE
        }
        if (relation != null) {
            val versionIndex = findFirstNotWhiteSpaceAfter(versionSpec, relation.presentableText.length)
            return pyRequirementVersionSpec(relation, versionSpec.substring(versionIndex))
        }
        return null
    }
    private fun findFirstNotWhiteSpaceAfter(line: String, beginIndex: Int): Int {
        /// Taken from PyRequirementParser, but that function is Private :-(
        for (i in beginIndex until line.length) {
            if (!StringUtil.isWhiteSpace(line[i])) {
                return i
            }
        }
        return line.length
    }
}