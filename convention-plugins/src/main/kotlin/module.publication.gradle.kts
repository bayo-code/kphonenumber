import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.`maven-publish`

plugins {
    `maven-publish`
    signing
}

publishing {
    // Configure all publications
    publications.withType<MavenPublication> {
        // Stub javadoc.jar artifact
        artifact(tasks.register("${name}JavadocJar", Jar::class) {
            archiveClassifier.set("javadoc")
            archiveAppendix.set(this@withType.name)
        })

        // Provide artifacts information required by Maven Central
        pom {
            name.set("KPhoneNumber")
            description.set("Phone number parser for Kotlin Multiplatform")
            url.set("https://github.com/bayo-code/kphonenumber")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    id.set("bayo-code")
                    name.set("Adebayo Jagunmolu")
                    organization.set("Brain Crunch Labs")
                    organizationUrl.set("https://git.braincrunchlabs.tech")
                }
            }
            scm {
                url.set("https://git.braincrunchlabs.tech/bayo-code/kphonenumber")
            }
        }
    }
}

signing {
    if (project.hasProperty("signing.gnupg.keyName")) {
        useGpgCmd()
        sign(publishing.publications)
    }
}
