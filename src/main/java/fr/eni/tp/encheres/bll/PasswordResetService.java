package fr.eni.tp.encheres.bll;

import fr.eni.tp.encheres.bo.PasswordResetToken;

public interface PasswordResetService {
	
    PasswordResetToken createPasswordResetToken(String token, int user_id);

}
