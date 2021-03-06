
import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import com.moowork.gradle.node.yarn.YarnTask
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import nebula.plugin.bintray.BintrayExtension
import nebula.plugin.bintray.BintrayPlugin
import nebula.plugin.responsible.NebulaResponsiblePlugin
import org.gradle.process.internal.ExecAction
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    `build-scan` version Versions.com_gradle_build_scan_gradle_plugin
    buildSrcVersions
    kotlin("jvm") version Versions.org_jetbrains_kotlin_jvm_gradle_plugin apply false
    id("com.diffplug.gradle.spotless") version Versions.com_diffplug_gradle_spotless_gradle_plugin apply false
    id("io.gitlab.arturbosch.detekt") version Versions.io_gitlab_arturbosch_detekt_gradle_plugin apply false
    id("org.jetbrains.dokka") version Versions.org_jetbrains_dokka_gradle_plugin
    id("nebula.maven-publish") version Versions.nebula_maven_publish_gradle_plugin apply false
    id("nebula.nebula-bintray") version Versions.nebula_nebula_bintray_gradle_plugin apply false
    id("nebula.project") version Versions.nebula_project_gradle_plugin apply false
    id("nebula.release") version Versions.nebula_release_gradle_plugin
    id("com.moowork.node") version Versions.com_moowork_node_gradle_plugin
}

subprojects {
    this@subprojects.version = rootProject.version
    pluginManager.withPlugin("java") {
        this@subprojects.pluginManager.apply(NebulaResponsiblePlugin::class.java)
        this@subprojects.pluginManager.apply(BintrayPlugin::class.java)
        this@subprojects.pluginManager.apply(SpotlessPlugin::class.java)

        this@subprojects.configure<BintrayExtension> {
            pkgName.value("MythicDrops")
            repo.value("mythicdrops")
            userOrg.value("pixeloutlaw")
            syncToMavenCentral.value(false)
        }
        this@subprojects.configure<SpotlessExtension> {
            java {
                target("src/**/*.java")
                googleJavaFormat()
                trimTrailingWhitespace()
                endWithNewline()
            }
            format("mythicDropsJava") {
                target("src/*/java/com/tealcube/**/*.java")
                if (this@subprojects.file("HEADER").exists()) {
                    licenseHeaderFile("HEADER", "package ")
                }
            }
        }

        this@subprojects.tasks.withType<JavaCompile> {
            dependsOn("spotlessJavaApply", "spotlessMythicDropsJavaApply")
            options.compilerArgs.add("-parameters")
            options.isFork = true
            options.forkOptions.executable = "javac"
        }
        this@subprojects.tasks.withType<Test> {
            useJUnitPlatform()
        }

        this@subprojects.dependencies {
            "testImplementation"(Libs.spigot_api)
            "testImplementation"(Libs.mockito_core)
            "testImplementation"(Libs.truth)
            "testImplementation"(platform(Libs.junit_bom))
            "testImplementation"("org.junit.jupiter:junit-jupiter")
            "testImplementation"(Libs.junit)
            "testRuntimeOnly"("org.junit.vintage:junit-vintage-engine") {
                because("allows JUnit 3 and JUnit 4 tests to run")
            }
            "testRuntimeOnly"("org.junit.platform:junit-platform-launcher") {
                because("allows tests to run from IDEs that bundle older version of launcher")
            }
        }
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        this@subprojects.pluginManager.apply(DetektPlugin::class.java)
        this@subprojects.pluginManager.apply(DokkaPlugin::class.java)
        this@subprojects.pluginManager.apply(SpotlessPlugin::class.java)
        this@subprojects.configure<DetektExtension> {
            baseline = this@subprojects.file("baseline.xml")
        }
        this@subprojects.configure<SpotlessExtension> {
            kotlin {
                target("src/**/*.kt")
                ktlint()
                trimTrailingWhitespace()
                endWithNewline()
                if (this@subprojects.file("HEADER").exists()) {
                    licenseHeaderFile("HEADER")
                }
            }
        }
        this@subprojects.tasks.withType<KotlinCompile> {
            dependsOn("spotlessKotlinApply")
            kotlinOptions {
                javaParameters = true
                jvmTarget = "1.8"
            }
        }
        this@subprojects.tasks.getByName<DokkaTask>("dokka") {
            outputFormat = "html"
            configuration {
                jdkVersion = 8
            }
        }
        val dokkaJavadoc = this@subprojects.tasks.create("dokkaJavadoc", DokkaTask::class.java) {
            outputDirectory = "${project.buildDir}/javadoc"
            outputFormat = "javadoc"
            configuration {
                jdkVersion = 8
            }
        }
        this@subprojects.tasks.getByName<Jar>("javadocJar") {
            dependsOn(dokkaJavadoc)
        }

        this@subprojects.dependencies {
            "testImplementation"(Libs.kotlin_reflect)
            "testImplementation"(Libs.mockk)
        }
    }
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"

    publishAlways()
}

node {
    nodeModulesDir = rootProject.file("/website")
}

tasks.withType<Wrapper> {
    gradleVersion = Versions.gradleLatestVersion
    distributionType = Wrapper.DistributionType.ALL
}

tasks.withType<YarnTask> {
    setExecOverrides(closureOf<ExecAction> {
        workingDir = rootProject.file("/website")
    })
}

tasks.register("docusaurusVersion", YarnTask::class.java) {
    dependsOn("yarn_install")
    onlyIf { rootProject.file("/website/build/versions.json").exists() }
    this.setArgs(listOf("version", project.version.toString()))
}

tasks.register("docusaurusBuild", YarnTask::class.java) {
    doFirst {
        delete(rootProject.file("/website/build"))
    }
    dependsOn("docusaurusVersion")
    this.setArgs(listOf("build"))
}

tasks.findByName("release")?.finalizedBy("build", "docusaurusBuild")