package com.novoda.gradle.test

import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSet

class SourceSetConfigurator {

    private static final String TEST_DIR = AndroidTestPluginExtension.TEST_DIR

    private final Project project

    SourceSetConfigurator(Project project) {
        this.project = project
    }

    public void configureCompileTestTask(String language, SourceSet variationSources, TestInfo testTasksInfo, VariationInfo variationInfo) {

        SourceDirectorySet languageVariationSource = variationSources.getProperty(language)
        languageVariationSource.setSrcDirs testSrcDir(variationInfo, language)

        // Create a task which compiles the test sources.
        def testCompileTask = project.tasks.getByName variationSources.getCompileTaskName(language)
        // Depend on the project compilation (which itself depends on the manifest processing task).
        testCompileTask.dependsOn testTasksInfo.androidCompile
        testCompileTask.group = null
        testCompileTask.description = null
        testCompileTask.classpath = testTasksInfo.testCompileClasspath
        testCompileTask.source = languageVariationSource
        testCompileTask.destinationDir = testTasksInfo.testDestinationDir
    }

    private ArrayList testSrcDir(VariationInfo info, String language) {
        def testSrcDirs = []
        testSrcDirs.add(file("src/$TEST_DIR/$language"))
        testSrcDirs.add(file("src/$TEST_DIR$info.buildTypeName/$language"))
        testSrcDirs.add(file("src/$TEST_DIR$info.projectFlavorName/$language"))
        info.projectFlavorNames.each { flavor ->
            testSrcDirs.add file("src/$TEST_DIR$flavor/$language")
        }
        testSrcDirs
    }

    private File file(Object path) {
        project.file(path)
    }
}
