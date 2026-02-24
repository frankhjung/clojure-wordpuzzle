# Makefile for running Leiningen tasks primarily under the `dev` profile.
# The `cicd` profile is only used for GitHub/GitLab pipelines; special
# targets prefixed with "cicd-" invoke it.

LEIN = lein with-profile dev
LEIN_CICD = lein with-profile cicd
LEIN_UBERJAR = lein with-profile cicd,uberjar

.PHONY: default
default: clean fmt check compile test

# basic help listing
.PHONY: help
help:
	@echo "Available make targets:"
	@grep -E '^[a-zA-Z_-]+:' Makefile | sed 's/:.*$$//' | sort | uniq | xargs -n1 printf "  %s\n"

#
# Local development targets
#

.PHONY: clean
clean:
	$(LEIN) clean

.PHONY: fmt
fmt:
	$(LEIN) cljfmt fix

.PHONY: check
check:
	$(LEIN) cljfmt check

.PHONY: compile
compile:
	$(LEIN) compile

.PHONY: test
test:
	$(LEIN) eftest

.PHONY: run
run:
	@echo 9-Letter word puzzle:
	$(LEIN) run -- --size=7 --letters=cadevrsoi
	@echo Spelling Bee puzzle:
	$(LEIN) run -- --size=7 --repeats --letters=mitncao

.PHONY: uberjar
uberjar:
	$(LEIN_UBERJAR) uberjar

.PHONY: run-uberjar
run-uberjar: uberjar
	java -jar target/uberjar/wordpuzzle-*-standalone.jar --size=7 --letters=cadevrsoi --dictionary=resources/dictionary

#
# Targets for CI/CD pipelines (GitHub/GitLab)
#

.PHONY: cicd-clean
cicd-clean:
	$(LEIN_CICD) clean


.PHONY: cicd-test
cicd-test:
	$(LEIN_CICD) eftest

#
# Utility target to show supported lein profiles
#
.PHONY: show-profiles
show-profiles:
	@echo "Project :profiles from project.clj:"
	@grep -n ":profiles" -A20 project.clj | sed 's/^/    /'

.PHONY: dictionary
dictionary:
ifeq (,$(wildcard /usr/share/dict/words))
	@echo Warning: /usr/share/dict/words not found, skipping dictionary generation
else
	@echo filtering 4-letters or more words from dictionary ...
	@LC_ALL=C grep -E '^[a-z]{4,}$$' /usr/share/dict/words | sort -u > resources/dictionary
	@echo $(shell wc -l < resources/dictionary) words in dictionary
endif
