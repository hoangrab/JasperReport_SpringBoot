package com.hoan.testjasperreport.service;

import com.hoan.testjasperreport.entity.User;
import com.hoan.testjasperreport.repository.UserRepository;
import com.hoan.testjasperreport.util.JasperReportExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import org.apache.commons.io.FileUtils;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.*;
import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Value("${TEMPLATE_PATH}")
    private String templateFolder;

    @Value("${FILE_TEMP_UPLOAD_PATH}")
    private String fileNameFullFolder;

    @Autowired
    private DataSource dataSource;

    public String buildFileExportUser(List<User> users) {
        Map<String, Object> paramsReport = new HashMap<>();
        paramsReport.put("sl", users.size());
        paramsReport.put("p_sql", "select * from users");
        String templateFolder = this.templateFolder;
        String fileName = "ranh_qua_viet_vui_" + Calendar.getInstance().getTimeInMillis() + ".xlsx";
        String fileNameFull = this.fileNameFullFolder + File.separator + fileName;

        File templateFile = new File(templateFolder, "jasperrach.jrxml");
        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(templateFile.getAbsolutePath());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, paramsReport, dataSource.getConnection());
            SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
            configuration.setOnePagePerSheet(true);
            configuration.setDetectCellType(true); // Detect cell types (date and etc.)
            configuration.setWhitePageBackground(false); // No white background!
            configuration.setFontSizeFixEnabled(false);

            // No spaces between rows and columns
            configuration.setRemoveEmptySpaceBetweenRows(true);
            configuration.setRemoveEmptySpaceBetweenColumns(true);

            JasperReportExporter instance = new JasperReportExporter();
            byte[] fileInBytes = instance.exportToXlsx(jasperPrint, "Test vui thoi");
            FileUtils.writeByteArrayToFile(new File(fileNameFull), fileInBytes);

            return fileNameFull;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
