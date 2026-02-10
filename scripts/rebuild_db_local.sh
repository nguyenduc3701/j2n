#!/bin/bash

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# MySQL Config
MYSQL_USER="root"
MYSQL_PASSWORD="Welcome1"
MYSQL_HOST="localhost"
MYSQL_PORT="3306"

echo "🚀 Starting Master Database Rebuild..."

DB_DIRS=("auth-db" "bff-db" "image-db" "report-db" "config-db")

for dir in "${DB_DIRS[@]}"; do
    TARGET_DIR="$SCRIPT_DIR/$dir"
    if [ -d "$TARGET_DIR" ]; then
        # Map directory to database name
        case "$dir" in
            "auth-db") DB_NAME="j2n_auth" ;;
            "bff-db")  DB_NAME="j2n_bff" ;;
            "image-db") DB_NAME="j2n_image" ;;
            "report-db") DB_NAME="j2n_report" ;;
            "config-db") DB_NAME="j2n_config" ;;
            *) DB_NAME="j2n_unknown" ;;
        esac
        
        echo "--------------------------------------------------"
        echo "📦 Rebuilding Database: $DB_NAME (from $dir)"
        
        # Create database
        mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $MYSQL_USER -p$MYSQL_PASSWORD -e "DROP DATABASE IF EXISTS $DB_NAME; CREATE DATABASE $DB_NAME;"
        
        # Execute all SQL files in order
        for sql_file in $(ls "$TARGET_DIR"/*.sql | sort); do
            echo "  [Running] $(basename "$sql_file")..."
            mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $MYSQL_USER -p$MYSQL_PASSWORD "$DB_NAME" < "$sql_file"
            if [ $? -ne 0 ]; then
                echo "❌ Error executing $sql_file"
                exit 1
            fi
        done
    else
        echo "⚠️  Directory not found: $TARGET_DIR"
    fi
done

echo "--------------------------------------------------"
echo "✅ All databases rebuilt successfully!"
