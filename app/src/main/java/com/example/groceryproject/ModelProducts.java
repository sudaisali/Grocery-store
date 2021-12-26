package com.example.groceryproject;

public class ModelProducts {
   private  String  productId , productTitle , productDescription , productCategory , productQuantity , productIcon , originalPrice ,discountPrice
           ,discountnote , discountAvaliable , timestamp , uid;

   public ModelProducts(){

   }
   public ModelProducts(String productId, String productTitle, String productDescription,
                        String productCategory, String productQuantity, String productIcon,
                        String originalPrice, String discountPrice, String discountnote,
                        String discountAvaliable, String timestamp, String uid) {

      this.productId = productId;
      this.productTitle = productTitle;
      this.productDescription = productDescription;
      this.productCategory = productCategory;
      this.productQuantity = productQuantity;
      this.productIcon = productIcon;
      this.originalPrice = originalPrice;
      this.discountPrice = discountPrice;
      this.discountnote = discountnote;
      this.discountAvaliable = discountAvaliable;
      this.timestamp = timestamp;
      this.uid = uid;
   }

   public String getProductId() {
      return productId;
   }

   public void setProductId(String productId) {
      this.productId = productId;
   }

   public String getProductTitle() {
      return productTitle;
   }

   public void setProductTitle(String productTitle) {
      this.productTitle = productTitle;
   }

   public String getProductDescription() {
      return productDescription;
   }

   public void setProductDescription(String productDescription) {
      this.productDescription = productDescription;
   }

   public String getProductCategory() {
      return productCategory;
   }

   public void setProductCategory(String productCategory) {
      this.productCategory = productCategory;
   }

   public String getProductQuantity() {
      return productQuantity;
   }

   public void setProductQuantity(String productQuantity) {
      this.productQuantity = productQuantity;
   }

   public String getProductIcon() {
      return productIcon;
   }

   public void setProductIcon(String productIcon) {
      this.productIcon = productIcon;
   }

   public String getOriginalPrice() {
      return originalPrice;
   }

   public void setOriginalPrice(String originalPrice) {
      this.originalPrice = originalPrice;
   }

   public String getDiscountPrice() {
      return discountPrice;
   }

   public void setDiscountPrice(String discountPrice) {
      this.discountPrice = discountPrice;
   }

   public String getDiscountnote() {
      return discountnote;
   }

   public void setDiscountnote(String discountnote) {
      this.discountnote = discountnote;
   }

   public String getDiscountAvaliable() {
      return discountAvaliable;
   }

   public void setDiscountAvaliable(String discountAvaliable) {
      this.discountAvaliable = discountAvaliable;
   }

   public String getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(String timestamp) {
      this.timestamp = timestamp;
   }

   public String getUid() {
      return uid;
   }

   public void setUid(String uid) {
      this.uid = uid;
   }
}
