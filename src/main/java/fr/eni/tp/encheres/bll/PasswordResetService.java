package fr.eni.tp.encheres.bll;

import fr.eni.tp.encheres.bo.PasswordResetToken;
import fr.eni.tp.encheres.bo.User;

public interface PasswordResetService {
	
    PasswordResetToken createPasswordResetToken(String token, int user_id);
    PasswordResetToken generateToken(int userId);
    PasswordResetToken findByToken(String token);
    void deleteToken(PasswordResetToken token);

}
