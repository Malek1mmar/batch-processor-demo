package com.malloc.batchprocessor.model;

import java.math.BigDecimal;

public record TransactionCsv(String date, String description, BigDecimal amount) { }
