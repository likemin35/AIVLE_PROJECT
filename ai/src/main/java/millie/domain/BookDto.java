package millie.domain;

public class BookDto {
    private String title;
    private String content;
    private String authorId;
    private String category;
    private boolean isBestSeller;
    private int viewCount;
    private String keywords;

    // Getter / Setter
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isBestSeller() { return isBestSeller; }
    public void setBestSeller(boolean bestSeller) { isBestSeller = bestSeller; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
}
