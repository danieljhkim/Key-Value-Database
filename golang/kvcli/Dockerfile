# Use the official Go base image for building
FROM golang:1.24-alpine AS builder

WORKDIR /app

# Copy Go module files
COPY go.mod go.sum ./

# Download dependencies
RUN go mod download

# Copy source code
COPY . .

# Build the CLI binary
RUN go build -o kv

# ------------------------
# Final runtime image
# ------------------------

FROM alpine:latest

WORKDIR /cli

# Copy the built binary from builder stage
COPY --from=builder /app/kv .

COPY config.yaml .
# Optional: make sure it's executable
RUN chmod +x kv

# Default command when container starts
ENTRYPOINT ["./kv"]