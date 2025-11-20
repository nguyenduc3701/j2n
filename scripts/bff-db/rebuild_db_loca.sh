#!/bin/bash

# Config MySQL
MYSQL_USER="root"
MYSQL_PASSWORD="Welcome1"
MYSQL_DB="j2n_bff"
MYSQL_HOST="localhost"
MYSQL_PORT="3306"

# Folder chứa các file sql
SQL_DIR="."

# Lặp qua tất cả file .sql trong folder, chạy theo thứ tự tên file
mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $MYSQL_USER -p$MYSQL_PASSWORD -e "CREATE DATABASE IF NOT EXISTS $MYSQL_DB;"

for sql_file in $(ls $SQL_DIR/*.sql | sort); do
    echo "Running $sql_file..."
    mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DB < $sql_file
    if [ $? -ne 0 ]; then
        echo "Error executing $sql_file"
        exit 1
    fi
done

echo "All SQL scripts executed successfully!"
