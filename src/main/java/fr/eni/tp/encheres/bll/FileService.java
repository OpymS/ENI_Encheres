package fr.eni.tp.encheres.bll;

import org.springframework.web.multipart.MultipartFile;

import fr.eni.tp.encheres.bo.Article;

import java.io.IOException;

import org.springframework.core.io.Resource;

public interface FileService {

	
//	Resource loadFileAsResource(String filename);

	void saveFile(MultipartFile file, Article article) throws IOException;
}
