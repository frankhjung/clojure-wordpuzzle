# Makefile for running Leiningen tasks primarily under the `dev` profile.
# The `cicd` profile is only used for GitHub/GitLab pipelines; special
# targets prefixed with "cicd-" invoke it.

LEIN = lein with-profile dev
LEIN_CICD = lein with-profile cicd
LEIN_UBERJAR = lein with-profile cicd,uberjar

.PHONY: default
default: clean fmt check compile test ## Run default build pipeline

.PHONY: help
help: ## Display this help
	@printf "Default goal: \033[36m%s\033[0m\n" "${.DEFAULT_GOAL}"
	@awk 'BEGIN {FS = ":.*##"; \
	  printf "\nUsage:\n  make \033[36m<target>\033[0m\n\nTargets:\n"} \
	    /^[a-zA-Z_-]+:.*?##/ \
	    { printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2 }' \
	  $(MAKEFILE_LIST)

#
# Local development targets
#

.PHONY: clean
clean: ## Clean build artifacts
	$(LEIN) clean

.PHONY: fmt
fmt: ## Format source code with cljfmt
	$(LEIN) cljfmt fix

.PHONY: check
check: ## Check source formatting with cljfmt
	$(LEIN) cljfmt check

.PHONY: compile
compile: ## Compile source code
	$(LEIN) compile

.PHONY: test
test: ## Run tests with eftest
	$(LEIN) eftest

.PHONY: run
run: ## Run the application with example puzzles
	@echo 9-Letter word puzzle:
	$(LEIN) run -- --size=7 --letters=cadevrsoi
	@echo Spelling Bee puzzle:
	$(LEIN) run -- --size=7 --repeats --letters=mitncao

.PHONY: uberjar
uberjar: ## Build standalone uberjar
	$(LEIN_UBERJAR) uberjar

.PHONY: run-uberjar
run-uberjar: uberjar ## Build and run the standalone uberjar
	java -jar target/uberjar/wordpuzzle-*-standalone.jar --size=7 --letters=cadevrsoi --dictionary=resources/dictionary

#
# Targets for CI/CD pipelines (GitHub/GitLab)
#

.PHONY: cicd-clean
cicd-clean: ## Clean build artifacts (CI/CD)
	$(LEIN_CICD) clean


.PHONY: cicd-test
cicd-test: ## Run tests with eftest (CI/CD)
	$(LEIN_CICD) eftest

#
# Utility target to show supported lein profiles
#
.PHONY: show-profiles
show-profiles: ## Show supported Leiningen profiles
	@echo "Project :profiles from project.clj:"
	@grep -n ":profiles" -A20 project.clj | sed 's/^/    /'

.PHONY: dictionary
dictionary: ## Generate dictionary from /usr/share/dict/words
ifeq (,$(wildcard /usr/share/dict/words))
	@echo Warning: /usr/share/dict/words not found, skipping dictionary generation
else
	@echo filtering 4-letters or more words from dictionary ...
	@LC_ALL=C grep -E '^[a-z]{4,}$$' /usr/share/dict/words \
	  | grep -Ev '^m{0,3}(cm|cd|d?c{0,3})(xc|xl|l?x{0,3})(ix|iv|v?i{0,3})$$' \
	  | sort -u > resources/dictionary
	@echo $(shell wc -l < resources/dictionary) words in dictionary
endif
