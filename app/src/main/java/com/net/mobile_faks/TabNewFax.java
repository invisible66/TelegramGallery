package com.net.mobile_faks;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.kcode.bottomlib.BottomDialog;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ConstFiles.Consts;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * Created by Ismail AY on 17.04.2017.
 */

public class TabNewFax extends Fragment {

    private int MAX_ATTACHMENT_COUNT = 5;

    private int reqCode_Gallery = 12;
    Consts consts;
    private Unbinder unbinder;
    FilePickerDialog dialog;

    private List<String> docsPaths = new ArrayList<>();
    private ArrayList<DosyaObj> DosyaList = new ArrayList<>();
    private int FileID = 0;

    @BindView(R.id.dosyaList)
    LinearLayout FilesLayout;

    @BindView(R.id.receipents)
    LinearLayout Numbers;

    @BindView(R.id.Dosya)
    Button DosyaEkle;

    public static TabNewFax newInstance() {
        TabNewFax fragment = new TabNewFax();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        consts = Consts.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab_newfax, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        registerForContextMenu(DosyaEkle);

        return rootView;
    }

    public void ListAllFiles() {
        LinearLayout allFiles = (LinearLayout) getActivity().findViewById(R.id.allFilesLayout);
        if (DosyaList.size() > 0) { // arayüz oluşturulsun
            if (allFiles != null) {
                allFiles.setVisibility(View.VISIBLE);
            }

            LayoutInflater inflater = LayoutInflater.from(getContext());

            View view = inflater.inflate(R.layout.file_row, null);
            TextView tvText = (TextView) view.findViewById(R.id.fileName);
            RelativeLayout RL = (RelativeLayout) view.findViewById(R.id.liner1);
            RL.setId(FileID);

            String a = DosyaList.get(DosyaList.size() - 1).getImagepath();
            int i = a.lastIndexOf('/');
            a = a.substring(i + 1, a.length());
            tvText.setText(a);

            FilesLayout.addView(view);
        } else {
            if (allFiles != null) {
                allFiles.setVisibility(View.INVISIBLE);
            }
        }

    }

    @OnClick(R.id.Dosya) //-- Dosya butonuna basılınca çağrılır
    public void DosyaTuruSec() {
        BottomDialog dialog = BottomDialog.newInstance(
                getResources().getString(R.string.fileType),
                getResources().getString(R.string.Cancel),
                new String[]{
                        getResources().getString(R.string.takePhoto),
                        getResources().getString(R.string.pickImage),
                        getResources().getString(R.string.pickFile)}
        );
        dialog.show(getChildFragmentManager(), "dialog");
        dialog.setListener(new BottomDialog.OnClickListener() {
            @Override
            public void click(int position) {
                DosyaEkle(position);
            }
        });
    }

    public void DosyaEkle(int Index) {
        switch (Index) {
            case 0: { // Fotoğraf Çek
                EasyImage.openCamera(this, 0);
                break;
            }
            case 1: { // Galeriden Seç
                int x = MAX_ATTACHMENT_COUNT - DosyaList.size();
                GalleryConfig config = new GalleryConfig.Build()
                        .limitPickPhoto(x)
                        .singlePhoto(false)
                        .hintOfPick(getResources().getString(R.string.maxFile).replace("MAX_ATTACHMENT_COUNT", "" + x))
                        .showDropDown(false)
                        .build();
                GalleryActivity.openActivity(getActivity(), reqCode_Gallery, config);
                break;
            }
            case 2: { // Dosya Seç

                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.MULTI_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File("/");
                properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                properties.offset = new File(DialogConfigs.DEFAULT_DIR);

                String fextension = "xls,doc,docx,xlsx,pdf";
                if (fextension.length() > 0) {
                    //Add extensions to be sorted from the EditText input to the array of String.
                    int commas = countCommas(fextension);

                    //Array representing extensions.
                    String[] exts = new String[commas + 1];
                    StringBuffer buff = new StringBuffer();
                    int i = 0;
                    for (int j = 0; j < fextension.length(); j++) {
                        if (fextension.charAt(j) == ',') {
                            exts[i] = buff.toString();
                            buff = new StringBuffer();
                            i++;
                        } else {
                            buff.append(fextension.charAt(j));
                        }
                    }
                    exts[i] = buff.toString();
                    properties.extensions = exts;
                } else
                    properties.extensions = null;
//---
                dialog = new FilePickerDialog(this.getActivity(), properties);
                dialog.setTitle(getResources().getString(R.string.pleasePickFile));

                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        String dosya = files[0];
                        for (String file : files) {
                            docsPaths.add(file);
                            DosyaObj dosyaX = new DosyaObj(FileID, 1, (int) new File(file).length(), file);
                            DosyaList.add(dosyaX);
                            FileID++;
                        }
                        ListAllFiles();
                    }
                });
                dialog.show();
                break;
            }
            default: {

                break;
            }

        }
    }

    private int countCommas(String fextension) {
        int count = 0;
        for (char ch : fextension.toCharArray()) {
            if (ch == ',') {
                count++;
            }
        }
        return count;
    }

    //Add this method to show Dialog when the required permission has been granted to the app.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (dialog != null) {   //Show dialog if the read permission has been granted.
                        dialog.show();
                    }
                } else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(this.getActivity(), "Permission is Required for getting list of files", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (reqCode_Gallery == requestCode && resultCode == Activity.RESULT_OK) {
            List<String> images = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
            for (String img : images) {
                DosyaObj dosyaX = new DosyaObj(FileID, 0, (int) new File(img).length(), img);
                DosyaList.add(dosyaX);
                FileID++;
            }
            ListAllFiles();
        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, this.getActivity(), new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                DosyaObj dosyaX = new DosyaObj(FileID, 0, (int) imageFile.length(), imageFile.getAbsolutePath());
                DosyaList.add(dosyaX);
                ListAllFiles();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
