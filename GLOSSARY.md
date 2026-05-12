# Glossary

## Puzzle

A set of constraints for a word game. It includes a set of available letters, a
**Mandatory Letter**, a minimum word size, and a rule about whether letters can
be repeated.

## Mandatory Letter

The specific character that MUST be present in every valid word found by the
**Solver**. By convention in this application, it is the first character of the
provided letter string.

## Dictionary

A collection of candidate words, typically provided as a file or a sequence of
strings.

## Solver

The core logic that identifies which words from a **Dictionary** satisfy the
constraints of a **Puzzle**.
