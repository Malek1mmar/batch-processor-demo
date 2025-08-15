# Spring Batch Transaction Processor

## 📌 Overview
This project is a Spring Batch application that:
1. Reads transaction data from a CSV file.
2. Transforms and cleans the data.
3. Saves it to a PostgreSQL database.
4. Generates a category-based summary report.

## 🏗 Architecture
Reader (CSV) → Processor (Transform) → Writer (DB) → Aggregator → Report Writer (TXT)

## ⚙️ How It Works

### Step 1 - Import Transactions
- Uses `FlatFileItemReader` to read CSV rows.
- `TransactionItemProcessor` cleans and categorizes data.
- Writes to `transactions` table via `JdbcBatchItemWriter`.

### Step 2 - Generate Report
- Uses `JdbcCursorItemReader` to fetch aggregated totals.
- Writes results to a `.txt` file with category totals.

## 🗄 Database
**Table: `transactions`**
| Column       | Type         |
|--------------|--------------|
| id           | BIGSERIAL PK |
| date         | DATE         |
| description  | VARCHAR(255) |
| amount       | NUMERIC(12,2)|
| category     | VARCHAR(100) |

## 🚀 Running the App
```bash
mvn spring-boot:run
```
Or with parameters:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="inputFile=transactions.csv"
```

## 📂 Input CSV Example
```
date,description,amount
2025-08-01,Walmart,-54.23
2025-08-02,Amazon,-120.75
2025-08-03,Landlord,-800.00
```

## 📄 Output Report Example
```
===== Transaction Summary =====
GROCERY: -54.23
SHOPPING: -120.75
RENT: -800.00
===============================
```
