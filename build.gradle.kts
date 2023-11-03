import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
  id("org.gradle.eclipse")
  id("org.gradle.java-library")
  id("org.gradle.checkstyle")

  id("io.freefair.lombok") version "6.6.3"
  id("com.github.johnrengelman.shadow") version "8.1.0"

  id("xyz.jpenilla.run-paper") version "2.0.1"
  id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
}

if (project.hasProperty("tag")) {
  version = project.property("tag")!!
} else {
  version = "develop"
}

var basePackage = "ml.empee.plots"

bukkit {
  load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
  main = "${basePackage}.RentablePlots"
  apiVersion = "1.13"
  depend = listOf("DecentHolograms")
  authors = listOf("Mr. EmPee")
}

repositories {
  maven("https://repo.codemc.io/repository/nms/")
  maven("https://jitpack.io")

  mavenCentral()
}

dependencies {
  compileOnly("com.destroystokyo.paper:paper:1.13.2-R0.1-SNAPSHOT")

  compileOnly("org.jetbrains:annotations:24.0.1")
  compileOnly("org.xerial:sqlite-jdbc:3.34.0")


  compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.4")

  // Core depends
  implementation("com.github.Mr-EmPee:LightWire:1.1.0")

  implementation("me.lucko:commodore:2.2") {
    exclude("com.mojang", "brigadier")
  }

  implementation("cloud.commandframework:cloud-paper:1.8.3")
  implementation("cloud.commandframework:cloud-annotations:1.8.3")

  // Utilities
  implementation("com.github.Mr-EmPee:SimpleMenu:0.3.1")
  implementation("com.github.Mr-EmPee:ItemBuilder:1.1.3")
  implementation("com.github.cryptomorin:XSeries:9.4.0") { isTransitive = false }

  //implementation("org.cloudburstmc:nbt:3.0.1.Final")
  //implementation("com.github.Mr-EmPee:SimpleHeraut:1.0.1")
}

tasks {
  checkstyle {
    toolVersion = "10.10.0"
    configFile = file("$projectDir/checkstyle.xml")
  }

  shadowJar {
    archiveFileName.set("${project.name}.jar")
    isEnableRelocation = project.version != "develop"
    relocationPrefix = "$basePackage.relocations"
  }

  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }

  processResources {
    filteringCharset = Charsets.UTF_8.name()
  }

  compileJava {
    sourceCompatibility = "11"
    targetCompatibility = "11"

    options.encoding = Charsets.UTF_8.name()
    options.compilerArgs.add("-parameters")
  }

  runServer {
    version.set("1.19.4")
  }
}