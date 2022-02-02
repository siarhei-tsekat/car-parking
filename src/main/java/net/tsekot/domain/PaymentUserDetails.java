package net.tsekot.domain;

public class PaymentUserDetails {
    private String userId;
    private String email;

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public PaymentUserDetails(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    @Override
    public String toString() {
        return "PaymentUserDetails{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
