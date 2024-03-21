package com.exporter.excel;

import com.exporter.listener.ExporterCallback;
import com.exporter.model.ExcelFileExtension;
import com.exporter.model.ExporterData;
import com.exporter.task.AsyncThread;
import com.exporter.util.ExFileUtils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExportExcelTask extends AsyncThread<Void, Void, File> {

    private final File directory;
    private final String fileName;
    private final ExporterData excelData;
    private final ExporterCallback<File> listener;
    private String errorMessage = "";

    public ExportExcelTask(File directory, String fileName, ExporterData excelData, ExporterCallback<File> listener) {
//        this.fileName = ExFileUtils.getValidFileName(fileName, ExportExcel.EXTENSION);
        this.fileName = ExFileUtils.getValidFileName(fileName, "." + excelData.getExcelFileExtension());
        this.excelData = excelData;
        this.directory = directory;
        this.listener = listener;
    }

    @Override
    protected File doInBackground(Void... voids) {
        try {
            return writeXLSXFile();
//            return exportDataInExcelFile();
        } catch (IOException e) {
            e.printStackTrace();
            this.errorMessage = e.getMessage();
            return null;
        }
    }

    @Override
    protected void onPostExecute(File result) {
        super.onPostExecute(result);
        if (result != null) {
            listener.onSuccess(result);
        } else {
            listener.onFailure(new Exception("Error:101 " + errorMessage));
        }
    }


    private File exportDataInExcelFile() throws IOException {
        Workbook wb = excelData.getExcelFileExtension().equals(ExcelFileExtension.XLSX) ? new XSSFWorkbook() : new HSSFWorkbook();
        Cell cell = null;
        CellStyle cs = null;
        Sheet sheet1 = wb.createSheet(fileName);

        // Generate column headings
        for (int i = 0; i < excelData.getExcelArray().size(); i++) {
            Row row = sheet1.createRow(i);
            List<String> rowList = excelData.getExcelArray().get(i);
            for (int j = 0; j < rowList.size(); j++) {
                if (i == 0) {
                    if (j == 0) {
                        if(excelData.isEnableColumnSerial()) {//serialNo
                            createHeaderRowColumn(sheet1, cell, row, cs, 100, j, "S.No.");
                        }
                        createHeaderRowColumn(sheet1, cell, row, cs, 100, j + 1, rowList.get(j));
                    } else {
                        createHeaderRowColumn(sheet1, cell, row, cs, 100, j + 1, rowList.get(j));
                    }
                } else {
                    if (j == 0) {
                        if(excelData.isEnableColumnSerial()) {
                            setRowEntry(cell, row, j, String.valueOf(i));//serialNo
                        }
                        setRowEntry(cell, row, j + 1, rowList.get(j));
                    } else {
                        setRowEntry(cell, row, j + 1, rowList.get(j));
                    }
                }
            }

        }

        if (!directory.exists()) {
            boolean status = directory.mkdirs();
        }
        File myExternalFile = new File(directory, fileName);
        FileOutputStream fileOut = new FileOutputStream(myExternalFile);
        wb.write(fileOut);
        fileOut.close();
        return myExternalFile;
    }

    public File writeXLSXFile() throws IOException {


        String sheetName = "Sheet1";//name of sheet

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName) ;

        //iterating r number of rows
        for (int r=0;r < 5; r++ )
        {
            XSSFRow row = sheet.createRow(r);

            //iterating c number of columns
            for (int c=0;c < 5; c++ )
            {
                XSSFCell cell = row.createCell(c);

                cell.setCellValue("Cell "+r+" "+c);
            }
        }
        if (!directory.exists()) {
            boolean status = directory.mkdirs();
        }
        File myExternalFile = new File(directory, fileName);

        FileOutputStream fileOut = new FileOutputStream(myExternalFile);

        //write this workbook to an Outputstream.
        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
        return myExternalFile;
    }


    private Cell createHeaderRowColumn(Sheet sheet1, Cell cell, Row row, CellStyle cs, int width, int columnPosition, String columnName) {
        cell = row.createCell(columnPosition);
        cell.setCellValue(columnName);
//        cell.setCellStyle(cs);
        sheet1.setColumnWidth(columnPosition, (15 * width));
        return cell;
    }

    private Cell setRowEntry(Cell cell, Row multiRow, int listPos, String listValue) {
        cell = multiRow.createCell(listPos);
        cell.setCellValue(listValue);
        return cell;
    }

    private Cell setRowEntry(Cell cell, Row multiRow, int listPos, String label, String[] data) {
        cell = multiRow.createCell(listPos);
        cell.setCellValue(label);
        int columnIndex = 0;
        for (String item : data) {
            cell = multiRow.createCell(columnIndex++);
            cell.setCellValue(item);
        }
        return cell;
    }

//    private static void readExcelFile(Context context, String filename) {
//
//        try {
//            // Creating Input Stream
//            File file = new File(context.getExternalFilesDir(null), filename);
//            FileInputStream myInput = new FileInputStream(file);
//
//            // Create a POIFSFileSystem object
//            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
//
//            // Create a workbook using the File System
//            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
//
//            // Get the first sheet from workbook
//            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
//
//            /** We now need something to iterate through the cells.**/
//            Iterator<Row> rowIter = mySheet.rowIterator();
//
//            while (rowIter.hasNext()) {
//                HSSFRow myRow = (HSSFRow) rowIter.next();
//                Iterator<Cell> cellIter = myRow.cellIterator();
//                while (cellIter.hasNext()) {
//                    HSSFCell myCell = (HSSFCell) cellIter.next();
//                    Log.w("FileUtils", "Cell Value: " + myCell.toString());
//                    Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return;
//    }


}
