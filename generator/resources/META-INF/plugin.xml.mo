<idea-plugin>
  <id>com.margush.{{theme}}</id>
  <name>{{theme}}</name>
  <version>1.0</version>
  <vendor email="matthias.margush@me.com" url="http://github.com/matthias-margush/paper">Matthias Margush</vendor>

  <description><![CDATA[
  {{description}}
    ]]></description>

  <!-- please see https://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/build_number_ranges.html for description -->
  <idea-version since-build="173.0"/>

  <!-- please see https://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/plugin_compatibility.html
       on how to target different products -->
  <depends>com.intellij.modules.platform</depends>

  <extensions defaultExtensionNs="com.intellij">
    <!-- Add your extensions here -->
    <themeProvider id="{{uuid}}" path="/{{theme}}.theme.json"/>
  </extensions>

  <actions>
    <!-- Add your actions here -->
  </actions>

</idea-plugin>
