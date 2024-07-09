package fr.eni.tp.encheres.bll;

public interface EmailService {
	
	void sendPasswordResetEmail(String to, String token);

}
