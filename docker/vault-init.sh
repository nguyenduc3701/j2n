#!/bin/sh

# Set Vault address to the service name in docker-compose
export VAULT_ADDR='http://vault:8200'
SECRETS_FILE="/vault-secrets.env"

# Wait for Vault to be ready
until vault status > /dev/null 2>&1; do
  echo "Waiting for Vault at $VAULT_ADDR..."
  sleep 2
done

if [ -f "$SECRETS_FILE" ]; then
  echo "Vault is ready. Provisioning secrets from $SECRETS_FILE..."
  
  # Read the .env file, ignore comments and empty lines, and pass to vault kv put
  # Using xargs to convert newlines to spaces for the command arguments
  vault kv put secret/application $(grep -v '^#' "$SECRETS_FILE" | grep -v '^$' | xargs)
  
  echo "Secrets provisioned successfully!"
else
  echo "Error: $SECRETS_FILE not found inside container!"
  exit 1
fi
