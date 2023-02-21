package com.phonetaxx.utils;

import android.os.Environment;

import java.io.File;

public class MediaConst {
    private static MediaConst INSTANCE;
    public final String IMAGE_DIRECTORY_PATH;
    public final String PDF_DIRECTORY_PATH;
    public final String CSV_DIRECTORY_PATH;
    public final String XL_DIRECTORY_PATH;

    private final String PARENT_FOLDER = "/PhoneTaxx";
    private final String IMAGE_FOLDER = "/Images";
    private final String PDF_FOLDER = "/pdf";
    private final String CSV_FOLDER = "/csv";
    private final String XL_FOLDER = "/xl";

    public MediaConst() {
        IMAGE_DIRECTORY_PATH = GetExternalStorage()
                + PARENT_FOLDER
                + IMAGE_FOLDER;

        PDF_DIRECTORY_PATH = GetExternalStorage()
                + PARENT_FOLDER
                + PDF_FOLDER;

        CSV_DIRECTORY_PATH
                = GetExternalStorage()
                + PARENT_FOLDER
                + CSV_FOLDER;

        XL_DIRECTORY_PATH = GetExternalStorage()
                + PARENT_FOLDER
                + XL_FOLDER;

    }

    public static MediaConst getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MediaConst();
        }
        return INSTANCE;
    }

    public String GetExternalStorage() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public File CreateFileStructure() {
        File mParentFolder = new File(Environment.getExternalStorageDirectory() + PARENT_FOLDER);
        CheckIfDirExist(mParentFolder);
        File mSubFolderFolder = new File(Environment.getExternalStorageDirectory() + PARENT_FOLDER + IMAGE_FOLDER);
        CheckIfDirExist(mSubFolderFolder);
        File mSubpdfFolderFolder = new File(Environment.getExternalStorageDirectory() + PARENT_FOLDER + PDF_FOLDER);
        CheckIfDirExist(mSubpdfFolderFolder);
        File mSubcsvFolderFolder = new File(Environment.getExternalStorageDirectory() + PARENT_FOLDER + CSV_FOLDER);
        CheckIfDirExist(mSubcsvFolderFolder);
        File mSubxlFolderFolder = new File(Environment.getExternalStorageDirectory() + PARENT_FOLDER + XL_FOLDER);
        CheckIfDirExist(mSubxlFolderFolder);
        return mParentFolder;
    }

    public void CheckIfDirExist(File mFileObject) {
        if (!mFileObject.exists()) {
            mFileObject.mkdir();
        } else {
            if (!mFileObject.isDirectory()) {
                mFileObject.mkdir();
            }
        }
    }


}
