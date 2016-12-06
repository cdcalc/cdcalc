# Gradle plugin for cdcalc
Gradle is not need to run cdcalc but it handles dependency management 
makes it possible to calculate the version for usages with `maven-publish` 

## Running
See the `plugin-sample` for information but this is the basic steps.
- A build time dependency must be added. `classpath 'com.github.orjan:plugin:0.0.1-SNAPSHOT'`
- Apply the plugin `apply plugin: 'cdcalc'`
- `./gradlew calculateVersion`

## Configuration
It's possible to specify the location of the repository
```
cdcalc {
    repository = '../.git'
}
```

When `calculateVersion` has been executed the calculated version can be 
accessed like this.
```
task publish(dependsOn: 'calculateVersion', description: 'Publish artifacts to Nexus', group: 'Publishing') {
    doLast {
        println("Version from calculateVersion $cdcalc.calculatedVersion")
    }
}
```
