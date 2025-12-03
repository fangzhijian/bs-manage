package com.springboot.demo.until;

import com.springboot.demo.exception.BusinessException;
import com.springboot.demo.model.json.ExcelData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 2018/12/24 19:33
 */
@Slf4j
public class ExcelUtils {

    /**
     * 读取excel
     *
     * @param file excel文件
     * @return excel第一个sheet内容
     */
    public static Sheet getSheet(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$"))) {
            throw new BusinessException("上传文件格式不正确");
        }
        boolean isExcel2003 = true;
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            isExcel2003 = false;
        }
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook;
            if (isExcel2003) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                workbook = new XSSFWorkbook(inputStream);
            }
            Sheet sheet = workbook.getSheetAt(0);
            int totalRow = sheet.getLastRowNum();
            log.info("总行数:{},开始导入数据到数据库", totalRow);
            return sheet;
        } catch (IOException e) {
            throw new BusinessException("excel读取失败");
        }
    }

    /**
     * 使用浏览器选择路径下载
     */
    public static void exportExcel(HttpServletResponse response, String fileName, ExcelData data) {
        try {
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", "utf-8"));
            exportExcel(data, response.getOutputStream());
        } catch (Exception e) {
            log.error("导出excel失败");
            log.error(e.getMessage(), e);
        }
    }

    public static void exportZip(HttpServletResponse response, String zipName, List<ExcelData> excelDataList, String path) {
        try {
            List<File> files = new ArrayList<>();
            String directoryPath = path.concat("/excel");
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            //生成excel
            for (ExcelData excelData : excelDataList) {
                File file = new File(directoryPath, excelData.getExcelName().concat(".xlsx"));
                exportExcel(excelData, new FileOutputStream(file));
                files.add(file);
            }
            File zip = new File(directoryPath, zipName.concat(".zip"));
            FileUtil.zipFiles(files, zip);
            response.setContentType("application/zip");
            response.setHeader("Location", zip.getName());
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(zipName.concat(".zip"), "utf-8"));
            OutputStream outputStream = response.getOutputStream();
            InputStream inputStream = new FileInputStream(zip);
            byte[] buffer = new byte[1024];
            int i;
            while ((i = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, i);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            //删除文件
            for (File file : files) {
                file.delete();
            }
            zip.delete();
        } catch (Exception e) {
            log.error("导出zip失败");
            log.error(e.getMessage(), e);
        }

    }


    public static void exportExcel(ExcelData data, OutputStream out) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook();
        try {
            String sheetName = data.getSheetName();
            if (null == sheetName) {
                sheetName = "Sheet1";
            }
            XSSFSheet sheet = wb.createSheet(sheetName);
            writeExcel(wb, sheet, data);
            wb.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //此处需要关闭 wb 变量
            out.close();
        }
    }


    /**
     * 表显示字段
     */
    private static void writeExcel(XSSFWorkbook wb, Sheet sheet, ExcelData data) {
        writeTitlesToExcel(wb, sheet, data.getColumnNames());
        writeRowsToExcel(wb, sheet, data.getRows());
        autoSizeColumns(sheet, data.getColumnNames().length + 1);
    }

    /**
     * 设置表头
     */
    private static void writeTitlesToExcel(XSSFWorkbook wb, Sheet sheet, String[] titles) {
        Font titleFont = wb.createFont();
        //设置字体
        titleFont.setFontName("simsun");
        //设置粗体
        titleFont.setBoldweight(Short.MAX_VALUE);
        //设置字号
        titleFont.setFontHeightInPoints((short) 14);
        //设置颜色
        titleFont.setColor(IndexedColors.BLACK.index);
        XSSFCellStyle titleStyle = wb.createCellStyle();
        //水平居中
        titleStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        //垂直居中
        titleStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        //设置图案颜色
        titleStyle.setFillForegroundColor(new XSSFColor(new Color(182, 184, 192)));
        //设置图案样式
        titleStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        titleStyle.setFont(titleFont);
        setBorder(titleStyle, new XSSFColor(new Color(0, 0, 0)));
        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(25);
        int colIndex = 0;
        for (String field : titles) {
            Cell cell = titleRow.createCell(colIndex);
            cell.setCellValue(field);
            cell.setCellStyle(titleStyle);
            colIndex++;
        }
    }

    /**
     * 设置内容
     */
    private static void writeRowsToExcel(XSSFWorkbook wb, Sheet sheet, List<List<Object>> rows) {
        int colIndex;
        Font dataFont = wb.createFont();
        dataFont.setFontName("simsun");
        dataFont.setFontHeightInPoints((short) 14);
        dataFont.setColor(IndexedColors.BLACK.index);

        XSSFCellStyle dataStyle = wb.createCellStyle();
        dataStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        dataStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        dataStyle.setFont(dataFont);
        setBorder(dataStyle, new XSSFColor(new Color(0, 0, 0)));
        int rowIndex = 1;
        for (List<Object> rowData : rows) {
            Row dataRow = sheet.createRow(rowIndex);
            dataRow.setHeightInPoints(25);
            colIndex = 0;
            for (Object cellData : rowData) {
                Cell cell = dataRow.createCell(colIndex);
                if (cellData != null) {
                    cell.setCellValue(cellData.toString());
                } else {
                    cell.setCellValue("");
                }
                cell.setCellStyle(dataStyle);
                colIndex++;
            }
            rowIndex++;
        }
    }

    /**
     * 自动调整列宽
     */
    private static void autoSizeColumns(Sheet sheet, int columnNumber) {
        for (int i = 0; i < columnNumber; i++) {
            int orgWidth = sheet.getColumnWidth(i);
            sheet.autoSizeColumn(i, true);
            int colWidth = (sheet.getColumnWidth(i) + 100);
            if (colWidth < 255 * 256) {
                sheet.setColumnWidth(i, Math.max(colWidth, 3000));
            } else {
                sheet.setColumnWidth(i, 6000);
            }

        }
    }

    /**
     * 设置边框
     */
    private static void setBorder(XSSFCellStyle style, XSSFColor color) {
        BorderStyle border = BorderStyle.THIN;
        style.setBorderTop(border);
        style.setBorderLeft(border);
        style.setBorderRight(border);
        style.setBorderBottom(border);
        style.setBorderColor(XSSFCellBorder.BorderSide.TOP, color);
        style.setBorderColor(XSSFCellBorder.BorderSide.LEFT, color);
        style.setBorderColor(XSSFCellBorder.BorderSide.RIGHT, color);
        style.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM, color);
    }

    /**
     * 设置String类型
     *
     * @param cell 单元格
     * @return String值
     */
    public static String getString(Cell cell) {
        if (cell != null) {
            if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
                return cell.getStringCellValue();
            } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                //数字长时需要这样写
                return String.valueOf(Double.valueOf(cell.getNumericCellValue()).longValue());
            } else if (Cell.CELL_TYPE_BLANK == cell.getCellType()) {
                return null;
            } else if (Cell.CELL_TYPE_FORMULA == cell.getCellType()) {
//                return null;
                return cell.getCellFormula().replace("\"", "");
            } else {
                System.out.println(cell.getCellType());
                throw new BusinessException("excel单元格格式不是数字也不是文本");
            }
        } else {
            return null;
        }
    }

    /**
     * 设置Integer类型
     *
     * @param cell 单元格
     * @return Integer值
     */
    public static Integer getInteger(Cell cell) {
        if (cell != null) {
            if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
                if (!StringUtils.hasText(cell.getStringCellValue())) {
                    return null;
                }
                return Integer.valueOf(cell.getStringCellValue());
            } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                return Double.valueOf(cell.getNumericCellValue()).intValue();
            } else if (Cell.CELL_TYPE_BLANK == cell.getCellType()) {
                return null;
            } else {
                throw new BusinessException("excel单元格格式不是数字也不是文本");
            }
        } else {
            return null;
        }
    }

    /**
     * 设置Long类型
     *
     * @param cell 单元格
     * @return Long值
     */
    public static Long getLong(Cell cell) {
        if (cell != null) {
            if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
                if (!StringUtils.hasText(cell.getStringCellValue())) {
                    return null;
                }
                return Long.valueOf(cell.getStringCellValue());
            } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                return Double.valueOf(cell.getNumericCellValue()).longValue();
            } else if (Cell.CELL_TYPE_BLANK == cell.getCellType()) {
                return null;
            } else {
                throw new BusinessException("excel单元格格式不是数字也不是文本");
            }
        } else {
            return null;
        }
    }

    /**
     * 设置Double类型
     *
     * @param cell 单元格
     * @return Long值
     */
    public static Double getDouble(Cell cell) {
        if (cell != null) {
            if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
                if (!StringUtils.hasText(cell.getStringCellValue())) {
                    return null;
                }
                return Double.valueOf(cell.getStringCellValue());
            } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                return cell.getNumericCellValue();
            } else if (Cell.CELL_TYPE_BLANK == cell.getCellType()) {
                return null;
            } else {
                throw new BusinessException("excel单元格格式不是数字也不是文本");
            }
        } else {
            return null;
        }
    }

    /**
     * 设置Float类型
     *
     * @param cell 单元格
     * @return Long值
     */
    public static Float getFloat(Cell cell) {
        if (cell != null) {
            if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
                if (!StringUtils.hasText(cell.getStringCellValue())) {
                    return null;
                }
                return Float.valueOf(cell.getStringCellValue());
            } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                return Double.valueOf(cell.getNumericCellValue()).floatValue();
            } else if (Cell.CELL_TYPE_BLANK == cell.getCellType()) {
                return null;
            } else {
                throw new BusinessException("excel单元格格式不是数字也不是文本");
            }
        } else {
            return null;
        }
    }

    /**
     * 设置BigDecimal类型
     *
     * @param cell 单元格
     * @return BigDecimal值
     */
    public static BigDecimal getBigDecimal(Cell cell) {
        if (cell != null) {
            if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
                if (!StringUtils.hasText(cell.getStringCellValue())) {
                    return null;
                }
                return new BigDecimal(cell.getStringCellValue()).setScale(4, BigDecimal.ROUND_HALF_EVEN);
            } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                return BigDecimal.valueOf(cell.getNumericCellValue()).setScale(4, BigDecimal.ROUND_HALF_EVEN);
            } else if (Cell.CELL_TYPE_BLANK == cell.getCellType()) {
                return BigDecimal.ZERO;
            } else if (Cell.CELL_TYPE_FORMULA == cell.getCellType()) {
                return new BigDecimal(cell.getCellFormula().replace("\"", ""));
            } else {
                System.out.println(cell.getCellType());
                throw new BusinessException("excel单元格格式不是数字也不是文本");
            }
        } else {
            return BigDecimal.ZERO;
        }
    }


}
