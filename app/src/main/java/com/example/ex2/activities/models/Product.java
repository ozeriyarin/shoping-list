package com.example.ex2.activities.models;

@SuppressWarnings("unused")
public class Product {
    private String id;
    private String name;
    private String description;
    private int quantity;
    private String imageUrl;

    public Product() { }

    public Product(String id, String name, String description,
                   int quantity, String imageUrl) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.quantity    = quantity;
        this.imageUrl    = imageUrl;
    }

    public String getId()        { return id; }
    public String getName()      { return name; }
    public String getDescription(){ return description; }
    public int    getQuantity()  { return quantity; }
    public String getImageUrl()  { return imageUrl; }

    public void setId(String id)               { this.id = id; }
    public void setName(String name)           { this.name = name; }
    public void setDescription(String desc)    { this.description = desc; }
    public void setQuantity(int quantity)      { this.quantity = quantity; }
    public void setImageUrl(String imageUrl)   { this.imageUrl = imageUrl; }
}
