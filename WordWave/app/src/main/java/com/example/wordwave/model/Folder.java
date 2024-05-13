package com.example.wordwave.model;

public class Folder {
    String folderId;
    String folderName;
    String userId;


    public Folder() {
        // Default constructor required for calls to DataSnapshot.getValue(Folder.class)
    }
    public Folder(String folderName, String userId){
        this.userId = userId;
        this.folderName = folderName;
    }
    public Folder(String folderId, String userId, String folderName) {
        this.folderId = folderId;
        this.userId = userId;
        this.folderName = folderName;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
