# Spring Batch Demo

A minimal **Spring Batch** project using **Spring Boot** that demonstrates reading data from a CSV file, processing it, and writing the results to the console.

## 📌 Overview

This project:
- Reads records from a CSV file (`persons.csv`).
- Processes each record by converting first and last names to uppercase.
- Writes the transformed records to the console.
- Uses **chunk-oriented processing** with Spring Batch.

---

## 🛠 Tech Stack

- **Java 17+** (or compatible version)
- **Spring Boot 3.x**
- **Spring Batch**
- **Maven**

---

## 📄 Example Input File

**`src/main/resources/persons.csv`**
```csv
John,Doe
Jane,Smith
Bob,Brown
```

## ⚙️ How It Works

### Reader
Uses **FlatFileItemReader** to read rows from a CSV file.

### Processor
`PersonItemProcessor` converts both `firstName` and `lastName` to uppercase.

### Writer
Outputs the processed data to the console  
*(can be replaced with database writing or other destinations)*.

### Chunk Size
Processes **3 items at a time** in a single transaction for efficiency and atomicity.