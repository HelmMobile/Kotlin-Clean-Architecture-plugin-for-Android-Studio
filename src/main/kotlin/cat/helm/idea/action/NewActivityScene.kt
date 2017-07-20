import cat.helm.idea.template.Template
import com.android.ide.common.xml.ManifestData
import com.android.tools.idea.model.ManifestInfo
import com.android.tools.idea.navigator.nodes.AndroidManifestFileNode
import com.android.tools.idea.navigator.nodes.AndroidManifestsGroupNode
import com.android.tools.idea.run.activity.AndroidActivityLauncher
import com.android.xml.AndroidManifest
import com.android.xml.AndroidXPathFactory
import com.intellij.codeInsight.FileModificationService
import com.intellij.ide.actions.ElementCreator
import com.intellij.ide.util.EditorHelper
import com.intellij.lang.Language
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.lang.StdLanguages
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.compiler.make.ManifestBuilder
import com.intellij.openapi.project.DumbModePermission
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.ReadonlyStatusHandler
import com.intellij.psi.*
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.idea.inspections.findExistingEditor
import com.intellij.psi.search.PsiShortNamesCache
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.JavaPsiFacadeImpl
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlToken
import com.intellij.util.xml.DomElement
import com.intellij.util.xml.DomManager
import com.intellij.util.xml.PsiClassConverter
import javafx.application.Application
import org.jetbrains.android.AndroidFileTemplateProvider
import org.jetbrains.android.augment.AndroidPsiElementFinder
import org.jetbrains.android.dom.AndroidAttributeValue
import org.jetbrains.android.dom.AndroidDomElement
import org.jetbrains.android.dom.AndroidDomElementDescriptorProvider
import org.jetbrains.android.dom.manifest.*
import org.jetbrains.android.util.AndroidBundle
import org.jetbrains.android.util.AndroidUtils
import org.jetbrains.kotlin.idea.decompiler.navigation.SourceNavigationHelper
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.lang.manifest.psi.ManifestElementType
import org.jetbrains.lang.manifest.psi.ManifestTokenType


/**
 * Created by Borja on 17/7/17.
 */
class NewActivityScene : AnAction() {

    override fun actionPerformed(actionEvent: AnActionEvent) {
        val sceneName = Messages.showInputDialog("Enter new scene name", "New Scene", null)
        sceneName?.let {
            val project = actionEvent.project
            project?.let {
                val destinationDirectory = actionEvent.getData(LangDataKeys.PSI_ELEMENT) as PsiDirectory
                val activityFile = createSceneFiles(sceneName, project, destinationDirectory)
                addActivityEntryToManifest(actionEvent, project, activityFile)
            }
        }
    }

    private fun addActivityEntryToManifest(actionEvent: AnActionEvent, project: Project?, activityFile: PsiElement) {
        //Todo Show Android facet error
        val facet = AndroidFacet.getInstance(actionEvent.getData(LangDataKeys.PSI_ELEMENT)!!)
        val manifest = facet!!.mainIdeaSourceProvider.manifestFile
        if (manifest == null ||
                !ReadonlyStatusHandler.ensureFilesWritable(facet.getModule().getProject(), manifest)) {
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
        val activityTemplate = Template.Activity(sceneName)
        val activityFile = activityTemplate.createTemplate(project, destinationDirectory)

        val presenterTemplate = Template.Presenter(sceneName)
        presenterTemplate.createTemplate(project, destinationDirectory)

        val viewTemplate = Template.View(sceneName)
        viewTemplate.createTemplate(project, destinationDirectory)
        return activityFile
    }
}