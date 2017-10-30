package cat.helm.idea.fileModifier

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportsFactory
import org.jetbrains.kotlin.resolve.ImportPath


class ImportUtils {
    companion object {
        @JvmStatic
        fun addImport(destinationFile: PsiFile, project: Project, fqName: FqName) {
            val importList = (destinationFile as KtFile).importList
            val imports = KtImportsFactory(project).createImportDirectives(listOf(ImportPath(fqName, true)))
            importList?.add(imports.first())
        }
    }
}