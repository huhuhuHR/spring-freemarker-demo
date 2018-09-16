//package com.huo.utils;////import com.huo.annotation.ExcelColumn;//import com.huo.annotation.ExcelSheet;//import org.apache.commons.lang3.StringUtils;//import org.apache.poi.hssf.usermodel.HSSFWorkbook;//import org.apache.poi.ss.usermodel.Cell;//import org.apache.poi.ss.usermodel.Row;//import org.apache.poi.ss.usermodel.Sheet;//import org.apache.poi.ss.usermodel.Workbook;//import org.apache.poi.xssf.usermodel.*;//import org.springframework.web.multipart.MultipartFile;////import javax.servlet.http.HttpServletResponse;//import java.io.IOException;//import java.io.InputStream;//import java.io.OutputStream;//import java.io.UnsupportedEncodingException;//import java.lang.reflect.Field;//import java.net.URLEncoder;//import java.util.*;//////public class ExcelUtil {//    private static final Logger LOGGER = Logger.getLogger(ExcelUtil.class);//    /**//     * Default ArrayList initial capacity.//     *///    public static final int DEFAULT_CAPACITY = 10;//    public static final int INT_1 = 1;//    public static final int INT_0 = 0;////    /**//     * excel内容读取，兼容2003和2007//     * <p>//     * excel内容读取仅限第一个sheet页//     *//     * @param file//     * @return//     * @throws Exception//     *///    public static List<String[]> readExcel(MultipartFile file) {//        // java1.7之后try-catch可以自动关闭资源//        // 获得Workbook工作薄对象//        Workbook workbook = getWorkBook(file);//        // 创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回//        List<String[]> list = new ArrayList<String[]>();//        if (workbook != null) {//            // 第一个sheet页//            Sheet sheet = workbook.getSheetAt(0);//            // 第一个有记录的行，作为头行，通常是标题行//            // 获得当前sheet的开始行//            int firstRowNum = sheet.getFirstRowNum();//            // 获得当前sheet的结束行//            int lastRowNum = sheet.getLastRowNum();//            // 行数//            int totalRowNum = lastRowNum - firstRowNum + 1;//            // 减少大数量级别数据，arraylist频繁扩容带来的问题//            int initialCapacity = totalRowNum - 1;//            if (initialCapacity > DEFAULT_CAPACITY) {//                // initialCapacity - 1 这个1是标题行不录入//                list = new ArrayList<String[]>(initialCapacity);//            }//            Row canReadFirstRow = sheet.getRow(firstRowNum);//            int firstClomn = canReadFirstRow.getFirstCellNum();//            int lastClomn = canReadFirstRow.getLastCellNum();//            int columnNum = lastClomn - firstClomn;//            if (columnNum == 0) {//                return list;//            }//            // 第一行是标题行//            for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {//                // 获得当前行//                Row row = sheet.getRow(rowNum);//                if (row == null) {//                    continue;//                }//                // excel一行数据写入String[]//                String[] cells = readRow(columnNum, row, firstClomn);//                list.add(cells);//            }//        }//        return list;//    }////    /**//     * 读一行//     *//     * @param columnNum//     * @param row//     * @param firstCellNum//     * @return//     *///    private static String[] readRow(int columnNum, Row row, int firstCellNum) {//        String[] cells = new String[columnNum];//        int lastCellNum = firstCellNum + columnNum;//        // 循环当前行//        for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {//            Cell cell = row.getCell(cellNum);//            cells[cellNum] = getCellValue(cell);//        }//        return cells;//    }////    /**//     * 根据第一行，通常是标题行确定excel数据读取的每行长度//     *//     * @param sheet//     * @return//     *///    private static int getHeadColumnNum(Sheet sheet) {//        int firstRow = sheet.getFirstRowNum();//        // 循环输出表格中的内容//        int columnNum = 0;//        // 以列头来计算列数，poi这里在计算每行的列数时，如果当前行后面为空，就不计算了//        if (null != sheet.getRow(firstRow)) {//            columnNum = sheet.getRow(firstRow).getLastCellNum() - sheet.getRow(firstRow).getFirstCellNum();//        }//        return columnNum;//    }////    /**//     * 2003HSSFWorkbook//     * <p>//     * 2007XSSFWorkbook//     *//     * @param file//     * @return//     *///    private static Workbook getWorkBook(MultipartFile file) {//        // 获得文件名//        String fileName = file.getOriginalFilename();//        // 创建Workbook工作薄对象，表示整个excel//        Workbook workbook = null;//        try {//            // 获取excel文件的io流//            InputStream is = file.getInputStream();//            // 根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象//            if (fileName.endsWith("xls")) {//                // 2003//                workbook = new HSSFWorkbook(is);//            } else if (fileName.endsWith("xlsx")) {//                // 2007//                workbook = new XSSFWorkbook(is);//            }//        } catch (Exception e) {//            LOGGER.error("ExcelUtil.getWorkBook catch a exception create excel workbook fail", e);//        }//        return workbook;//    }////    /**//     * 读取单元格中的数据//     *//     * @param cell//     * @return//     *///    private static String getCellValue(Cell cell) {//        String cellValue = "";//        if (cell == null) {//            return cellValue;//        }//        // 把数字当成String来读，避免出现1读成1.0的情况//        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {//            cell.setCellType(Cell.CELL_TYPE_STRING);//        }//        // 判断数据的类型//        switch (cell.getCellType()) {//            case Cell.CELL_TYPE_NUMERIC: // 数字//                cellValue = String.valueOf(cell.getNumericCellValue());//                break;//            case Cell.CELL_TYPE_STRING: // 字符串//                cellValue = String.valueOf(cell.getStringCellValue());//                break;//            case Cell.CELL_TYPE_BOOLEAN: // Boolean//                cellValue = String.valueOf(cell.getBooleanCellValue());//                break;//            case Cell.CELL_TYPE_FORMULA: // 公式//                cellValue = String.valueOf(cell.getCellFormula());//                break;//            case Cell.CELL_TYPE_BLANK: // 空值//                cellValue = "";//                break;//            case Cell.CELL_TYPE_ERROR: // 故障//                cellValue = "非法字符";//                break;//            default://                cellValue = "未知类型";//                break;//        }//        return cellValue;//    }////    /**//     * sheet1为excel默认名字//     * <p>//     * 导出的是.xlsx文件//     *//     * @param excelName 文件名字//     * @param titles 标题也就是第一行//     * @param data 导出数据//     * @return//     *///    public static void dowLoadExcel(String excelName, HttpServletResponse response, String[] titles,//                                    List<String[]> data) {//        dowLoadExcel(excelName, response, titles, data, "sheet1");//    }////    private static void writeOut(HttpServletResponse response, XSSFWorkbook wb) {//        try {//            OutputStream out = response.getOutputStream();//            wb.write(out);//            out.close();//        } catch (IOException e) {//            LOGGER.error("ExcelUtil.writeOut catch a exception:", e);//        }//    }////    /**//     * 自定义sheet名字//     *//     * @param excelName 文件名字//     * @param titles 标题也就是第一行//     * @param data 导出数据//     * @param sheetName sheet页名字//     * @return//     *///    public static void dowLoadExcel(String excelName, HttpServletResponse response, String[] titles,//                                    List<String[]> data, String sheetName) {//        // 设置导出excel的名字和响应头//        writeHeader(response, excelName);//        XSSFWorkbook wb = new XSSFWorkbook();//        XSSFSheet sheet = wb.createSheet(sheetName);//        ExcelUtil.writeTitle(sheet, titles);//        writeAttr(sheet);//        writeFont(wb);//        ExcelUtil.wirteData(sheet, titles.length, data);//        writeOut(response, wb);//    }////    /**//     * 设置行高，行宽，针对导出2007//     *//     * @param sheet//     *///    private static void writeAttr(XSSFSheet sheet) {//        sheet.setDefaultRowHeight((short) (2 * 256));// 设置行高//        sheet.setColumnWidth(0, 4000);// 设置列宽//        sheet.setColumnWidth(1, 4000);//    }////    /**//     * 设置字体，以及字体大小，针对导出2007//     *//     * @param wb//     *///    private static void writeFont(XSSFWorkbook wb) {//        XSSFFont font = wb.createFont();//        font.setFontName("宋体");//        font.setFontHeightInPoints((short) 16);//    }////    /**//     * 写返回头//     *//     * @param response//     * @param excelName//     * @throws Exception//     *///    public static void writeHeader(HttpServletResponse response, String excelName) {//        response.setCharacterEncoding("UTF-8");//        response.setContentType("application/x-download");//        String fileName = excelName;//        try {//            fileName = URLEncoder.encode(fileName, "UTF-8");//        } catch (UnsupportedEncodingException e) {//            LOGGER.error("ExcelUtil.writeHeader catch a exception转utf-8失败", e);//        }//        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);//    }////    /**//     * 写excel第一行;通常excel第一行被称为标题行//     *//     * @param sheet//     * @param titles//     *///    private static void writeTitle(XSSFSheet sheet, String[] titles) {//        XSSFRow row = sheet.createRow(0);//        XSSFCell cell;//        for (int i = 0; i < titles.length; i++) {//            cell = row.createCell(i);//            cell.setCellValue(titles[i]);//        }//    }////    /**//     * 给即将导出的excel写数据//     *//     * @param sheet//     * @param lenth//     * @param dataArray//     *///    private static void wirteData(XSSFSheet sheet, int lenth, List<String[]> dataArray) {//        for (int i = 0; i < dataArray.size(); i++) {//            writeRow(sheet, lenth, dataArray, i);//        }//    }////    /**//     * 写一行数据//     *//     * @param sheet//     * @param lenth//     * @param dataArray//     * @param RowId//     *///    private static void writeRow(XSSFSheet sheet, int lenth, List<String[]> dataArray, int RowId) {//        String[] strings = dataArray.get(RowId);//        XSSFRow rows = sheet.createRow(RowId + 1);//        for (int j = 0; j < lenth; j++) {//            writeCell(strings[j], rows.createCell(j));//        }//    }////    /**//     * 写一个单元格数据//     *//     * @param string//     * @param cell//     *///    private static void writeCell(String string, XSSFCell cell) {//        if (StringUtils.isEmpty(string))//            string = "";//        cell.setCellValue(string);//    }////    public static String transference(String value) {//        return value.replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\\'").replaceAll("\"", "\\\\\"");//    }////    /**//     *//     * @param file//     * @param cl//     * @param <T>//     * @return mapResult{ code： 0 异常 1成功 mag: 异常信息 data：excel中解析的数据}//     *///    public static <T> Map<String, Object> readExcel(MultipartFile file, Class<T> cl) {//        Map<String, Object> mapResult = new HashMap<String, Object>();//        mapResult.put("code", "0");//        List<T> result = new ArrayList<T>();//        ExcelSheet sheetName = cl.getAnnotation(ExcelSheet.class);//        if (sheetName == null) {//            mapResult.put("msg", "excel实体类定义异常");//            return mapResult;//        }//        Workbook workbook = getWorkBook(file);//        if (workbook == null) {//            mapResult.put("msg", "未能正常解析excel");//            return mapResult;//        }//        Sheet sheet = chooseSheet(sheetName, workbook);//        // 第一行//        final int firstRowNum = sheet.getFirstRowNum();//        Map<Integer, Field> map = new HashMap<Integer, Field>();//        if (concatExcelColumAndPojoField(cl, mapResult, sheet.getRow(firstRowNum), map)) {//            return mapResult;//        }//        // 获得实际最后一行//        final int lastRowNum = getRealLastRowNum(sheet, firstRowNum);//        Row row;//        T t;//        try {//            for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {//                // 获得当前行//                row = sheet.getRow(rowNum);//                if (row == null) {//                    continue;//                }//                t = cl.newInstance();//                if (setPojo(mapResult, sheet, map, row, rowNum, t)) {//                    return mapResult;//                }//                result.add(t);//            }//        } catch (InstantiationException | IllegalAccessException e) {//            LOGGER.error("ExcelUtil.readExcel catch a exception", e);//            mapResult.put("msg", "系统异常1");//        }//        mapResult.put("code", "1");//        mapResult.put("data", result);//        return mapResult;//    }////    /**//     * 获取实际最后一行行号//     *//     * @param sheet//     * @param firstRowNum//     * @return//     *///    private static int getRealLastRowNum(Sheet sheet, int firstRowNum) {//        Row row;// 最后一行//        final int lastRowNum = sheet.getLastRowNum();//        int realRowNum = lastRowNum;//        for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {//            row = sheet.getRow(rowNum);//            if (row == null) {//                realRowNum = rowNum - 1;//                break;//            }//        }//        return realRowNum;//    }////    /**//     * 把excel数据转换成实体类数据//     *//     * @param mapResult//     * @param sheet//     * @param map//     * @param row//     * @param rowNum//     * @param t//     * @param <T>//     * @return//     *///    private static <T> boolean setPojo(Map<String, Object> mapResult, Sheet sheet, Map<Integer, Field> map, Row row,//                                       int rowNum, T t) {//        final int firstCellNum = sheet.getRow(sheet.getFirstRowNum()).getFirstCellNum();//        final int lastCellNum = sheet.getRow(sheet.getFirstRowNum()).getLastCellNum();//        Cell cell;//        String cellValue;//        Field currentField;//        try {//            for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {//                cell = row.getCell(cellNum);//                cellValue = getCellValue(cell);//                currentField = map.get(cellNum);//                if (currentField.isAnnotationPresent(NotBlank.class) && StringUtils.isBlank(cellValue)) {//                    ExcelColumn excelColumn = currentField.getAnnotation(ExcelColumn.class);//                    mapResult.put("msg",//                            "excel第" + rowNum + "行,第" + (cellNum + 1) + "列," + excelColumn.value() + "不能为空");//                    return true;//                }//                currentField.setAccessible(true);//                currentField.set(t, cellValue);//            }//        } catch (IllegalAccessException e) {//            LOGGER.error("ExcelUtil.setPojo catch a exception", e);//            mapResult.put("msg", "系统异常2");//            return true;//        }//        return false;//    }////    /**//     * 建立列和实体类的映射关系//     *//     * @param cl//     * @param mapResult//     * @param canReadFirstRow//     * @param map//     * @param <T>//     * @return//     *///    private static <T> boolean concatExcelColumAndPojoField(Class<T> cl, Map<String, Object> mapResult,//                                                            Row canReadFirstRow, Map<Integer, Field> map) {//        List<String> tmp = new ArrayList<String>();//        int columnNum = canReadFirstRow.getLastCellNum() - canReadFirstRow.getFirstCellNum();//        String[] cells = readRow(columnNum, canReadFirstRow, canReadFirstRow.getFirstCellNum());//        Field[] fields = cl.getDeclaredFields();//        ExcelColumn column;//        String value;//        for (Field field : fields) {//            if (field.isAnnotationPresent(ExcelColumn.class)) {//                column = field.getAnnotation(ExcelColumn.class);//                value = column.value().trim();//                // 重复//                if (!tmp.add(value)) {//                    mapResult.put("msg", "excel实体类定义列名重复");//                    return true;//                }//                int i;//                for (i = 0; i < cells.length; i++) {//                    if (cells[i].contains(value)) {//                        map.put(i, field);//                        break;//                    }//                }//            }//        }//        return false;//    }////    /**//     * 读取对应sheet//     *//     * @param sheetName//     * @param workbook//     * @return//     *///    private static Sheet chooseSheet(ExcelSheet sheetName, Workbook workbook) {//        String name = sheetName.value();//        Sheet sheet;//        if (StringUtils.isNotBlank(name)) {//            sheet = workbook.getSheet(name);//        } else {//            sheet = workbook.getSheetAt(INT_0);//        }//        return sheet;//    }//}