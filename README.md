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
package com.github.romanqed.jconv;

public class Main {
    public static void main(String[] args) {
        var builder = new LinkedPipelineBuilder<Integer>();
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
        pipeline.accept(10);
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
package com.github.romanqed.jconv;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        var builder = new LinkedPipelineBuilder<>();
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
                    throw new IOException(); // Some I/O problem occurs here
                })
                .build();
        pipeline.accept(null);
    }
}
```

This example will print

```
Catch IO exception: 
java.io.IOException
	at com.github.romanqed.jconv/com.github.romanqed.jconv.Main.lambda$main$1(Main.java:19)
	at com.github.romanqed.jconv/com.github.romanqed.jconv.LinkedTask.run(LinkedTask.java:26)
	at com.github.romanqed.jconv/com.github.romanqed.jconv.Main.lambda$main$0(Main.java:12)
	at com.github.romanqed.jconv/com.github.romanqed.jconv.LinkedTask.accept(LinkedTask.java:32)
	at com.github.romanqed.jconv/com.github.romanqed.jconv.Main.main(Main.java:22)
```

### Short-circuiting

```Java
package com.github.romanqed.jconv;

public class Main {
    public static void main(String[] args) {
        var builder = new LinkedPipelineBuilder<Integer>();
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
        pipeline.accept(5); // <-- This call will print "5"
        pipeline.accept(0); // <-- This call will print nothing
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
