package com.lianos.darn.expenses.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Vasilis Lianos
 */
public interface Protocol {

    enum Currency { EUROS }

    enum Recurring { DAILY, WEEKDAYS, WEEKENDS, WEEKLY, MONTHLY, YEARLY }

    enum ExpenseType { HOUSEHOLD, ENTERTAINMENT, VEHICLES, CLOTHING, GENERAL }

    final class Occurrences implements Serializable {

        public final Recurring recurring;

        public final long from;

        public final long to;

        @JsonCreator
        public Occurrences(@JsonProperty("recurring") Recurring recurring,
                           @JsonProperty("from") long from,
                           @JsonProperty("to") long to) {

            this.recurring = recurring;
            this.from = from;
            this.to = to;

        }

        @Override
        public String toString() {
            return "Occurrences: {\n" +
                    "\trecurring: " + recurring + "\n" +
                    "\tfrom: " + new Date(from) + "\n" +
                    "\tto: " + new Date(to) + "\n" +
                    "}";
        }

    }

    interface Amount {

        int getAmount();

        long getDate();

        Currency getCurrency();

        Occurrences getOccurrences();

        String label();

    }

    final class Expense implements Amount, Protocol, Serializable {

        public final int amount;

        public final Currency currency;

        public final long date;

        public final Occurrences occurrences;

        public final ExpenseType type;

        public Expense(int amount, long date, Occurrences occurrences, ExpenseType type) {
            this(amount, Currency.EUROS, date, occurrences, type);
        }

        @JsonCreator
        public Expense(@JsonProperty("amount") int amount,
                       @JsonProperty("currency") Currency currency,
                       @JsonProperty("date") long date,
                       @JsonProperty("occurrences") Occurrences occurrences,
                       @JsonProperty("type") ExpenseType type) {

            this.amount = amount;
            this.currency = currency == null ? Currency.EUROS : currency;
            this.date = date;
            this.occurrences = occurrences;
            this.type = type;

        }


        @Override
        public int getAmount() { return amount; }

        @Override
        public Currency getCurrency() { return currency; }

        @Override
        public long getDate() { return date; }

        @Override
        public Occurrences getOccurrences() { return occurrences; }

        @Override
        public String label() { return type.name(); }

        @Override
        public String toString() {
            return "Expense: {\n" +
                    "\tamount: " + amount + " " + currency + "\n" +
                    "\tdate: " + new Date(date) + "\n" +
                    "\toccurrences: " + occurrences + "\n" +
                    "\ttype: " + type + "\n" +
                    "}";
        }

    }

    final class Saving implements Amount, Protocol, Serializable {

        public final int amount;

        public final Currency currency;

        public final long date;

        public final Occurrences occurrences;

        public final String tag;

        public Saving(int amount, long date, Occurrences occurrences, String tag) {
            this(amount, Currency.EUROS, date, occurrences, tag);
        }

        @JsonCreator
        public Saving(@JsonProperty("amount") int amount,
                      @JsonProperty("currency") Currency currency,
                      @JsonProperty("date") long date,
                      @JsonProperty("occurrences") Occurrences occurrences,
                      @JsonProperty("tag") String tag) {

            this.amount = amount;
            this.currency = currency == null ? Currency.EUROS : currency;
            this.date = date;
            this.occurrences = occurrences;
            this.tag = tag;

        }

        @Override
        public int getAmount() { return amount; }

        @Override
        public Currency getCurrency() { return currency; }

        @Override
        public long getDate() { return date; }

        @Override
        public Occurrences getOccurrences() { return occurrences; }

        @Override
        public String label() { return tag; }

        @Override
        public String toString() {
            return "Saving: {\n" +
                    "\tamount: " + amount + " " + currency + "\n" +
                    "\tdate: " + new Date(date) + "\n" +
                    "\toccurrences: " + occurrences + "\n" +
                    "\ttag: " + tag + "\n" +
                    "}";
        }

    }

}
