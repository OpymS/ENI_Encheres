package fr.eni.tp.encheres.dal;

import fr.eni.tp.encheres.bo.PasswordResetToken;

public interface PasswordResetDAO {
	
    void save(PasswordResetToken token);
    PasswordResetToken findByUserId(int userId);

}
