package cat.helm.idea.extensions

import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.idea.caches.resolve.getNullableModuleInfo
import org.jetbrains.kotlin.idea.compiler.IDELanguageSettingsProvider
import org.jetbrains.kotlin.idea.project.TargetPlatformDetector
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.ImportPath

/**
 * Created by Borja on 18/7/17.
 */

public fun KtFile.getDefaultImports(): List<ImportPath> {
    val moduleInfo = getNullableModuleInfo() ?: return emptyList()
    val versionSettings = IDELanguageSettingsProvider.getLanguageVersionSettings(moduleInfo, project)
    return TargetPlatformDetector.getPlatform(this).getDefaultImports(
            versionSettings.supportsFeature(LanguageFeature.DefaultImportOfPackageKotlinComparisons)
    )
}