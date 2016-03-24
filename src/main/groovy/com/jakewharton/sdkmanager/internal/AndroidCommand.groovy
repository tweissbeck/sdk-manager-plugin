package com.jakewharton.sdkmanager.internal

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

import static com.android.SdkConstants.FD_TOOLS
import static com.android.SdkConstants.androidCmdName

interface AndroidCommand {
  int update(String filter);
  String listAll(String filter);
  String listUpdates(String filter);

  static final class Real implements AndroidCommand {
    final Logger log = Logging.getLogger Real
    final File androidExecutable
    final System system

    Real(File sdk, System system) {
      this.system = system
      def toolsDir = new File(sdk, FD_TOOLS)
      androidExecutable = new File(toolsDir, androidCmdName())
    }

    @Override int update(String filter) {
      // -a == all
      // -t == filter
      def options = ['-a', '-t', filter]
      def cmd = generateCommand('update', options)
      def process = new ProcessBuilder(cmd)
          .redirectErrorStream(true)
          .start()

      // Press 'y' and then enter on the license prompt.
      def output = new OutputStreamWriter(process.out)
      output.write("y\n")
      output.close()

      // Pipe the command output to our log.
      def input = new InputStreamReader(process.in)
      def line
      while ((line = input.readLine()) != null) {
        log.debug line
      }

      return process.waitFor()
    }

    @Override String listAll(String filter) {
      listWithOptions(filter, ['-a', '-e'])
    }

    @Override String listUpdates(String filter) {
      listWithOptions(filter, ['-e'])
    }

    String listWithOptions(String filter, def options) {
      // -a == all
      // -e == extended
      def cmd = generateCommand('list', options)
      def process = new ProcessBuilder(cmd)
          .redirectErrorStream(true)
          .start()

      // Pipe the command output to our log.
      def input = new InputStreamReader(process.in)
      def output = ''
      def line
      while ((line = input.readLine()) != null) {
        log.debug line
        output += line
      }

      process.waitFor()

      def result = ''
      output.split('----------').each {
        if (it.contains('"' + filter + '"')) {
          result += it
        }
      }

      return result
    }

    def generateCommand(String command, options) {
      // -u == no UI
      def result = [androidExecutable.absolutePath, command, 'sdk', '-u'];
      if (options != null) {
        result += options
      }

      // --proxy-host == hostname of a proxy server
      // --proxy-port == port of a proxy server
      def proxyHost = system.property('http.proxyHost');
      def proxyPort = system.property('http.proxyPort');
      if (proxyHost != null && proxyPort != null) {
        result += ['--proxy-host', proxyHost, '--proxy-port', proxyPort]
      }

      return result;
    }
  }
}
