# easy-javafx-gui

A Java library that helps you to comfortably create JavaFX applications.

>**Warning** ⚠️   
>This project is still under construction and "work-in-progress". 

## Features 📦

* Lightweight library with minimal dependencies ("sl4j-api" and "classgraph")
* [MVP](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter) like component separation
* Lightweight dependency injection mechanism
* Theme manager to support different themes and colors

## Documentation 📒

It is recommended to read the documentation in the following order.

* [Getting started 🚀](https://github.com/DGS-Development/easy-javafx-gui/blob/main/static/documentation/getting-started/getting-started.md)
* [Lifecycle 🔁](https://github.com/DGS-Development/easy-javafx-gui/blob/main/static/documentation/lifecycle/lifecycle.md)
* [Dependency-Injection 🔎 (optional)](https://github.com/DGS-Development/easy-javafx-gui/blob/main/static/documentation/dependency-injection/dependency-injection.md)
* [Theme manager 🎨 (optional)](https://github.com/DGS-Development/easy-javafx-gui/blob/main/static/documentation/theme-manager/theme-manager.md)

## Requirements ☑️

* You need a Java 21 JDK to use this library (it is highly recommended to use the [Bellsoft Liberica JDK](https://bell-sw.com/pages/downloads/), which already includes JavaFX)

## Installation 🔨

The easiest way to use the library in your project is to add it as a jitpack-dependency.

### Maven

Add the jitpack repository.

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the dependency.

```xml
<dependency>
    <groupId>eu.dgs-development</groupId>
    <artifactId>easy-javafx-gui</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Gradle

Add the jitpack repository.

```text
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```

Add the dependency.

```text
dependencies {
    implementation 'eu.dgs-development:easy-javafx-gui:1.0.0'
}
```