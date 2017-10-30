package cat.helm.idea.android.resource

import com.android.resources.ResourceFolderType
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.android.facet.AndroidFacet
import java.util.ArrayList

class ResourceUtils {

    companion object {

        @JvmStatic
        fun getResourceSubdirs(resourceType: ResourceFolderType, facet: AndroidFacet): List<VirtualFile> {
            val resourceDirs = facet.resourceFolderManager.folders

            val dirs = ArrayList<VirtualFile>()

            resourceDirs
                    .filter { it.isValid }
                    .forEach {
                        for (child in it.children) {
                            val type = ResourceFolderType.getFolderType(child.name)
                            if (resourceType == type) dirs.add(child)
                        }
                    }
            return dirs
        }
    }
}