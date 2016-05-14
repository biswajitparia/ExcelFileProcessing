package biswajit.paria.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Spreadsheet {

	public static void main(String arg[]) {
		if (arg.length == 0) {
			System.out.println(
					"Please provide input file path such as C:\\test\\spreedsheet.txt as command line argument!");
		} else {
			new Spreadsheet().readAndEvaluteExcel(arg[0]);
		}
	}

	private void readAndEvaluteExcel(String path) {
		Map<String, Integer> rowToNumber = new HashMap<>();
		populateRowToNumberMapping(rowToNumber);

		AtomicInteger count = new AtomicInteger(-1);
		AtomicInteger rowCount = new AtomicInteger(0);
		AtomicInteger columnCount = new AtomicInteger(-1);
		SpreadSheetTable spreadSheetTable = new SpreadSheetTable();
		try (Stream<String> stream = Files.lines(Paths.get(path))) {
			stream.forEach(e -> {
				int c = count.incrementAndGet();
				if (c == 0) {
					String splits[] = e.split(" ");
					int row = new Integer(splits[1]);
					int column = new Integer(splits[0]);
					spreadSheetTable.setDimention(column, row);
					System.out.println("INPUT===>" + e + " | OUTPUT===>" + e);
				} else {
					int cc = columnCount.incrementAndGet();
					int rc = rowCount.get();
					if (cc >= spreadSheetTable.getColum()) {
						columnCount.set(0);
						cc = 0;
						rc = rowCount.incrementAndGet();
					}
					spreadSheetTable.setExpValue(e.toString(), rc, cc, rowToNumber);
				}
			});

			evaluteSpreadSheet(rowToNumber, spreadSheetTable);
			String[][] spreedSheetExp = spreadSheetTable.getSpreedSheetExp();
			float[][] spreedSheetVal = spreadSheetTable.getSpreedSheetVal();
			for (int r = 0; r < spreadSheetTable.getRow(); r++) {
				for (int c = 0; c < spreadSheetTable.getColum(); c++) {
					System.out.println("INPUT===>" + spreedSheetExp[r][c] + " | OUTPUT===>"
							+ String.format("%.5f", new Float(spreedSheetVal[r][c])));
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void evaluteSpreadSheet(Map<String, Integer> rowToNumber, SpreadSheetTable spreadSheetTable) {
		String[][] spreedSheetExp = spreadSheetTable.getSpreedSheetExp();
		float[][] spreedSheetVal = spreadSheetTable.getSpreedSheetVal();

		for (int i = 0; i < spreadSheetTable.getRow(); i++) {
			for (int j = 0; j < spreadSheetTable.getColum(); j++) {

				String exp = spreedSheetExp[i][j];
				String expSplits[] = exp.split(" ");

				if (expSplits.length == 1) {
					try {
						new Integer(expSplits[0]);
					} catch (Exception ignored) {
						int row = rowToNumber.get((expSplits[0]).substring(0, 1)) - 1;
						int column = new Integer((expSplits[0]).substring(1, 2)).intValue() - 1;
						spreedSheetVal[i][j] = spreedSheetVal[row][column];
					}
				} else {
					String expressionCell = "";
					boolean evaluated = true;
					for (int m = 0; m < expSplits.length; m++) {
						String el = (expSplits[m]).substring(0, 1);
						if (rowToNumber.get(el) != null) {
							evaluated = false;
							expressionCell += " " + spreedSheetVal[rowToNumber.get(el)
									- 1][new Integer((expSplits[m]).substring(1, 2)).intValue() - 1];
							;
						} else {
							expressionCell += " " + expSplits[m];
						}
					}

					if (!evaluated) {
						spreedSheetVal[i][j] = new Float(spreadSheetTable.getCellVal(expressionCell.trim()));
					}
				}
			}
		}

	}

	private void populateRowToNumberMapping(Map<String, Integer> rowToNumber) {
		rowToNumber.put("A", 1);
		rowToNumber.put("B", 2);
		rowToNumber.put("C", 3);
		rowToNumber.put("D", 4);
		rowToNumber.put("E", 5);
		rowToNumber.put("F", 6);
		rowToNumber.put("G", 7);
		rowToNumber.put("H", 8);

		rowToNumber.put("I", 9);
		rowToNumber.put("J", 10);
		rowToNumber.put("K", 11);
		rowToNumber.put("L", 12);
		rowToNumber.put("M", 13);
		rowToNumber.put("N", 14);
		rowToNumber.put("O", 15);
		rowToNumber.put("P", 16);

		rowToNumber.put("Q", 17);
		rowToNumber.put("R", 18);
		rowToNumber.put("S", 19);
		rowToNumber.put("T", 20);
		rowToNumber.put("U", 21);
		rowToNumber.put("V", 22);
		rowToNumber.put("W", 23);
		rowToNumber.put("X", 24);

		rowToNumber.put("Y", 25);
		rowToNumber.put("Z", 26);
	}

}

class SpreadSheetTable {
	private int row;
	private int colum;
	private String[][] spreedSheetExp;
	private float[][] spreedSheetVal;

	public String[][] getSpreedSheetExp() {
		return spreedSheetExp;
	}

	public float[][] getSpreedSheetVal() {
		return spreedSheetVal;
	}

	public void setDimention(int column, int row) {
		this.spreedSheetExp = new String[row][column];
		this.spreedSheetVal = new float[row][column];
		this.row = row;
		this.colum = column;
	}

	public void setExpValue(String val, int row, int column, Map<String, Integer> rowToNumber) {
		spreedSheetExp[row][column] = val;
		String s[] = val.split(" ");
		if (s.length == 1) {
			try {
				int i = new Integer(s[0]);
				spreedSheetVal[row][column] = i;
			} catch (Exception ignored) {
			}
		} else {
			boolean exp = true;
			for (int k = 0; k < s.length; k++) {
				String el = (s[k]).substring(0, 1);
				if (rowToNumber.get(el) != null) {
					exp = false;
				}
			}
			if (exp)
				spreedSheetVal[row][column] = new Float(getCellVal(val)).floatValue();
		}
	}

	public String getCellVal(String cellExpression) {

		String cellExp[] = cellExpression.split(" ");
		List<String> cellVal = new ArrayList<>();
		for (String cellExpVal : cellExp) {
			cellVal.add(cellExpVal);
		}

		int indexofOperator = 0;
		while (cellVal.size() > 1) {
			if (cellVal.get(indexofOperator).equals("+")) {
				cellVal.set(indexofOperator - 2, String.valueOf(
						new Float(cellVal.get(indexofOperator - 2)) + new Float(cellVal.get(indexofOperator - 1))));
				cellVal.remove(indexofOperator - 1);
				cellVal.remove(indexofOperator - 1);
				indexofOperator = 0;
				continue;
			} else if (cellVal.get(indexofOperator).equals("-")) {
				cellVal.set(indexofOperator - 2, String.valueOf(
						new Float(cellVal.get(indexofOperator - 2)) - new Float(cellVal.get(indexofOperator - 1))));
				cellVal.remove(indexofOperator - 1);
				cellVal.remove(indexofOperator - 1);
				indexofOperator = 0;
				continue;
			} else if (cellVal.get(indexofOperator).equals("*")) {
				cellVal.set(indexofOperator - 2, String.valueOf(
						new Float(cellVal.get(indexofOperator - 2)) * new Float(cellVal.get(indexofOperator - 1))));
				cellVal.remove(indexofOperator - 1);
				cellVal.remove(indexofOperator - 1);
				indexofOperator = 0;
				continue;
			} else if (cellVal.get(indexofOperator).equals("/")) {
				cellVal.set(indexofOperator - 2, String.valueOf(
						new Float(cellVal.get(indexofOperator - 2)) / new Float(cellVal.get(indexofOperator - 1))));
				cellVal.remove(indexofOperator - 1);
				cellVal.remove(indexofOperator - 1);
				indexofOperator = 0;
				continue;
			} else if (cellVal.get(indexofOperator).equals("++")) {
				cellVal.set(indexofOperator - 1,
						String.valueOf(new Float(cellVal.get(indexofOperator - 1)).floatValue() + 1));
				cellVal.remove(indexofOperator);
				indexofOperator = 0;
				continue;
			} else if (cellVal.get(indexofOperator).equals("--")) {
				cellVal.set(indexofOperator - 1,
						String.valueOf(new Float(cellVal.get(indexofOperator - 1)).floatValue() - 1));
				cellVal.remove(indexofOperator);
				indexofOperator = 0;
				continue;
			}

			indexofOperator++;
		}

		return cellVal.get(0);
	}

	public int getRow() {
		return row;
	}

	public int getColum() {
		return colum;
	}

}
