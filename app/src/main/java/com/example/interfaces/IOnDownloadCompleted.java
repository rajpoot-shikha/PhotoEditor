package com.example.interfaces;

import com.example.model.Image;

import java.util.ArrayList;

/**
 * Created by Shikha.Rajpoot on 1/24/2018.
 */

public interface IOnDownloadCompleted {
    void onDownloadCompleted(ArrayList <Image> images) throws InterruptedException;
}
