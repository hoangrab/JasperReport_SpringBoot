package com.hoan.testjasperreport.controller;

import com.hoan.testjasperreport.entity.User;
import com.hoan.testjasperreport.repository.UserRepository;
import com.hoan.testjasperreport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("api/v1/")
public class HomeController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("exportExcel")
    public ResponseEntity<?> downLoadFileByMonth() {
        List<User> response = userRepository.findAll();
        File fileToDownload = new File(userService.buildFileExportUser(response));
        if (fileToDownload != null) {
            try {
                InputStream inputStream = new FileInputStream(fileToDownload);
                String fileName = "testjasper.xls";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", fileName);
                InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(inputStreamResource);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("fakeData")
    public ResponseEntity<?> fakeData() {
        try {
            User user = new User();
            user.setFirstName("hoan");
            user.setLastName("ga");
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
