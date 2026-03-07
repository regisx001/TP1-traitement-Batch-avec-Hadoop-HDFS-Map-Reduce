# TP1 — Batch Processing with Hadoop HDFS & MapReduce

A Java-based Hadoop MapReduce lab project featuring two classic batch processing jobs:

1. **Word Count** — counts the frequency of each word in a text file.
2. **Total Sales Per Store** — aggregates total sales revenue per store from a purchases dataset.

---

## Project Structure

```
src/main/java/com/hadoop/mapreduce/
├── tp1/                          # Word Count job
│   ├── TokenizerMapper.java      # Mapper: tokenizes each line into words
│   ├── IntSumReducer.java        # Reducer: sums occurrences per word
│   └── WordCount.java            # Driver: configures and submits the job
│
└── application/                  # Total Sales job
    ├── SalesMapper.java          # Mapper: extracts store name and sale price
    ├── SalesReducer.java         # Reducer: sums total sales per store
    └── TotalSales.java           # Driver: configures and submits the job

src/main/resources/input/
├── file.txt                      # Sample input for Word Count
└── purchases.txt                 # Sample input for Total Sales
```

---

##  Prerequisites

| Tool | Version |
|------|---------|
| Java (JDK) | 8+ |
| Apache Maven | 3.6+ |
| Apache Hadoop | 3.3.6 |
| Docker (optional) | For running a Hadoop cluster locally |

---

##  Build

Package the project into a fat JAR (includes all dependencies):

```bash
mvn clean package
```

The output JAR will be located at:

```
target/wordcount-1.0-SNAPSHOT-jar-with-dependencies.jar
```

> The default main class is set to `TotalSales`. Use the `-mainClass` flag or specify the class when running via `hadoop jar` to run `WordCount` instead.

---

## Running on a Hadoop Cluster (Docker)

### 1. Copy the JAR to the Hadoop master container

```bash
sudo docker cp target/wordcount-1.0-SNAPSHOT-jar-with-dependencies.jar hadoop-master:/root/totalsales.jar
```

### 2. Access the master container

```bash
sudo docker exec -it hadoop-master bash
```

---

## Job 1: Word Count

Counts how many times each word appears in an input text file.

### Input format (`file.txt`)

```
Hello Wordcount!
Hello Hadoop!
```

### Run on HDFS

```bash
# Upload input file to HDFS
hadoop fs -mkdir -p /input
hadoop fs -put /root/file.txt /input/

# Run the job
hadoop jar /root/totalsales.jar com.hadoop.mapreduce.tp1.WordCount /input /output/wordcount

# View results
hadoop fs -cat /output/wordcount/part-r-00000
```

### Expected output

```
Hadoop!    1
Hello      2
Wordcount! 1
```

---

## Job 2: Total Sales Per Store

Reads a tab/space-separated purchases file and computes the total revenue per store.

### Input format (`purchases.txt`)

```
2023-01-01 10:00 StoreA TV 500 Card
2023-01-01 11:00 StoreB Phone 300 Cash
2023-01-01 12:00 StoreA Laptop 700 Card
```

> **Field layout:** `date  time  store  product  price  payment_method`

### Run on HDFS

```bash
# Upload input file to HDFS
hadoop fs -mkdir -p /input
hadoop fs -put /root/purchases.txt /input/

# Run the job
hadoop jar /root/totalsales.jar com.hadoop.mapreduce.application.TotalSales /input/purchases.txt /output/totalsales

# View results
hadoop fs -cat /output/totalsales/part-r-00000
```

### Expected output

```
StoreA    1200.0
StoreB    300.0
```

---

##  Architecture Overview

### MapReduce Flow

```
Input File
    │
    ▼
┌─────────┐
│  Mapper │  ──►  Emits (key, value) pairs
└─────────┘
    │
    ▼
┌──────────┐
│ Shuffler │  ──►  Groups values by key
└──────────┘
    │
    ▼
┌─────────┐
│ Reducer │  ──►  Aggregates values per key
└─────────┘
    │
    ▼
Output File (HDFS)
```

### Word Count

| Phase | Class | Logic |
|-------|-------|-------|
| Mapper | `TokenizerMapper` | Splits each line into tokens → emits `(word, 1)` |
| Combiner | `IntSumReducer` | Local pre-aggregation on each node |
| Reducer | `IntSumReducer` | Sums all counts for each word |

### Total Sales

| Phase | Class | Logic |
|-------|-------|-------|
| Mapper | `SalesMapper` | Parses each line → emits `(store, price)` |
| Reducer | `SalesReducer` | Sums all prices for each store |

---

##  Technologies

- **Java 8**
- **Apache Hadoop 3.3.6** (`hadoop-common`, `hadoop-hdfs`, `hadoop-mapreduce-client-*`)
- **Apache Maven 3** with `maven-assembly-plugin` for fat JAR packaging

---

##  License

This project is intended for educational purposes as part of a Big Data lab assignment.
