package com.malloc.batchprocessor.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Transaction(LocalDate date, String description, BigDecimal amount, String category) { }
