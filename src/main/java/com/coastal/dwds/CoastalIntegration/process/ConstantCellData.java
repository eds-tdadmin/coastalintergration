package com.coastal.dwds.CoastalIntegration.process;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.coastal.dwds.CoastalIntegration.constant.GlobalCellsConstants;

/**
 * @author Madhan
 *
 */
public class ConstantCellData {
	/**
	 * method for iterate the row and cell values into map
	 * 
	 * @param mySheet
	 * @param keysSheet
	 * @param workbook
	 * @param startRow
	 * @param endRow
	 * @return
	 */
	public Map<String, String> addGeneralConstantCells(Sheet mySheet, int startRow, int endRow, Map<String, String> generalMap) {
		for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
			Row row = mySheet.getRow(rowNum);
			if (rowNum > 2) {
				if (row.getCell(2) != null) {
					generalMap.put(row.getCell(0).toString().trim(), row.getCell(2).toString().trim());
				}
				if (row.getCell(5) != null) {
					generalMap.put(row.getCell(4).toString().trim(), row.getCell(5).toString().trim());
				}
				if (row.getCell(9) != null) {
					generalMap.put(row.getCell(7).toString().trim(), row.getCell(9).toString().trim());
				}
				if (row.getCell(13) != null) {
					generalMap.put(row.getCell(11).toString().trim(), row.getCell(13).toString().trim());
				}
				if (row.getCell(15) != null) {
					generalMap.put(row.getCell(14).toString().trim(), row.getCell(15).toString().trim());
				}
			}
		}
		return generalMap;
	}

	/**
	 * method for iterate the row and cell values into map
	 * 
	 * @param mySheet
	 * @param keysSheet
	 * @param workbook
	 * @param startRow
	 * @param endRow
	 * @param generalMap
	 * @param fileName
	 * @return
	 */
	public Map<String, String> addDepthConstantCells(Sheet mySheet, int startRow, int endRow, Map<String, String> generalMap) {
		for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
			Row row = mySheet.getRow(rowNum);
			if (row.getCell(2) != null) {
				generalMap.put(row.getCell(0).toString().trim(), row.getCell(2).toString().trim());
			}
			if (row.getCell(6) != null) {
				generalMap.put(row.getCell(4).toString().trim(), row.getCell(6).toString().trim());
			}
			if (row.getCell(10) != null) {
				generalMap.put(row.getCell(7).toString().trim(), row.getCell(10).toString().trim());
			}
			if (row.getCell(13) != null) {
				generalMap.put(row.getCell(11).toString().trim(), row.getCell(13).toString().trim());
			}
			if (row.getCell(16) != null) {
				generalMap.put(row.getCell(14).toString().trim(), row.getCell(16).toString().trim());
			}
		}
		return generalMap;
	}

	/**
	 * @param mySheet
	 * @param keysSheet
	 * @param workbook
	 * @param startRow
	 * @param endRow
	 * @param generalMap
	 * @param fileName
	 * @return
	 */
	public Map<String, String> addStatusConstantCells(Sheet mySheet, int startRow, int endRow, Map<String, String> generalMap) {
		for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
			Row row = mySheet.getRow(rowNum);
			if (row.getCell(2) != null) {
				generalMap.put(row.getCell(0).toString().trim(), row.getCell(2).toString().trim());
			}
		}
		return generalMap;
	}

	/**
	 * @param mySheet
	 * @param keysSheet
	 * @param startRow
	 * @param endRow
	 * @param operationSummary
	 * @param fileName
	 * @return
	 */
	public Map<String, String> DynamicOperationDataCells(Sheet mySheet, int startRow, int endRow) {
		Map<String, String> dynamicBlankFieldMap = new HashMap<String, String>();
		for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
			Row row = mySheet.getRow(rowNum);
			if (row.getCell(0) != null || row.getCell(1) != null || row.getCell(2) != null || row.getCell(3) != null
					|| row.getCell(4) != null || row.getCell(5) != null || row.getCell(7) != null || row.getCell(8) != null) {
				if (row.getCell(0) != null) {
					if (row.getCell(0).toString().length() > 0) {
						dynamicBlankFieldMap.put(GlobalCellsConstants.TIME_FROM + rowNum, row.getCell(0).toString().trim());
					}
				}
				if (row.getCell(1) != null) {
					if (row.getCell(1).toString().length() > 0) {
						dynamicBlankFieldMap.put(GlobalCellsConstants.TIME_TO + rowNum, row.getCell(1).toString().trim());
					}
				}
				if (row.getCell(2) != null) {
					dynamicBlankFieldMap.put(GlobalCellsConstants.TIME_HRS + rowNum, row.getCell(2).toString().trim());
				}
				if (row.getCell(3) != null) {
					dynamicBlankFieldMap.put(GlobalCellsConstants.PHASE + rowNum, row.getCell(3).toString().trim());
				}
				if (row.getCell(4) != null) {
					dynamicBlankFieldMap.put(GlobalCellsConstants.CODE_OPRN + rowNum, row.getCell(4).toString().trim());
				}
				if (row.getCell(5) != null) {
					dynamicBlankFieldMap.put(GlobalCellsConstants.SUBCODE_GROUP + rowNum, row.getCell(5).toString().trim());
				}
				if (row.getCell(7) != null) {
					dynamicBlankFieldMap.put(GlobalCellsConstants.MD_FROM + rowNum, row.getCell(7).toString().trim());
				}
				if (row.getCell(8) != null) {
					dynamicBlankFieldMap.put(GlobalCellsConstants.OPERATION_DESC + rowNum, row.getCell(8).toString().trim());
				}
			}
		}
		return dynamicBlankFieldMap;
	}

	/**
	 * @param mySheet
	 * @param keysSheet
	 * @param startRow
	 * @param endRow
	 * @param nptSummary
	 * @param fileName
	 * @return
	 */
	public Map<String, String> DynamicNPTDataCells(Sheet mySheet, int startRow, int endRow, Map<String, String> generalMap) {
		for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
			Row row = mySheet.getRow(rowNum);
			if (row.getCell(0) != null || row.getCell(1) != null || row.getCell(2) != null || row.getCell(3) != null
					|| row.getCell(5) != null || row.getCell(7) != null || row.getCell(9) != null || row.getCell(11) != null
					|| row.getCell(16) != null) {
				if (row.getCell(0) != null) {
					if (row.getCell(0).toString().length() > 0) {
						generalMap.put(GlobalCellsConstants.NPT_TIME_FROM + rowNum, row.getCell(0).toString().trim());
					}
				}
				if (row.getCell(1) != null) {
					if (row.getCell(1).toString().length() > 0) {
						generalMap.put(GlobalCellsConstants.NPT_TIME_TO + rowNum, row.getCell(1).toString().trim());
					}
				}
				if (row.getCell(2) != null) {
					generalMap.put(GlobalCellsConstants.NPT_TIME_HRS + rowNum, row.getCell(2).toString().trim());
				}
				if (row.getCell(3) != null) {
					generalMap.put(GlobalCellsConstants.NPT_CATEGORY + rowNum, row.getCell(3).toString().trim());
				}
				if (row.getCell(5) != null) {
					generalMap.put(GlobalCellsConstants.NPT_TYPE + rowNum, row.getCell(5).toString().trim());
				}
				if (row.getCell(7) != null) {
					generalMap.put(GlobalCellsConstants.MAIN_VENDOR + rowNum, row.getCell(7).toString().trim());
				}
				if (row.getCell(9) != null) {
					generalMap.put(GlobalCellsConstants.SUB_VENDOR + rowNum, row.getCell(9).toString().trim());
				}
				if (row.getCell(11) != null) {
					generalMap.put(GlobalCellsConstants.NPT_DESC + rowNum, row.getCell(11).toString().trim());
				}
				if (row.getCell(16) != null) {
					generalMap.put(GlobalCellsConstants.NET_COST_USD + rowNum, row.getCell(16).toString().trim());
				}
			}
		}
		return generalMap;
	}

	/**
	 * @param mySheet
	 * @param keysSheet
	 * @param startRow
	 * @param endRow
	 * @param casingSummary
	 * @param casingAssemblySummary
	 */
	public Map<String, String> DynamicCasingInfoCells(Sheet mySheet, int startRow, int endRow, Map<String, String> generalMap) {
		for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
			Row row = mySheet.getRow(rowNum);

			if (row.getCell(0) != null || row.getCell(3) != null || row.getCell(4) != null || row.getCell(5) != null
					|| row.getCell(6) != null || row.getCell(8) != null || row.getCell(9) != null || row.getCell(10) != null
					|| row.getCell(11) != null || row.getCell(12) != null || row.getCell(14) != null) {

				if (row.getCell(0) != null) {
					generalMap.put(GlobalCellsConstants.CASING_NAME + rowNum, row.getCell(0).toString().trim());
				}
				if (row.getCell(3) != null) {
					generalMap.put(GlobalCellsConstants.OD + rowNum, row.getCell(3).toString().trim());
				}
				if (row.getCell(4) != null) {
					generalMap.put(GlobalCellsConstants.WEIGHT + rowNum, row.getCell(4).toString().trim());
				}
				if (row.getCell(5) != null) {
					generalMap.put(GlobalCellsConstants.GRADE + rowNum, row.getCell(5).toString().trim());
				}
				if (row.getCell(6) != null) {
					generalMap.put(GlobalCellsConstants.CONNECTION + rowNum, row.getCell(6).toString().trim());
				}
				if (row.getCell(8) != null) {
					generalMap.put(GlobalCellsConstants.TOP_MD + rowNum, row.getCell(8).toString().trim());
				}
				if (row.getCell(9) != null) {
					generalMap.put(GlobalCellsConstants.BOTTOM_MD + rowNum, row.getCell(9).toString().trim());
				}
				if (row.getCell(10) != null) {
					generalMap.put(GlobalCellsConstants.TOP_TVD + rowNum, row.getCell(10).toString().trim());
				}
				if (row.getCell(11) != null) {
					generalMap.put(GlobalCellsConstants.BOTTOM_TVD + rowNum, row.getCell(11).toString().trim());
				}
				if (row.getCell(12) != null) {
					generalMap.put(GlobalCellsConstants.CONDITION + rowNum, row.getCell(12).toString().trim());
				}
				if (row.getCell(14) != null) {
					generalMap.put(GlobalCellsConstants.SHOE_TEST + rowNum, row.getCell(14).toString().trim());
				}
			}
		}
		return generalMap;
	}
}
