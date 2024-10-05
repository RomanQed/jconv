# jconv [![maven-central](https://img.shields.io/maven-central/v/com.github.romanqed/jconv?color=blue)](https://repo1.maven.org/maven2/com/github/romanqed/jconv/)

Flexible and lightweight pipeline implementation for java 11+ in style ASP.NET middleware.

## Getting Started

To install it, you will need:

* Java 11+
* Maven/Gradle

### Features

## Installing

### Gradle dependency

```Groovy
dependencies {
    implementation group: 'com.github.romanqed', name: 'jconv', version: 'LATEST'
}
```

### Maven dependency

```
<dependency>
    <groupId>com.github.romanqed</groupId>
    <artifactId>jconv</artifactId>
    <version>LATEST</version>
</dependency>
```

## Example

### Common example

```Java
import com.github.romanqed.jconv.PipelineBuilders;

public class Main {
    public static void main(String[] args) throws Throwable {
        var builder = PipelineBuilders.<Integer>createClosed();
        var pipeline = builder
                // The code below will be executed inside the pipeline in the following order:
                .add((c, n) -> {
                    System.out.println("{ - 1"); // 1
                    n.run(c);
                    System.out.println("} - 4"); // 4
                })
                .add((c, n) -> {
                    System.out.println(c + " - 2"); // 2
                    n.run(c - 1);
                })
                .add((c, n) -> {
                    System.out.println(c + " - 3"); // 3
                })
                .add((c, n) -> {
                    System.out.println(c); // This code will never be executed
                })
                .build();
        pipeline.run(10);
    }
}
```

This example will print

```
{ - 1
10 - 2
9 - 3
} - 4
```

### Exception handling

```Java
import com.github.romanqed.jconv.PipelineBuilders;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Throwable {
        var builder = PipelineBuilders.createClosed();
        var pipeline = builder
                .add((c, n) -> {
                    try {
                        n.run(null);
                    } catch (IOException e) {
                        System.out.println("Catch IO exception: ");
                        e.printStackTrace();
                    }
                })
                .add((c, n) -> {
                    var io = new IOException();
                    throw new IOException(); // Some I/O problem occurs here
                })
                .build();
        pipeline.run(null);
    }
}
```

This example will print

```
Catch IO exception: 
java.io.IOException
	at Main.lambda$main$1(Main.java:20)
	at com.github.romanqed.jconv/com.github.romanqed.jconv.LinkedRunnable.run(LinkedRunnable.java:26)
	at Main.lambda$main$0(Main.java:13)
	at com.github.romanqed.jconv/com.github.romanqed.jconv.LinkedRunnable.run(LinkedRunnable.java:26)
	at Main.main(Main.java:23)
```

### Short-circuiting

```Java
import com.github.romanqed.jconv.PipelineBuilders;

public class Main {
    public static void main(String[] args) throws Throwable {
        var builder = PipelineBuilders.<Integer>createClosed();
        var pipeline = builder
                .add((c, n) -> {
                    if (c > 0) {
                        n.run(c);
                    }
                })
                .add((c, n) -> {
                    System.out.println(c);
                })
                .build();
        pipeline.run(5); // <-- This call will print "5"
        pipeline.run(0); // <-- This call will print nothing
    }
}
```

## Built With

* [Gradle](https://gradle.org) - Dependency management
* [jfunc](https://github.com/RomanQed/jfunc) - Lambda interfaces

## Authors

* **[RomanQed](https://github.com/RomanQed)** - *Main work*

See also the list of [contributors](https://github.com/RomanQed/jconv/contributors)
who participated in this project.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details
