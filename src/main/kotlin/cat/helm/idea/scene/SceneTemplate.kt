package cat.helm.idea.scene

import cat.helm.idea.extensions.NameFormats
import cat.helm.idea.extensions.sceneNameFormat
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl
import org.jetbrains.kotlin.idea.core.getPackage
import java.util.*

/**
 * Created by Borja on 18/7/17.
 */

sealed class SceneTemplate(val sceneName: String) {

    abstract val name: String
    abstract val templateFileName: String


    fun createTemplate(project: Project, destinationDirectory: PsiDirectory): PsiElement {

        val templateName = FileTemplateManager.getInstance(project).getInternalTemplate(templateFileName)
        val templateProperties = FileTemplateManager.getInstance(project).defaultProperties
        return FileTemplateUtil.createFromTemplate(templateName, name, getProperties(templateProperties), destinationDirectory)

    }

    abstract fun getProperties(templateProperties: Properties): Properties?

    class Activity(sceneName: String) : SceneTemplate(sceneName) {

     //   var baseActivityImport = (destinationDirectory.parentDirectory as PsiJavaDirectoryImpl).getPackage()?.qualifiedName
      //  var rImport = (destinationDirectory.parentDirectory?.parentDirectory as PsiJavaDirectoryImpl).getPackage()?.qualifiedName
        override fun getProperties(templateProperties: Properties): Properties? {
            templateProperties.put("PRESENTER_NAME", Presenter(sceneName).name)
            templateProperties.put("VIEW_NAME", View(sceneName).name)
            //   templateProperties.put("BASE_ACTIVITY", baseActivityImport)
            templateProperties.put("LAYOUT_FILE_NAME", sceneName.sceneNameFormat(NameFormats.LAYOUT))
          //  templateProperties.put("PAKCAGE", rImport)

            return templateProperties
        }

        override val name: String = "${sceneName}Activity"
        override val templateFileName: String = "CleanActivity"

    }

    class Presenter(sceneName: String) : SceneTemplate(sceneName) {

        override fun getProperties(templateProperties: Properties): Properties? {
            templateProperties.put("VIEW_NAME", View(sceneName).name)
            return templateProperties
        }

        override val name: String = "${sceneName}Presenter"
        override val templateFileName: String = "CleanPresenter"
    }

    class View(sceneName: String) : SceneTemplate(sceneName) {
        override fun getProperties(templateProperties: Properties): Properties? = null

        override val name: String = "${sceneName}View"
        override val templateFileName: String = "CleanView"
    }

    class Layout(sceneName: String) : SceneTemplate(sceneName) {

        override fun getProperties(templateProperties: Properties): Properties? = null

        override val name: String = "activity_$sceneName"
        override val templateFileName: String = "Layout"
    }


    class ActivityModule(sceneName: String) : SceneTemplate(sceneName) {
        override val name = "${sceneName}ActivityModule"
        override val templateFileName = "ActivityModule"
        override fun getProperties(templateProperties: Properties): Properties? {
            templateProperties.put("SCENE_NAME", sceneName)
            return templateProperties
        }

    }
}