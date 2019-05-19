# Pylon [![](https://jitpack.io/v/InsomniaKitten/Pylon.svg)](https://jitpack.io/#InsomniaKitten/Pylon)
##### Compile-time annotation processor for auto-generation of mod metadata

Pylon provides annotations for generating mod metadata resources dynamically from source.
To use this processor in your environment, append the following to your Gradle build script:

```groovy
repositories {
  maven { url = 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.InsomniaKitten:Pylon:0.3.2'
  annotationProcessor 'com.github.InsomniaKitten:Pylon:0.3.2'
}
```
