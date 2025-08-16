package com.malloc.batchprocessor.processor;


import com.malloc.batchprocessor.model.Transaction;
import com.malloc.batchprocessor.model.TransactionCsv;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.batch.item.ItemProcessor;

public class TransactionItemProcessor implements ItemProcessor<TransactionCsv, Transaction> {

  private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Override
  public Transaction process(TransactionCsv item) {
    LocalDate date = LocalDate.parse(item.date(), ISO);
    String desc = item.description();
    BigDecimal amount = item.amount();
    String category = categorize(desc);
    return new Transaction(date, desc, amount, category);
  }

  private String categorize(String desc) {
    String d = desc.toLowerCase();
    if (d.contains("leclerc") || d.contains("food")) return "GROCERY";
    if (d.contains("amazon")) return "SHOPPING";
    if (d.contains("landlord") || d.contains("rent")) return "RENT";
    if (d.contains("charge") || d.contains("gym") || d.contains("insurance")) return "UTILITIES";
    return "OTHER";
  }
}