package cat.helm.idea.template

import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.android.AndroidFileTemplateProvider
import java.util.*

/**
 * Created by Borja on 18/7/17.
 */

sealed class Template(val sceneName: String) {

    abstract val name: String
    abstract val templateFileName: String

    fun createTemplate(project: Project, destinationDirectory: PsiDirectory): PsiElement {
        val templateName = FileTemplateManager.getInstance(project).getInternalTemplate(templateFileName)
        val templateProperties = FileTemplateManager.getInstance(project).defaultProperties
        return FileTemplateUtil.createFromTemplate(templateName, name, getProperties(templateProperties), destinationDirectory)

    }

    abstract fun getProperties(templateProperties: Properties): Properties?

    class Activity(sceneName: String) : Template(sceneName) {

        override fun getProperties(templateProperties: Properties): Properties? {
            templateProperties.put("PRESENTER_NAME", Presenter(sceneName).name)
            templateProperties.put("VIEW_NAME", View(sceneName).name)
            return templateProperties
        }

        override val name: String = "${sceneName}Activity"
        override val templateFileName: String = "CleanActivity"
    }

    class Presenter(sceneName: String) : Template(sceneName) {

        override fun getProperties(templateProperties: Properties): Properties? {
            templateProperties.put("VIEW_NAME", View(sceneName).name)
            return templateProperties
        }

        override val name: String = "${sceneName}Presenter"
        override val templateFileName: String = "CleanPresenter"
    }

    class View(sceneName: String) : Template(sceneName) {
        override fun getProperties(templateProperties: Properties): Properties? {
            return null
        }

        override val name: String = "${sceneName}View"
        override val templateFileName: String = "CleanView"
    }
}