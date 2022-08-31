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

```bash
$ lein run -- --help
NAME

  Solve word puzzles like those at nineletterword.tompaton.com

SYNOPSIS

  wordpuzzle.main [-h|--help]
  wordpuzzle.main [-d|--dictionary PATH] [-s|--size INT] <-l|--letters STRING>

DESCRIPTION

  List all valid words using provided letters.  Each word must contain
  the mandatory letter which is the first character in the letters list.

OPTIONS

  -h, --help                                     This help text
  -d, --dictionary STRING  resources/dictionary  Alternate word dictionary
  -s, --size INT           4                     Minimum word size of 1 to 9 letters
  -l, --letters STRING                           [REQUIRED] 9 lowercase letters to make words

LICENSE

  Copyright © 2022 Frank H Jung, GPLv3.0
```

### Example - lein run

```bash
$ lein run -- --size=7 --letters=cadevrsoi
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

[LICENSE](LICENSE)
