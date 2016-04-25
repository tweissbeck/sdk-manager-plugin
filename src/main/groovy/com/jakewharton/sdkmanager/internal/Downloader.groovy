package com.jakewharton.sdkmanager.internal

interface Downloader {
  void download(File dest, String SDK_VERSION)

  static final class Real implements Downloader {
    @Override void download(File dest, String SDK_VERSION) {
      SdkDownload.get().download(dest, SDK_VERSION)
    }
  }
}
