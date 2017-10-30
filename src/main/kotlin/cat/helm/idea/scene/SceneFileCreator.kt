package cat.helm.idea.scene

import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.refactoring.toPsiDirectory


class SceneFileCreator(private val project: Project) {


    fun createSceneFiles(sceneName: String, destinationDirectory: PsiDirectory) {

        SceneType.values().forEach { sceneType ->
            if (sceneType.isSourceFile()) {
                val template = sceneType.getTemplate(sceneName)
                createTemplateFile(template, destinationDirectory)
            }
        }
    }

    fun createLayoutFiles(sceneName: String, destinationDirectories: List<VirtualFile>) {
        val template = SceneType.LAYOUT.getTemplate(sceneName)
        destinationDirectories.forEach { directory ->
            createTemplateFile(template, directory.toPsiDirectory(project)!!)
        }
    }

    private fun createTemplateFile(template: SceneTemplate, destinationDirectory: PsiDirectory) {
        val fileTemplate = FileTemplateManager.getInstance(project).getInternalTemplate(template.templateFileName)
        val templateProperties = FileTemplateManager.getInstance(project).defaultProperties
        FileTemplateUtil.createFromTemplate(fileTemplate, template.name, template.getProperties(templateProperties), destinationDirectory)
    }

}

