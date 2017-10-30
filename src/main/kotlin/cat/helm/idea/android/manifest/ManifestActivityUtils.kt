package cat.helm.idea.android.manifest

import cat.helm.idea.extensions.guard
import com.intellij.openapi.vfs.ReadonlyStatusHandler
import org.jetbrains.android.dom.manifest.Manifest
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.util.AndroidUtils
import org.jetbrains.kotlin.psi.KtFile

class ManifestActivityUtils(facet: AndroidFacet) {

    private val manifest: Manifest

    init {
        val manifestFile = facet.mainIdeaSourceProvider.manifestFile
        if (manifestFile == null ||
                !ReadonlyStatusHandler.ensureFilesWritable(facet.module.project, manifestFile)) {
            throw IllegalStateException("Manifest not found or not writable")
        }

        val manifestDom = AndroidUtils.loadDomElement(facet.module, manifestFile, Manifest::class.java)

        manifest = manifestDom.guard {
            throw IllegalStateException("Manifest can't be parsed")
        }
    }


    fun activityAlreadyExists(activityFileName: String): Boolean {
        val application = manifest.application
        application.activities.forEach { activity ->
            activity.activityClass.value
            if (activity.activityClass.value?.name == activityFileName) {
                return true
            }
        }
        return false
    }

    fun addActivityEntryToManifest(activityFile: KtFile) {

        val application = manifest.application
        val activity = application.addActivity()
        activity.activityClass.value = activityFile.classes[0]
    }
}