package fr.eni.tp.encheres.bll;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import org.springframework.core.io.Resource;

public interface FileService {

	String saveFile(MultipartFile file) throws IOException;
	
	Resource loadFileAsResource(String filename);
}
