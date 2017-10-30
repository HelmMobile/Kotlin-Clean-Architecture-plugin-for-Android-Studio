package cat.helm.idea.scene

enum class SceneType {

    ACTIVITY {
        override fun getTemplate(sceneName: String): SceneTemplate = SceneTemplate.Activity(sceneName)


    },
    ACTIVITY_MODULE {
        override fun getTemplate(sceneName: String): SceneTemplate = SceneTemplate.ActivityModule(sceneName)

    },
    LAYOUT {
        override fun getTemplate(sceneName: String): SceneTemplate = SceneTemplate.Layout(sceneName)

    },
    PRESENTER {
        override fun getTemplate(sceneName: String): SceneTemplate = SceneTemplate.Presenter(sceneName)
    },
    VIEW {
        override fun getTemplate(sceneName: String): SceneTemplate = SceneTemplate.View(sceneName)
    };


    abstract fun getTemplate(sceneName: String): SceneTemplate


    fun isSourceFile(): Boolean  = this != LAYOUT

}
