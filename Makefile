# -----------------------------------
# Variables
# -----------------------------------

MVN := mvn
GOCLI := golang/kvcli
COORD := kv.coordinator
NODE := kv.server

# -----------------------------------
# Targets
# -----------------------------------

all: java cli

# -----------------
# Build Java: uses Maven only
# -----------------
java:
	@echo "Running Maven build for all modules..."
	$(MVN) clean package
	cp $(COORD)/target/kv.coordinator-*.jar $(COORD)/Coordinator.jar
	cp $(NODE)/target/kv.server-*.jar $(NODE)/Node.jar

# -----------------
# Build Go CLI
# -----------------
cli:
	@echo "Building Go CLI..."
	cd $(GOCLI) && go build -o kv

# -----------------
# Clean everything
# -----------------
clean:
	@echo "Cleaning Maven build artifacts..."
	$(MVN) clean
	@echo "Cleaning Go binary..."
	rm -f $(GOCLI)/kv
	rm -f $(COORD)/Coordinator.jar
	rm -f $(NODE)/Node.jar


.PHONY: all java cli clean