# Makefile for running Leiningen tasks primarily under the `dev` profile.
# The `cicd` profile is only used for GitHub/GitLab pipelines; special
# targets prefixed with "cicd-" invoke it.

LEIN = lein with-profile dev
LEIN_CICD = lein with-profile cicd

# basic help listing
.PHONY: help
help:
	@echo "Available make targets:"
	@grep -E '^[a-zA-Z_-]+:' Makefile | sed 's/:.*$$//' | sort | uniq | xargs -n1 printf "  %s\n"

.PHONY: default
default: clean fmt check compile test

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
	$(LEIN) eftest

.PHONY: exec
exec:
	@echo 9-Letter word puzzle:
	$(LEIN) run -- --size=7 --letters=cadevrsoi
	@echo Spelling Bee puzzle:
	$(LEIN) run -- --size=7 --repeats --letters=mitncao

.PHONY: uberjar
uberjar:
	$(LEIN) uberjar

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
ifeq (,$(wildcard /usr/share/dict/british-english-huge))
	@echo using dictionary from https://raw.githubusercontent.com/dwyl/english-words/master/words.txt
	@curl -s https://raw.githubusercontent.com/dwyl/english-words/master/words.txt \
		> resources/dictionary
else
	@echo using dictionary from /usr/share/dict/british-english-huge
	@cp /usr/share/dict/british-english-huge resources/dictionary
endif
	@LC_ALL=C grep -E '^[a-z]{4,}$$' resources/dictionary \
		> resources/dictionary.tmp
	@mv resources/dictionary.tmp resources/dictionary
