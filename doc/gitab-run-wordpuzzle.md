# Run WordPuzzle on GitLab — Specification

## Purpose

Specify the behaviour and implementation details for running the
pre-built WordPuzzle standalone JAR from a GitLab pipeline. The
document describes the current pipeline implementation, required
inputs, artefact handling, and the manual run pipeline used to
execute the JAR on a minimal runner image.

## Scope

This specification applies to the GitLab CI configuration in this
repository (`.gitlab-ci.yml`) and the included `run-wordpuzzle`
template (see `templates/run-wordpuzzle/template.yml`). It assumes
the build pipeline produces a Clojure standalone uberjar
(`wordpuzzle-*-standalone.jar`) and that the repository contains a
`resources/dictionary` file which is packaged flat alongside the
JAR.

## Definitions

- Artifact / artefact: packaged output (tarball) containing the
  standalone JAR and dictionary. Spelt as "artefact" in
  documentation to match project style.
- TARGET_TAG: release tag (for example `v1.0.0`) that identifies a
  packaged artefact in the GitLab Generic Package Registry.
- Uberjar: a self-contained JAR produced by
  `lein with-profile cicd,uberjar uberjar`, containing all
  Clojure and Java dependencies.

## Pipeline Inputs (`spec`)

The pipeline declares the following inputs via the GitLab CI
`spec:inputs` block. When triggered from the web UI these appear
as form fields:

| Input        | Type    | Default   | Validation       | Description          |
|--------------|---------|-----------|------------------|----------------------|
| `target_tag` | string  | `v1.0.0`  | `^v\d\.\d+(\.\d+)$` | Release tag to run   |
| `size`       | number  | `4`       | —                | Minimum word size    |
| `letters`    | string  | `""`      | `^[a-z]*$`       | Letters to use       |
| `repeats`    | boolean | `true`    | —                | Allow repeated letters |

These are mapped to CI variables `TARGET_TAG`, `SIZE`, `LETTERS`,
and `REPEATS` via `$[[ inputs.<name> ]]`.

## Requirements

1. The build pipeline shall be triggered for pushes on any branch
   and for tag pushes.
2. On tag pushes that match `^v`, the build pipeline shall produce
   and publish a release artefact containing the uberjar
   (`wordpuzzle-*-standalone.jar`) and the `dictionary` file,
   packaged flat inside `wordpuzzle-release.tar.gz`.
3. The `run-wordpuzzle` pipeline/job shall be manually triggerable
   via the GitLab web UI (`CI_PIPELINE_SOURCE == "web"`).
4. The `run-wordpuzzle` job shall not recompile the project; it
   must download the release artefact identified by `TARGET_TAG`
   and execute the contained JAR using `java -jar`.
5. The `run-wordpuzzle` job shall run on a minimal container image
   with a pre-baked JRE (`eclipse-temurin:17-jre-alpine`) and
   install only `curl` and `ca-certificates` at runtime.
6. The `run-wordpuzzle` job shall accept the variables `SIZE`,
   `LETTERS`, `REPEATS` and `TARGET_TAG` as inputs and pass them
   to the JAR at runtime.

## Current Implementation (mapping to repository files)

- `.gitlab-ci.yml`
  - Declares pipeline `spec` inputs (see table above) and maps
    them to CI variables.
  - Uses `workflow.rules` to allow manual `web` pipelines and to
    skip pipelines created for branch pushes when an MR is open.
  - Defines `build_and_test` job which runs for non-web pipelines
    using the `clojure:temurin-11-lein-alpine` image; produces
    `target/uberjar/` and `resources/dictionary` as artifacts.
  - Defines `package_and_release` job which runs on tag pushes
    matching `^v`. This job:
    1. Stages the uberjar from `target/uberjar/` and
       `resources/dictionary` flat in a `release/` directory.
    2. Creates `wordpuzzle-release.tar.gz` from that directory.
    3. Uploads the tarball to the GitLab Generic Package Registry.
    4. Creates a GitLab Release using the `release` keyword.

- `templates/run-wordpuzzle/template.yml`
  - Implements the `run_wordpuzzle` job that:
    - Runs on `CI_PIPELINE_SOURCE == "web"` (manual trigger).
    - Uses `eclipse-temurin:17-jre-alpine` (JRE pre-baked) and
      installs only `curl` and `ca-certificates` via `apk`.
    - Validates `TARGET_TAG` is provided, downloads the artefact
      from the GitLab Generic Package Registry using `JOB-TOKEN`,
      extracts it, and runs:
      ```
      java -jar wordpuzzle-*-standalone.jar \
        --size=SIZE --letters=LETTERS [--repeats] \
        --dictionary=dictionary
      ```

## Security Considerations

- `JOB-TOKEN` is used to fetch packages from the GitLab Generic
  Package Registry; this is a scoped credential available to CI
  jobs and is appropriate for this use case. Ensure
  project/package visibility and token scopes are configured
  correctly.
- The `run_wordpuzzle` job uses a pre-baked JRE image
  (`eclipse-temurin:17-jre-alpine`) and only fetches `curl` and
  `ca-certificates` at runtime, minimising the attack surface.

## Operational Notes

- The `package_and_release` job runs only on tag pushes; therefore
  `TARGET_TAG` supplied to `run_wordpuzzle` must correspond to an
  existing tag with an uploaded artefact.
- `run_wordpuzzle` declares a `needs` dependency on
  `package_and_release` with `optional: true`. This allows the run
  job to be started independently when the artefact is already
  available in the registry.

## Example Run (manual)

1. Ensure a release tag (for example `v1.0.0`) has been created
   and the build pipeline published `wordpuzzle-release.tar.gz` to
   the Generic Package Registry.
2. From **CI / Run pipeline**, start a pipeline with
   `TARGET_TAG=v1.0.0`, `SIZE=6`, `LETTERS=cadevrsoi`,
   `REPEATS=false`.
3. The `run_wordpuzzle` job will download the artefact, extract
   the JAR and `dictionary`, and execute:

```bash
java -jar wordpuzzle-*-standalone.jar \
  --size=6 --letters=cadevrsoi --dictionary=dictionary
```

## Recommendations

1. Consider signing artefacts or publishing to a registry with
   immutability to protect against accidental replacement of
   release packages.

## Change Log

- 2026-02-26: Updated specification to match current workspace:
  image changed to `eclipse-temurin:17-jre-alpine`; documented
  `spec` inputs with defaults and validation; noted
  `resources/dictionary` in `build_and_test` artifacts; removed
  fulfilled recommendation about pre-baked JRE image.
- 2026-02-26: Initial Clojure uberjar specification; replaced all
  native executable references with JAR/`java -jar` language.
- 2026-02-25: Reworked document into a requirements/specification
  and aligned content with `.gitlab-ci.yml` and
  `templates/run-wordpuzzle/template.yml`.
