# Heartbeats/EnergyMon Profiler Java Bindings

This project provides Java bindings and thin wrappers around the `he-profiler` library.

## Dependencies

The `he-profiler` library, headers, and dependencies should be installed to the system.

The latest `he-profiler` C library can be found at
[https://github.com/connorimes/he-profiler](https://github.com/connorimes/he-profiler).

## Building

This project uses [Maven](http://maven.apache.org/).
Currently the only supported platforms are the `unix` family.
To build and run junit tests:

```sh
mvn clean install
```

If `he-profiler` is not installed to a default location, you need to set the `PKG_CONFIG_PATH` environment variable or export it to your environment so that `pkg-config` can discover the library.
Unless you are skipping tests (`-DskipTests=true`), you must do the same for `LD_LIBRARY_PATH`.

```sh
PKG_CONFIG_PATH=/path/to/he-profiler/install/lib/pkgconfig:$PKG_CONFIG_PATH \
  LD_LIBRARY_PATH=/path/to/he-profiler/install/lib/:$LD_LIBRARY_PATH \
  mvn clean package
```

## Usage

To integrate with the library, add it as a Maven dependency to your project's `pom.xml`:

```xml
    <dependency>
      <groupId>edu.uchicago.cs.heprofiler</groupId>
      <artifactId>he-profiler</artifactId>
      <version>0.0.1-SNAPSHOT</version>
    </dependency>
```

`edu.uchicago.cs.heprofiler.HEProfiler` provides static methods for initializing and destroying the profilers.
Events are processed through the `edu.uchicago.cs.heprofiler.HEProfilerEvent` interface - the default implementation is `edu.uchicago.cs.heprofiler.DefaultHEProfilerEvent`.

When launching, you will need to set the property `java.library.path` to include the location of the native library created by the module `libhe-profiler-wrapper` and its dependencies.
