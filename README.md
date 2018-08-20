# Pylon
##### Annotation processor for auto-generation of Rift mod metadata

Pylon provides annotations for generating `riftmod.json` metadata dynamically from source.
To use this processor in your environment, append the following to your Gradle build script:

```groovy
repositories {
  maven { url = 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.InsomniaKitten:Pylon:master-SNAPSHOT'
  annotationProcessor 'com.github.InsomniaKitten:Pylon:master-SNAPSHOT'
}
```
