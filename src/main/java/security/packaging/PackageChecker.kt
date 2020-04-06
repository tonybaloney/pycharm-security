package security.packaging

import com.intellij.openapi.util.text.StringUtil
import com.jetbrains.python.packaging.PyPackage
import com.jetbrains.python.packaging.pyRequirementVersionSpec
import com.jetbrains.python.packaging.requirement.PyRequirementRelation
import com.jetbrains.python.packaging.requirement.PyRequirementVersionSpec
import java.util.stream.Collectors
import java.util.stream.StreamSupport

interface PackageIssue {
    fun getMessage(): String
}

interface PackageChecker {
    fun hasMatch(pythonPackage: PyPackage): Boolean
    fun getMatches (pythonPackage: PyPackage): List<PackageIssue>
}

abstract class BasePackageChecker: PackageChecker {
    private val tripleRequirementMap: HashMap<String, PyRequirementRelation> = hashMapOf(
            "===" to PyRequirementRelation.STR_EQ)
    private val doubleRequirementMap: HashMap<String, PyRequirementRelation> = hashMapOf(
            "==" to PyRequirementRelation.EQ,
            "<=" to PyRequirementRelation.LTE,
            ">=" to  PyRequirementRelation.GTE,
            "~=" to PyRequirementRelation.COMPATIBLE,
            "!=" to PyRequirementRelation.NE)
    private val singleRequirementMap: HashMap<String, PyRequirementRelation> = hashMapOf(
            "<" to PyRequirementRelation.LT,
            ">" to PyRequirementRelation.GT)

    fun parseVersionSpecs(versionSpecs: String): List<PyRequirementVersionSpec?>? {
        return StreamSupport
                .stream(StringUtil.tokenize(versionSpecs, ",").spliterator(), false)
                .map { obj: String -> obj.trim { it <= ' ' } }
                .map { versionSpec: String? -> parseVersionSpec(versionSpec!!) }
                .collect(Collectors.toList())
    }

    private fun parseVersionSpec(versionSpec: String): PyRequirementVersionSpec? {
        val relation: PyRequirementRelation?
        if (versionSpec.length >= 3 && tripleRequirementMap.containsKey(versionSpec.substring(0, 3)))
            relation = tripleRequirementMap[versionSpec.substring(0, 3)]
        else if (versionSpec.length >= 2 && doubleRequirementMap.containsKey(versionSpec.substring(0, 2)))
            relation = doubleRequirementMap[versionSpec.substring(0, 2)]
        else if (versionSpec.length >= 1 && singleRequirementMap.containsKey(versionSpec.substring(0, 1)))
            relation = singleRequirementMap[versionSpec.substring(0, 1)]
        else
            return null

        val versionIndex = findFirstNotWhiteSpaceAfter(versionSpec, relation!!.presentableText.length)
        return pyRequirementVersionSpec(relation, versionSpec.substring(versionIndex))
    }

    fun findFirstNotWhiteSpaceAfter(line: String, beginIndex: Int): Int {
        /// Taken from PyRequirementParser, but that function is Private :-(
        for (i in beginIndex until line.length) {
            if (!StringUtil.isWhiteSpace(line[i])) {
                return i
            }
        }
        return line.length
    }
}