package fr.eni.tp.encheres.bo;

import java.util.Date;

public class PasswordResetToken {

	private int id;
    private String token;
    private int user_id;
    private Date expiryDate;

    public PasswordResetToken() {
        super();
    }

    public PasswordResetToken(final String token) {
        this.token = token;
    }

    public PasswordResetToken(final String token, final int user_id) {
        this.token = token;
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int i) {
        this.id = i;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public int getUserId() {
        return this.user_id;
    }

    public void setUserId(final int user_id) {
        this.user_id = user_id;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final Date expiryDate) {
        this.expiryDate = expiryDate;
    }

}
