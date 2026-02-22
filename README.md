# Clojure Solution to Word Puzzles

Explore Clojure to solve [9 Letter word
Puzzle](https://nineletterword.tompaton.com/adevcrsoi/) and [NYT Spelling Bee](https://www.nytimes.com/puzzles/spelling-bee) puzzles.

See also the other language solutions:

- [Haskell](https://gitlab.com/frankhjung1/haskell-wordpuzzle)
- [Java](https://gitlab.com/frankhjung1/java-wordpuzzle)
- [Kotlin](https://gitlab.com/frankhjung1/kotlin-wordpuzzle)
- [Go](https://gitlab.com/frankhjung1/go-wordpuzzle)
- [Python](https://gitlab.com/frankhjung1/python-wordpuzzle)

## Build

To build this project using [Leiningen](https://leiningen.org/)

```bash
lein build
```

## Run

```bash
$ lein run -- --help

NAME

  Solve word puzzles like those at nineletterword.tompaton.com and NYT Spelling Bee

SYNOPSIS

  wordpuzzle.main [-h|--help]
  wordpuzzle.main [-d|--dictionary PATH] [-s|--size INT] <-l|--letters STRING> [-r|--repeats]

DESCRIPTION

  List all valid words using provided letters.  Each word must contain
  the mandatory letter which is the first character in the letters list.

OPTIONS

  -h, --help                                     This help text
  -d, --dictionary STRING  resources/dictionary  Alternate word dictionary
  -s, --size INT           4                     Minimum word size
  -l, --letters STRING                           [REQUIRED] 7+ lowercase letters to make words
  -r, --repeats                                  Letters can be repeated (e.g., Spelling Bee)

LICENSE

  Copyright © 2022-2026 Frank H Jung, MIT License
```

### Example - 9 letter puzzle

```bash
$ lein with-profile dev run -- --size=7 --letters=cadevrsoi
varicose
sidecar
divorce
discover
divorces
viscera
```

### Example - Spelling Bee puzzle

```bash
$ lein with-profile dev run -- --size=7 --repeats --letters=mitncao
commotion
monomania
maintain
ammonia
imitation
monotonic
ottoman
anatomic
animation
cinnamon
minicam
```

### Example - java standalone jar

Build a standalone (Über) JAR:

```bash
lein uberjar
```

Run with:

```bash
java -cp target/uberjar/wordpuzzle-0.2.0-standalone.jar clojure.main -m wordpuzzle.main -h
```

## Updating dependencies

Dependencies for the project are defined in `project.clj`. After changing
versions (or adding new libraries) simply fetch them with:

```bash
lein deps
```

The project already includes the `lein-ancient` plugin, which can inspect and
upgrade out‑of‑date coordinates:

```bash
# show any dependencies that have newer releases available
lein ancient

# automatically update `project.clj` to use the latest versions
lein ancient upgrade
```

Remember to run `lein deps` again after editing `project.clj` to pull down any
new artifacts.

## LICENSE

[LICENSE](LICENSE)
