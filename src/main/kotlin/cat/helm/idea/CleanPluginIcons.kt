package cat.helm.idea

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

interface CleanPluginIcons {

        val DEMO_ACTION: Icon
            get() = IconLoader.getIcon("/action.png")
}