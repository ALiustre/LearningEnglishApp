package com.example.wordwave.fragment.library.folder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wordwave.model.Folder;

import java.util.List;

public class FolderViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Folder>> foldersLiveData = new MutableLiveData<>();

    public void setFolders(List<Folder> folders) {
        foldersLiveData.setValue(folders);
    }

    public LiveData<List<Folder>> getFolders() {
        return foldersLiveData;
    }
}