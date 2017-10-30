package cat.helm.idea.extensions

inline fun <T> T?.guard(block: () -> Nothing): T {
    if (this == null || this == false) block(); return this
}
