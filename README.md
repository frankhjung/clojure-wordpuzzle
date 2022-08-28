# Clojure Solution to 9 Letter Word Puzzle

Explore Clojure to solve [9 Letter word
Puzzle](https://nineletterword.tompaton.com/adevcrsoi/).

See also the other language solutions:

* [Haskell](https://gitlab.com/frankhjung1/haskell-wordpuzzle)
* [Java](https://gitlab.com/frankhjung1/java-wordpuzzle)
* [Kotlin](https://gitlab.com/frankhjung1/kotlin-wordpuzzle)
* [Go](https://gitlab.com/frankhjung1/go-wordpuzzle)
* [Python](https://gitlab.com/frankhjung1/python-wordpuzzle)

## Build

To build this project using [Leiningen](https://leiningen.org/)

```bash
lein build
```

## Run

### Example - lein run

```bash
$ lein run -- --size 7 --letters cadevrsoi
varicose
sidecar
divorce
discover
divorces
viscera
```

### Example - java standalone jar

Build a standalone (Über) JAR:

```bash
lein uberjar
```

Run with:

```bash
java -cp target/uberjar/wordpuzzle-0.1.0-standalone.jar clojure.main -m wordpuzzle.main -h
```

## LICENSE

[LICENSE]
