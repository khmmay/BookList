package com.may.android.booklist;

/**
 * Created by Henrik on 21.06.2017.
 */

public class Book {
    private String title;
    private String author;
    private String imageURI;
    private String price;
    private String link;

    public Book(String mTitle,String mAuthor,String mImageURI,String mPrice,String mLink){
        title=mTitle;
        author=mAuthor;
        imageURI=mImageURI;
        price=mPrice;
        link=mLink;
    }

    public String getTitle(){return title;}
    public String getAuthor(){return author;}
    public String getImageURI(){return imageURI;}
    public String getPrice(){return price;}
    public String getLink(){return link;}


}
