package fr.eni.tp.encheres.bll;

public interface PasswordResetService {
	
	void sendEmail(String to, String subject, String text);

}
