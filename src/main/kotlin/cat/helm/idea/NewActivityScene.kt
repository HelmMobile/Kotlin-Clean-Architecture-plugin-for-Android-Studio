import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.ide.fileTemplates.actions.CreateFromTemplateActionBase
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbModePermission
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.ui.Messages
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiDirectory

/**
 * Created by Borja on 17/7/17.
 */
class NewActivityScene : AnAction() {

    override fun actionPerformed(actionEvent: AnActionEvent) {
        val sceneName = Messages.showInputDialog("Enter new scene name", "New Scene", null)
        sceneName?.let {
            DumbService.allowStartingDumbModeInside(DumbModePermission.MAY_START_BACKGROUND) {
                val project = actionEvent.project!!
                val fileTemplate = FileTemplateManager.getInstance(project).getInternalTemplate("CleanActivity")
                val file = actionEvent.getData(LangDataKeys.PSI_ELEMENT)
                val props = FileTemplateManager.getInstance(project).defaultProperties
                props.put("PRESENTER_NAME", "${sceneName}Presenter")
                val activity = FileTemplateUtil.createFromTemplate(fileTemplate, sceneName, props, file as PsiDirectory)

                CreateFromTemplateActionBase.startLiveTemplate(activity.containingFile)
                createSceneFiles(sceneName)
            }
        }
    }

    private fun createSceneFiles(sceneName: String): Unit {
    }
}