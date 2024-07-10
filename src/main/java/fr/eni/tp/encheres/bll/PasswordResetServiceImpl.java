package fr.eni.tp.encheres.bll;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.tp.encheres.bo.PasswordResetToken;
import fr.eni.tp.encheres.dal.PasswordResetDAO;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
	private static final Logger passwordResetServiceLogger = LoggerFactory.getLogger(PasswordResetServiceImpl.class);

    @Autowired
    private PasswordResetDAO passwordResetDAO;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    

    @Override
    @Transactional
    public PasswordResetToken createPasswordResetToken(String token, int user_id) {
    	passwordResetServiceLogger.info("Méthode createPasswordResetToken");
        String encryptedToken = passwordEncoder.encode(token);

        PasswordResetToken myToken = new PasswordResetToken(encryptedToken, user_id);
        passwordResetDAO.save(myToken);
        return myToken;
    }
    
    @Override
    @Transactional
    public PasswordResetToken generateToken(int userId) {
        String token = UUID.randomUUID().toString();
        return createPasswordResetToken(token, userId);
    }
    
    @Override
    public PasswordResetToken findByToken(String token) {
        return passwordResetDAO.findByToken(token);
    }

    @Override
    public void deleteToken(PasswordResetToken token) {
        passwordResetDAO.delete(token);
    }

 
}
