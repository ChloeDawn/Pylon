# Pylon [![](https://jitpack.io/v/InsomniaKitten/Pylon.svg)](https://jitpack.io/#InsomniaKitten/Pylon)
##### Annotation processor for auto-generation of Rift mod metadata

Pylon provides annotations for generating `riftmod.json` metadata dynamically from source.
To use this processor in your environment, append the following to your Gradle build script:

```groovy
repositories {
  maven { url = 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.InsomniaKitten:Pylon:0.2.0'
  annotationProcessor 'com.github.InsomniaKitten:Pylon:0.2.0'
}
```
