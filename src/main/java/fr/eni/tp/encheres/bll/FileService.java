package fr.eni.tp.encheres.bll;

import org.springframework.web.multipart.MultipartFile;

import fr.eni.tp.encheres.bo.Article;
import jakarta.validation.Valid;

import java.io.IOException;

public interface FileService {

	
//	Resource loadFileAsResource(String filename);

	void saveFile(MultipartFile file, Article article) throws IOException;

	void saveFileFromModify(MultipartFile fileImage, @Valid Article article) throws IOException;
}
