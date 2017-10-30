package cat.helm.idea.action

import cat.helm.idea.android.manifest.ManifestActivityUtils
import cat.helm.idea.android.resource.ResourceUtils
import cat.helm.idea.extensions.NameFormats
import cat.helm.idea.extensions.guard
import cat.helm.idea.extensions.sceneNameFormat
import cat.helm.idea.fileModifier.ImportUtils
import cat.helm.idea.fileModifier.KotlinCodeBlockUtils
import cat.helm.idea.scene.SceneFileCreator
import cat.helm.idea.scene.SceneType
import cat.helm.idea.template.CodeBlockTemplate
import com.android.resources.ResourceFolderType
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.util.AndroidUtils
import org.jetbrains.kotlin.psi.KtFile
import javax.swing.JPanel

/**
 * Created by Borja on 17/7/17.
 */
class NewActivityScene : AnAction() {

    companion object {
        const private val ACTIVITY_INJECTOR_FILE_NAME = "ActivityInjector.kt"
        const private val ACTIVITY_INJECTOR_BLOCK_PROPERTY_NAME = "NAME"
    }

    lateinit var panel: JPanel


    override fun actionPerformed(actionEvent: AnActionEvent) {
        try {
            val dialogValues = Messages.
                    showInputDialogWithCheckBox("Enter new scene name",
                            "New Scene",
                            "Insert layout file ?",
                            true,
                            true,
                            null,
                            null,
                            null)

            val sceneName: String = dialogValues.first
            val needLayoutFile = dialogValues.second
            val project = actionEvent.project
            project?.let {
                WriteCommandAction.runWriteCommandAction(project) {
                    createScene(sceneName, actionEvent, project, needLayoutFile)
                }
            }
        } catch (e: Throwable) {
            Messages.showErrorDialog(e.message, "ERROR")
        }
    }

    private fun createScene(sceneName: String, actionEvent: AnActionEvent, project: Project, needLayoutFile: Boolean) {

        val facet: AndroidFacet = getAndroidFacet(project)
        val fileName = sceneName.sceneNameFormat(NameFormats.FILE)
        val layoutName = sceneName.sceneNameFormat(NameFormats.LAYOUT)

        val manifestActivityUtils = ManifestActivityUtils(facet)

        (!manifestActivityUtils.activityAlreadyExists(fileName)).guard {
            throw IllegalStateException("Activity already exists")
        }

        val directoryName = sceneName.sceneNameFormat(NameFormats.FOLDER)
        val destinationPath = actionEvent.getData(LangDataKeys.PSI_ELEMENT) as PsiDirectory
        val sceneDirectory = destinationPath.createSubdirectory(directoryName)

        val sceneFileCreator = SceneFileCreator(project)
        sceneFileCreator.createSceneFiles(fileName, sceneDirectory)

        if(needLayoutFile) {
            val layoutDirectories = ResourceUtils.getResourceSubdirs(ResourceFolderType.LAYOUT, facet)
            sceneFileCreator.createLayoutFiles(layoutName, layoutDirectories)
        }

        val activityFile = sceneDirectory.files.find { file ->
            file.name.contains(SceneType.ACTIVITY.getTemplate(fileName).name)
        }
        manifestActivityUtils.addActivityEntryToManifest(activityFile as KtFile)

        val activityInjector = FilenameIndex.getFilesByName(project, ACTIVITY_INJECTOR_FILE_NAME, GlobalSearchScope.projectScope(project)).first()


        val defaultProperties = FileTemplateManager.getInstance(project).defaultProperties
        defaultProperties.put(ACTIVITY_INJECTOR_BLOCK_PROPERTY_NAME, fileName)

        ImportUtils.addImport(activityInjector, project, activityFile.packageFqName)

        KotlinCodeBlockUtils.insertCodeBlock(project, CodeBlockTemplate.ACTIVITY_INJECTOR.fileName, activityInjector, defaultProperties)

        FileEditorManager.getInstance(project).openFile(activityFile.virtualFile, true)

    }

    private fun getAndroidFacet(project: Project): AndroidFacet {
        val applicationFacets = AndroidUtils.getApplicationFacets(project)
        if (applicationFacets.isEmpty()) {
            throw IllegalStateException("Android Module has not found")
        }
        return applicationFacets[0]

    }
}
