package fr.eni.tp.encheres.bll;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {
	
	@Value("${upload.path}") //Affecte la valeur égale à celle dans app.properties
	private String uploadPath;

	@Override
	public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }

        // Créer le répertoire de sauvegarde s'il n'existe pas
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Générer un nom de fichier unique
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if(originalFilename != null && originalFilename.contains(".")) {
        	// On récupère l'extension du fichier (jpg, jpeg, png etc....)
        	extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        
        Path destinationFile = Paths.get(uploadDir.getAbsolutePath(), uniqueFilename).normalize();

        // Copier le fichier uploadé dans le répertoire de destination
        Files.copy(file.getInputStream(), destinationFile);

        return uniqueFilename;
    }

	@Override
	public Resource loadFileAsResource(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

}
