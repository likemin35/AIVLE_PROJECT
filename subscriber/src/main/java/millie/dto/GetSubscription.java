package millie.dto;

import java.util.Date;

public class GetSubscription {
    private Long id;
    private Long userId;
    private Long bookId;
    private Boolean isSubscription;
    private Date rentalStart;
    private Date rentalEnd;
    private String webUrl;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public Boolean getIsSubscription() { return isSubscription; }
    public void setIsSubscription(Boolean isSubscription) { this.isSubscription = isSubscription; }

    public Date getRentalStart() { return rentalStart; }
    public void setRentalStart(Date rentalStart) { this.rentalStart = rentalStart; }

    public Date getRentalEnd() { return rentalEnd; }
    public void setRentalEnd(Date rentalEnd) { this.rentalEnd = rentalEnd; }

    public String getWebUrl() { return webUrl; }
    public void setWebUrl(String webUrl) { this.webUrl = webUrl; }
}
