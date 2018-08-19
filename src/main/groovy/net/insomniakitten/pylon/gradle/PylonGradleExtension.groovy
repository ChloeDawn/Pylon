package net.insomniakitten.pylon.gradle

/**
 * Extension class for the Pylon Gradle plugin, providing configuration properties
 * @author InsomniaKitten
 * @since 0.1.0
 */
class PylonGradleExtension {
  public static final NAME = 'pylon'

  /**
   * Determines if Pylon should allow empty name elements in the Mod annotation
   */
  public allowEmptyName = true // NYI

  /**
   * Determines if Pylon should generate a marker comment in the JSON file
   */
  public generateMarker = true // NYI
}
