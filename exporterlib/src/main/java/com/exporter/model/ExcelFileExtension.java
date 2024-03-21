package com.exporter.model;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//Useage : function(@Response.Visibility int visibility)
@StringDef({ExcelFileExtension.XLS, ExcelFileExtension.XLSX})
@Retention(RetentionPolicy.SOURCE)
public @interface ExcelFileExtension {
    String XLS = "xls";
    String XLSX = "xlsx";
}