package com.example;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * Created by Andreas on 20.11.2015.
 */
@Controller
public class HelloController {

    @Autowired
    GridFsTemplate gridFsTemplate;

    @RequestMapping("/")
    public String hello(){
        return "hello";
    }

    @RequestMapping(value="/upload", method= RequestMethod.POST)
    public @ResponseBody String handleFileUpload(@RequestParam("name") String name, @RequestParam("file") MultipartFile file){
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                InputStream inputStream = new ByteArrayInputStream(bytes);
                System.out.println(file.getContentType());
                gridFsTemplate.store(inputStream, name, file.getContentType());

                return "You successfully uploaded " + name + "!";
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + name + " because the file was empty.";
        }
    }

    @RequestMapping(value="/photo/{fileName}")
    public @ResponseBody ResponseEntity<InputStreamResource> getFile(@PathVariable String fileName){
        GridFSDBFile file = gridFsTemplate.findOne(new Query(Criteria.where("filename").is("halloween")));
        System.out.println(file);
        return ResponseEntity.ok()
                .contentLength(file.getLength())
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(new InputStreamResource(file.getInputStream()));
    }
}
