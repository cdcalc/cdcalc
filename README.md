# CDCalc
CDCalc was started a way to learn kotlin and also is heavliy inspired by the simplicity of [GitVersion](https://github.com/GitTools/GitVersion) and if you have the possiblity you should use GitVersion instead.

CDCalc is also taking a much easier route to handle versioning of GitFlow since it requires a pre-release tag at the development branch together with the creation of a release branch. Given that tradeoff the logic to calculate the version is much easier.


## Goal
The goal is to provide a simple way to automatically calculate a Semantic Version for projects that are delivered frequently enough to not be able to handle versioning manually but not frequent enough to be able to utilize Continous Deployment.

## GitFlow when doing a couple of releases per week
This is an anti pattern but from time to time it's not possible to deploy to production any given time and to support a flow where a release branch can be stabilized and tested in a verification environment at the same time as a hotfix can be developed with blocking the ordinary development flow this might be an option.
