package com.odc.pdfextractor.integration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.odc.pdfextractor.model.Transaction;
import com.odc.pdfextractor.model.Transaction.TransactionType;
import com.odc.pdfextractor.model.builder.TransactionBuilder;

public class BankStatementTest {
	
	private Map<TransactionType, Integer> transTypeCount;
	
	@Before
	public void setup() {
		transTypeCount = new HashMap<TransactionType, Integer>();
		transTypeCount.put(TransactionType.DEBIT, new Integer(0));
		transTypeCount.put(TransactionType.CREDIT, new Integer(0));
		transTypeCount.put(TransactionType.CHECK, new Integer(0));
		transTypeCount.put(TransactionType.BALANCE, new Integer(0));
		transTypeCount.put(TransactionType.UNKNOWN, new Integer(0));

	}
	
	@Test
	public void BankOfAmericaTest() throws IOException, Exception {
		Map<TransactionType, Integer> transTypeExpected = new HashMap<TransactionType, Integer>();
		transTypeExpected.put(TransactionType.DEBIT, new Integer(304));
		transTypeExpected.put(TransactionType.CREDIT, new Integer(280));
		transTypeExpected.put(TransactionType.CHECK, new Integer(114));
		transTypeExpected.put(TransactionType.BALANCE, new Integer(85));
		transTypeExpected.put(TransactionType.UNKNOWN, new Integer(0));
		runTest("bofa_all_pages.pdf", 783, 1802, 176940, transTypeExpected);
	}
	
	@Test
	public void TDBanknorthTest() throws IOException, Exception {
		Map<TransactionType, Integer> transTypeExpected = new HashMap<TransactionType, Integer>();
		transTypeExpected.put(TransactionType.DEBIT, new Integer(60));
		transTypeExpected.put(TransactionType.CREDIT, new Integer(34));
		transTypeExpected.put(TransactionType.CHECK, new Integer(50));
		transTypeExpected.put(TransactionType.BALANCE, new Integer(22));
		transTypeExpected.put(TransactionType.UNKNOWN, new Integer(0));
		runTest("TD_BANKNORTH_ALL_PAGES.pdf", 166, 3463, 794935, transTypeExpected);
	}
	
	@Test
	public void capitalOneTest() throws IOException, Exception {
		Map<TransactionType, Integer> transTypeExpected = new HashMap<TransactionType, Integer>();
		transTypeExpected.put(TransactionType.DEBIT, new Integer(31));
		transTypeExpected.put(TransactionType.CREDIT, new Integer(14));
		transTypeExpected.put(TransactionType.CHECK, new Integer(11));
		transTypeExpected.put(TransactionType.BALANCE, new Integer(0));
		transTypeExpected.put(TransactionType.UNKNOWN, new Integer(0));
		runTest("CAPITAL_ONE_ALL_PAGES.pdf", 56, -5528, 81875, transTypeExpected);
	}
	
	@Test
	public void capital1Test() throws IOException, Exception {
		Map<TransactionType, Integer> transTypeExpected = new HashMap<TransactionType, Integer>();
		transTypeExpected.put(TransactionType.DEBIT, new Integer(233));
		transTypeExpected.put(TransactionType.CREDIT, new Integer(147));
		transTypeExpected.put(TransactionType.CHECK, new Integer(56));
		transTypeExpected.put(TransactionType.BALANCE, new Integer(0));
		transTypeExpected.put(TransactionType.UNKNOWN, new Integer(0));
		runTest("CAPITAL_ONE_ALL_PAGES_1.pdf", 436, -245627, 6352997, transTypeExpected);
	}
	
	@Test
	public void USBankTest() throws IOException, Exception {
		Map<TransactionType, Integer> transTypeExpected = new HashMap<TransactionType, Integer>();
		transTypeExpected.put(TransactionType.DEBIT, new Integer(278));
		transTypeExpected.put(TransactionType.CREDIT, new Integer(44));
		transTypeExpected.put(TransactionType.CHECK, new Integer(170));
		transTypeExpected.put(TransactionType.BALANCE, new Integer(64));
		transTypeExpected.put(TransactionType.UNKNOWN, new Integer(0));
		runTest("US_BANK_ALL_PAGES.pdf", 556, 66357, 3365704, transTypeExpected);
	}
	
	@Test
	public void USBank1Test() throws IOException, Exception {
		Map<TransactionType, Integer> transTypeExpected = new HashMap<TransactionType, Integer>();
		transTypeExpected.put(TransactionType.DEBIT, new Integer(132));
		transTypeExpected.put(TransactionType.CREDIT, new Integer(349));
		transTypeExpected.put(TransactionType.CHECK, new Integer(227));
		transTypeExpected.put(TransactionType.BALANCE, new Integer(62));
		transTypeExpected.put(TransactionType.UNKNOWN, new Integer(0));
		runTest("US_BANK_ALL_PAGES_1.pdf", 770, 1960, 193570, transTypeExpected);
	}

	private void runTest(String filename, int transactionsExpected, int amountExpected, int balanceExpected, Map<TransactionType, Integer> transTypeExpected) throws IOException, Exception {
		TransactionBuilder builder = new TransactionBuilder(filename);
		List<Transaction> transactions = builder.getTransactionList();

		int amountCount = 0;
		int balanceCount = 0;
		for (Transaction t : transactions) {
			if (t.getAmount() != null) {
				amountCount += Math.round(t.getAmount());
			}
			if (t.getResultingBalance() != null) {
				balanceCount += Math.round(t.getResultingBalance());
			}
			transTypeCount.put(t.getType(), transTypeCount.get(t.getType()) + 1);
		}
		System.out.println(transactions.size());
		System.out.println(amountCount);
		System.out.println(balanceCount);
		System.out.println(transTypeCount);
		assertEquals(transactions.size(), transactionsExpected);
		assertEquals(amountCount, amountExpected);
		assertEquals(balanceCount, balanceExpected);
		assertEquals(transTypeCount, transTypeExpected);

	}
	
	

}
