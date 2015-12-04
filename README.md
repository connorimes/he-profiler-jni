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

If the `he-profiler` library is compiled but not installed, you need to specify the `CFLAGS` and `LDFLAGS` properties as part of the build command.
Unless you are skipping tests (`-DskipTests`), you also need to set the `LD_LIBRARY_PATH` environment variable or export it to your environment.

```sh
LD_LIBRARY_PATH=/path/to/he-profiler/_build/lib:$LD_LIBRARY_PATH \
  mvn clean package \
  -DCFLAGS=-I/path/to/he-profiler/inc -DLDFLAGS=/path/to/he-profiler/_build/lib
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
