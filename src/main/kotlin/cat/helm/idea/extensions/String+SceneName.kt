package cat.helm.idea.extensions

import com.intellij.codeInsight.CodeInsightSettings
import com.intellij.codeInsight.daemon.impl.DaemonListeners
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.caches.resolve.resolveImportReference
import org.jetbrains.kotlin.idea.core.targetDescriptors
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode


enum class NameFormats {
    FOLDER,
    FILE,
    LAYOUT
}

fun String.sceneNameFormat(format: NameFormats): String = when (format) {
    NameFormats.FOLDER,  NameFormats.LAYOUT -> this.substring(0, 1).toLowerCase() + this.substring(1)
    NameFormats.FILE -> this.substring(0, 1).toUpperCase() + this.substring(1)
}


