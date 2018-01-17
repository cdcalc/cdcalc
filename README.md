# CDCalc
[![Build Status](https://travis-ci.org/cdcalc/cdcalc.svg?branch=master)](https://travis-ci.org/cdcalc/cdcalc)
[![codecov](https://codecov.io/gh/cdcalc/cdcalc/branch/master/graph/badge.svg)](https://codecov.io/gh/cdcalc/cdcalc)
[![Download](https://api.bintray.com/packages/cdcalc/cdcalc/cdcalc/images/download.svg)](https://bintray.com/cdcalc/cdcalc/cdcalc/_latestVersion)

The goal is to provide a simple way to automatically calculate a Semantic Version for projects that are delivered frequently enough to not be able to handle versioning manually but not frequent enough to be able to utilize Continous Deployment.

## How to use CDCalc?
It's possible to use it via the gradle plugin or the cli, see section below!

### Gradle plugin
The easiest way to use it, is the gradle plugin https://plugins.gradle.org/plugin/com.github.cdcalc see the `sample` folder for more information.

### CLI
The CLI is experimental but can be used for debugging at the moment.

```
./gradlew installDist
./cli/build/install/cli/bin/cli
```

The `cli` or `cli.bat` to can be executed from any folder containing a `.git` folder

## Supported branching strategies

### GitFlow when doing a couple of releases per week
From time to time it's not possible to deploy to production any given time and to support a flow where a release branch can be stabilized and tested in a verification environment at the same time as a hotfix can be developed with blocking the ordinary development flow this might be an option.

### Default resolvers
```
master tag[v2.1.3] => 2.1.3
develop 2 commits ahead of latest reachable rc.0 tag tag[v1.2.3-rc.0] => 1.3.0-beta.2
release/2.0.0 => 2.0.0-rc.{numberOfFirstParentCommitsSinceDevelop} (this can be used for auto tagging)
hotfix/1.1.0 => 1.1.0-rc.{numberOfFirstParentCommitsSinceMaster} (this can be used for auto tagging)
merge-requests/137 => same base version resolver and bumping rules as develop => 1.1.0-alpha.137
undefined branch => throws a not supported exception
```

## History
CDCalc was started a way to learn kotlin and also is heavliy inspired by the simplicity of [GitVersion](https://github.com/GitTools/GitVersion) and if you have the possiblity you should use GitVersion instead.

CDCalc is also taking a much easier route to handle versioning of GitFlow since it requires a pre-release tag at the development branch together with the creation of a release branch. Given that tradeoff the logic to calculate the version is much easier.
