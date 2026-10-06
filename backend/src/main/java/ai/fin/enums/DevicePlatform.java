package ai.fin.enums;

import ai.fin.entities.DeviceToken;

/**
 * Represents the device platform used to identify the target for push notifications.
 * Currently, the application only supports the web platform, and other platforms
 * can be enabled as needed.
 *
 * @see DeviceToken
 */
public enum DevicePlatform {

    WEB,

    /*
     * The following platforms are intentionally disabled for now.
     * Uncomment them when support is added.
     */

    // ANDROID,
    // IOS

}
