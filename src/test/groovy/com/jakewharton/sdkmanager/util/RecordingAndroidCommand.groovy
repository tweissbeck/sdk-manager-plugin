package com.jakewharton.sdkmanager.util
import com.jakewharton.sdkmanager.internal.AndroidCommand

final class RecordingAndroidCommand extends ArrayList<String> implements AndroidCommand {
  int nextReturnCode = 0
  def sdk

  RecordingAndroidCommand(File sdk) {
    this.sdk = sdk
  }

  @Override int update(String filter) {
    add("update $filter" as String)
    return nextReturnCode
  }

  @Override String listAll(String filter) {
    add("list -a -e" as String)
    return  "id: 55 or \"sys-img-armeabi-v7a-android-19\"\n" +
            "     Type: SystemImage\n" +
            "     Desc: Android SDK Platform 4.4.2\n" +
            "           Revision 2\n" +
            "           Requires SDK Platform Android API 19\n"
  }

  @Override String listUpdates(String filter) {
    add("list -e" as String)
    return  "id: 1 or \"tools\"\n" +
            "     Type: Tool\n" +
            "     Desc: Android SDK Tools, revision 24.4.1\n"
  }
}
