import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.PasswordCredentials

fun RepositoryHandler.docshuntGPR(action: (PasswordCredentials.() -> Unit)? = null) = maven {
    name = "GitHubPackages"
    setUrl("https://maven.pkg.github.com/Drag-dog/docshunt-backend-library")
    credentials {
        if (action == null) {
            username = "be@docshunt.ai"
            password = "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"
        } else {
            action()
        }
    }
}

