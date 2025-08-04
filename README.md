# jconv [![jconv](https://img.shields.io/maven-central/v/com.github.romanqed/jconv?strategy=releaseProperty&style=for-the-badge&label=jconv&color=blue)](https://repo1.maven.org/maven2/com/github/romanqed/jconv)

Flexible and lightweight pipeline implementation for Java 11+, inspired by ASP.NET middleware design.

## Features

- Fluent API to build conditional and nested middleware pipelines
- Supports synchronous, asynchronous, and unified task consumers
- Zero-overhead design suitable for high-performance scenarios
- Clear separation between middleware (TaskConsumer) and terminal tasks (Task)
- Conditional branching with `addWhen` (like ASP.NET UseWhen) and `mapWhen` (like ASP.NET MapWhen)
- Easy integration with Java CompletableFuture for async execution

## Getting Started

### Requirements

- Java 11 or higher
- Maven or Gradle build system

## Installation

### Gradle

```groovy
dependencies {
    implementation group: 'com.github.romanqed', name: 'jconv', version: '2.0.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.github.romanqed</groupId>
    <artifactId>jconv</artifactId>
    <version>2.0.0</version>
</dependency>
```

## Usage Examples

### Basic Middleware Pipeline

```java
import com.github.romanqed.jconv.TaskBuilders;

public class Main {
    public static void main(String[] args) throws Throwable {
        var builder = TaskBuilders.<Integer>linked();
        var pipeline = builder
                .add((c, next) -> {
                    System.out.println("{ - 1");
                    next.run(c);
                    System.out.println("} - 4");
                })
                .add((c, next) -> {
                    System.out.println(c + " - 2");
                    next.run(c - 1);
                })
                .add((c, next) -> {
                    System.out.println(c + " - 3");
                })
                .build();

        pipeline.run(10);
    }
}
```

Output:

```
{ - 1
10 - 2
9 - 3
} - 4
```

### Exception Handling in Pipeline

```Java
import com.github.romanqed.jconv.TaskBuilders;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Throwable {
        var builder = TaskBuilders.<Object>linked();
        var pipeline = builder
                .add((ctx, next) -> {
                    try {
                        next.run(ctx);
                    } catch (IOException e) {
                        System.out.println("Caught IOException:");
                        e.printStackTrace();
                    }
                })
                .add((ctx, next) -> {
                    throw new IOException("Simulated I/O error");
                })
                .build();

        pipeline.run(null);
    }
}
```

Output:

```
Caught IOException:
java.io.IOException: Simulated I/O error
    at Main.lambda$main$1(Main.java:...)
    ...
```

### Short-Circuiting Execution

```Java
import com.github.romanqed.jconv.TaskBuilders;

public class Main {
    public static void main(String[] args) throws Throwable {
        var builder = TaskBuilders.<Integer>linked();
        var pipeline = builder
                .add((c, next) -> {
                    if (c > 0) {
                        next.run(c);
                    } // else short-circuit: do nothing, next tasks won't run
                })
                .add((c, next) -> {
                    System.out.println("Processed: " + c);
                })
                .build();

        pipeline.run(5);  // Prints "Processed: 5"
        pipeline.run(0);  // Prints nothing
    }
}
```

### Conditional Branching with addWhen (like UseWhen)

```java
var pipeline = TaskBuilders.<String>linked()
    .addWhen(s -> s.startsWith("admin"), b -> {
        b.add((s, next) -> {
            System.out.println("Admin branch: " + s);
            next.run(s);
        });
    })
    .add((s, next) -> {
        System.out.println("Common branch: " + s);
    })
    .build();

pipeline.run("adminUser");  // Prints both Admin branch and Common branch messages
pipeline.run("guestUser");  // Prints only Common branch message
```

### Conditional Branching with mapWhen (like MapWhen)

```java
var pipeline = TaskBuilders.<String>linked()
    .mapWhen(s -> s.startsWith("admin"), (s) -> {
        System.out.println("Admin mapped branch: " + s);
    })
    .add((s, next) -> {
        System.out.println("Common branch: " + s);
    })
    .build();

pipeline.run("adminUser");  // Prints only Admin mapped branch message
pipeline.run("guestUser");  // Prints only Common branch message
```

Note: `mapWhen` replaces the pipeline if condition is true, short-circuiting subsequent middleware.

## API Overview

- **Task<T>** — terminal unit of work (sync, async or unified)
- **TaskConsumer<T>** — middleware consumer that can delegate downstream
- **TaskConfigurer<T>** — fluent interface for building pipeline steps
- **TaskBuilder<T>** — interface to assemble and build pipelines
- **TaskBuilders** — factory for creating pipeline builders (e.g., linked)

## Built With

* [Gradle](https://gradle.org) - Dependency management
* [jfunc](https://github.com/RomanQed/jfunc) - Functional interfaces and utilities
* [jsync](https://github.com/RomanQed/jfunc) - Async functional interfaces and helpers
* [juni](https://github.com/RomanQed/jfunc) - Unified sync/async interfaces

## Authors

* **[RomanQed](https://github.com/RomanQed)** - Main author

See also the list of [contributors](https://github.com/RomanQed/jconv/contributors)
who participated in this project.

## License

This project is licensed under the Apache License 2.0 — see the [LICENSE](LICENSE) file for details
