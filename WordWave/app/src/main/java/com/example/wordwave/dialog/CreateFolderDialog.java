package com.example.wordwave.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.wordwave.R;
import com.example.wordwave.activity.MainActivity;
import com.example.wordwave.model.Folder;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateFolderDialog extends Dialog {

    String userId;
    EditText folderNameEdt;
    public CreateFolderDialog(@NonNull Context context, String userId) {
        super(context);
        this.setContentView(R.layout.dialog_create_folder);
        this.getWindow()
                .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setCancelable(false);

        this.userId = userId;
        MaterialButton okButton = this.findViewById(R.id.create_folder_ok_button);
        MaterialButton cancelButton = this.findViewById(R.id.create_folder_cancel_button);
        folderNameEdt = this.findViewById(R.id.create_folder_name);

        CreateFolderDialog _this = this;

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _this.dismiss();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFolder();
                _this.dismiss();
            }
        });
    }
    private void createFolder(){
        // Lấy tên thư mục từ EditText
        String folderName = folderNameEdt.getText().toString().trim();

        // Kiểm tra xem tên thư mục có rỗng không
        if (folderName.isEmpty()) {
            // Hiển thị thông báo hoặc thực hiện xử lý nếu tên thư mục rỗng
            return;
        }

        // Tạo một đối tượng Folder với thông tin cần thiết
        Folder folder = new Folder(null, userId, folderName);

        // Thêm dữ liệu của Folder lên Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("folders")
                .add(folder)
                .addOnSuccessListener(documentReference -> {
                    // Đã thêm thành công, lấy folderId từ documentReference và cập nhật vào đối tượng folder
                    String folderId = documentReference.getId();
                    folder.setFolderId(folderId);

                    // Cập nhật lại đối tượng Folder với folderId mới
                    db.collection("folders").document(folderId)
                            .set(folder)
                            .addOnSuccessListener(aVoid -> {
//                                Intent intent = new Intent(getContext(), MainActivity.class);
//                                getContext().startActivity(intent);
                                navigateBackToLibrary();
                                dismiss();
                            })
                            .addOnFailureListener(e -> {
                                // Gặp lỗi trong quá trình cập nhật, xử lý lỗi nếu cần
                            });
                })
                .addOnFailureListener(e -> {
                    // Gặp lỗi trong quá trình thêm, xử lý lỗi nếu cần
                });
    }
    private void navigateBackToLibrary() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        // Add flags to clear the back stack and set the destination to Library fragment
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("FRAGMENT_TO_LOAD", "Library"); // You may need to adjust this extra key based on your implementation
        getContext().startActivity(intent); // Optional, depending on whether you want to keep FolderActivity in the back stack
    }
}
