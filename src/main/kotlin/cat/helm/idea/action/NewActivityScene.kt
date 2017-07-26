import cat.helm.idea.template.Template
import com.android.ide.common.rendering.LayoutLibrary
import com.android.ide.common.resources.configuration.FolderConfiguration
import com.android.resources.ResourceFolderType
import com.android.resources.ResourceType
import com.android.sdklib.IAndroidTarget
import com.android.tools.idea.editors.strings.StringsWriteUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.ReadonlyStatusHandler
import com.intellij.psi.*
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.dom.manifest.*
import org.jetbrains.android.formatter.AndroidXmlCodeStyleSettings
import org.jetbrains.android.uipreview.LayoutLibraryLoader
import org.jetbrains.android.util.AndroidResourceUtil
import org.jetbrains.android.util.AndroidUtils
import org.jetbrains.jps.android.builder.AndroidResourceCachingBuildTarget
import org.jetbrains.jps.incremental.artifacts.builders.LayoutElementBuilderService
import org.jetbrains.kotlin.idea.core.getPackage
import org.jetbrains.kotlin.idea.decompiler.navigation.SourceNavigationHelper
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile


/**
 * Created by Borja on 17/7/17.
 */
class NewActivityScene : AnAction() {

    override fun actionPerformed(actionEvent: AnActionEvent) {
        val sceneName = Messages.showInputDialog("Enter new scene name", "New Scene", null)
        sceneName?.let {

            val project = actionEvent.project
            project?.let {

                val correctPathName = sceneName.substring(0,1).toLowerCase()+sceneName.substring(1)


                WriteCommandAction.runWriteCommandAction(project) {

                    val destinationDirectory = actionEvent.getData(LangDataKeys.PSI_ELEMENT) as PsiDirectory
                    val directory = createDirectory(destinationDirectory, correctPathName)

                    if(directory!=null){

                        val correctFileName = sceneName.substring(0,1).toUpperCase()+sceneName.substring(1)

                        val activityFile = createSceneFiles(correctFileName, project, directory)
                        addActivityEntryToManifest(actionEvent, project, activityFile)
                    } else {

                        Messages.showMessageDialog("Ya existe una carpeta con el mismo nombre.","Information",Messages.getErrorIcon())
                    }


                }

            }
        }
    }

    private fun addActivityEntryToManifest(actionEvent: AnActionEvent, project: Project?, activityFile: PsiElement) {
        //Todo Show Android facet error
        val facet = AndroidFacet.getInstance(actionEvent.getData(LangDataKeys.PSI_ELEMENT)!!)

        val manifest = facet!!.mainIdeaSourceProvider.manifestFile
        if (manifest == null ||
                !ReadonlyStatusHandler.ensureFilesWritable(facet.module.project, manifest)) {
            throw Exception()
        }
        val manifestDoom = AndroidUtils.loadDomElement(facet.module, manifest, Manifest::class.java)
        val application = manifestDoom?.application
        WriteCommandAction.runWriteCommandAction(project) {
            val activity = application!!.addActivity()
            activity.activityClass.value = SourceNavigationHelper.getOriginalPsiClassOrCreateLightClass((activityFile as KtFile).declarations[0] as KtClass)
        }
    }

    private fun createSceneFiles(sceneName: String, project: Project, destinationDirectory: PsiDirectory): PsiElement {


        val correctLayoutName = sceneName.substring(0,1).toLowerCase()+sceneName.substring(1)
        val destinationDirectoryLayout = JavaPsiFacade.getInstance(project).findPackage("layout")?.getDirectories(GlobalSearchScope.projectScope(project))?.get(0)
        val layoutTemplate = Template.Layout(correctLayoutName)
        layoutTemplate.createTemplate(project,destinationDirectoryLayout!!)

        val activityTemplate = Template.Activity(sceneName,destinationDirectory)
        val activityFile = activityTemplate.createTemplate(project, destinationDirectory)

        val presenterTemplate = Template.Presenter(sceneName)
        presenterTemplate.createTemplate(project, destinationDirectory)

        val viewTemplate = Template.View(sceneName)
        viewTemplate.createTemplate(project, destinationDirectory)
        return activityFile
    }


    private fun createDirectory(parent: PsiDirectory, name: String): PsiDirectory? {

        val subdirectories = parent.subdirectories.map { it.name }
        return if (!subdirectories.contains(name)) {

            parent.createSubdirectory(name)
        } else {

            null
        }

    }
}