package com.ixuea.android.downloader.domain;

public class ItemInfo {

    String id;//downloadInfo.id ->fileName: screenName_itemId_fileName
    String itemId;//
    String userId;
    String userName;
    String userAvatar;
    String brefText;
    String preview;
    long createAt;
    int rating = -1;//评分
    int status;//是否在硬盘上
    String origin;//原始数据
    String extra;

    public String getId() {
        return id;
    }

    public ItemInfo setId(String id) {
        this.id = id;
        return this;
    }

    public String getItemId() {
        return itemId;
    }

    public ItemInfo setItemId(String itemId) {
        this.itemId = itemId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public ItemInfo setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public ItemInfo setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public ItemInfo setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
        return this;
    }

    public String getBrefText() {
        return brefText;
    }

    public ItemInfo setBrefText(String brefText) {
        this.brefText = brefText;
        return this;
    }

    public String getPreview() {
        return preview;
    }

    public ItemInfo setPreview(String preview) {
        this.preview = preview;
        return this;
    }

    public int getRating() {
        return rating;
    }

    public ItemInfo setRating(int rating) {
        this.rating = rating;
        return this;
    }

    public String getOrigin() {
        return origin;
    }

    public ItemInfo setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public ItemInfo setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getExtra() {
        return extra;
    }

    public ItemInfo setExtra(String extra) {
        this.extra = extra;
        return this;
    }

    public long getCreateAt() {
        return createAt;
    }

    public ItemInfo setCreateAt(long createAt) {
        this.createAt = createAt;
        return this;
    }
}
