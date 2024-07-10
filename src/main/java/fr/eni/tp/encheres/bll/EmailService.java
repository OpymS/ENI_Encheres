package fr.eni.tp.encheres.bll;

import fr.eni.tp.encheres.exception.BusinessException;

public interface EmailService {
	
	void sendPasswordResetEmail(String to, String token) throws BusinessException;

}
