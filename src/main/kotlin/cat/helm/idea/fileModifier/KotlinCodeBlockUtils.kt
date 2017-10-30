package cat.helm.idea.fileModifier

import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementVisitor
import org.jetbrains.kotlin.psi.KtBlockCodeFragment
import org.jetbrains.kotlin.psi.KtClassBody
import java.util.*

class KotlinCodeBlockUtils {

    companion object {
        @JvmStatic
        fun insertCodeBlock(project: Project, templateName: String, activityInjector: PsiFile, properties: Properties) {
            val internalTemplate = FileTemplateManager.getInstance(project).getInternalTemplate(templateName)


            val ktBlockCodeFragment = KtBlockCodeFragment(project, templateName, internalTemplate.getText(properties), null, null)
            PsiDocumentManager.getInstance(project).commitDocument(PsiDocumentManager.getInstance(project).getDocument(activityInjector)!!)

            val e = object : PsiRecursiveElementVisitor() {
                override fun visitElement(element: PsiElement?) {
                    if (element is KtClassBody) {
                        element.addRangeBefore(ktBlockCodeFragment.firstChild, ktBlockCodeFragment.lastChild, element.rBrace)
                        PsiDocumentManager.getInstance(project).commitDocument(PsiDocumentManager.getInstance(project).getDocument(activityInjector)!!)
                    }
                    super.visitElement(element)
                }
            }
            e.visitFile(activityInjector)
        }
    }
}