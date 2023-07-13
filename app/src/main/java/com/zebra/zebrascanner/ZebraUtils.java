package com.zebra.zebrascanner;

import java.util.HashMap;

public class ZebraUtils {
    private static final int SDLCodeTypeID_CODE_39 = 1;
    private static final int SDLCodeTypeID_CODABAR = 2;
    private static final int SDLCodeTypeID_CODE_128 = 3;
    private static final int SDLCodeTypeID_DISCRETE_2OF5 = 4;
    private static final int SDLCodeTypeID_IATA = 5;
    private static final int SDLCodeTypeID_INTERLEAVED_2OF5 = 6;
    private static final int SDLCodeTypeID_CODE_93 = 7;
    private static final int SDLCodeTypeID_UPC_A = 8;
    private static final int SDLCodeTypeID_UPC_E0 = 9;
    private static final int SDLCodeTypeID_EAN_8 = 10;
    private static final int SDLCodeTypeID_EAN_13 = 11;
    private static final int SDLCodeTypeID_CODE_11 = 12;
    private static final int SDLCodeTypeID_CODE_49 = 13;
    private static final int SDLCodeTypeID_MSI = 14;
    private static final int SDLCodeTypeID_EAN_128 = 15;
    private static final int SDLCodeTypeID_UPC_E1 = 16;
    private static final int SDLCodeTypeID_PDF_417 = 17;
    private static final int SDLCodeTypeID_CODE_16K = 18;
    private static final int SDLCodeTypeID_CODE_39_FULL_ASCII = 19;
    private static final int SDLCodeTypeID_UPC_D = 20;
    private static final int SDLCodeTypeID_CODE_39_TRIOPTIC = 21;
    private static final int SDLCodeTypeID_BOOKLAND = 22;
    private static final int SDLCodeTypeID_COUPON_CODE = 23;
    private static final int SDLCodeTypeID_NW_7 = 24;
    private static final int SDLCodeTypeID_ISBT_128 = 25;
    private static final int SDLCodeTypeID_MICRO_PDF = 26;
    private static final int SDLCodeTypeID_DATAMATRIX = 27;
    private static final int SDLCodeTypeID_QR_CODE = 28;
    private static final int SDLCodeTypeID_MICRO_PDF_CCA = 29;
    private static final int SDLCodeTypeID_POSTNET_US = 30;
    private static final int SDLCodeTypeID_PLANET_CODE = 31;
    private static final int SDLCodeTypeID_CODE_32 = 32;
    private static final int SDLCodeTypeID_ISBT_128_CON = 33;
    private static final int SDLCodeTypeID_JAPAN_POSTAL = 34;
    private static final int SDLCodeTypeID_AUSTRALIAN_POSTAL = 35;
    private static final int SDLCodeTypeID_DUTCH_POSTAL = 36;
    private static final int SDLCodeTypeID_MAXICODE = 37;
    private static final int SDLCodeTypeID_CANADIAN_POSTAL = 38;
    private static final int SDLCodeTypeID_UK_POSTAL = 39;
    private static final int SDLCodeTypeID_MACRO_PDF = 40;
    private static final int SDLCodeTypeID_MACRO_QR = 41;
    private static final int SDLCodeTypeID_MICRO_QR = 44;
    private static final int SDLCodeTypeID_AZTEC = 45;
    private static final int SDLCodeTypeID_AZTEC_RUNE = 46;
    private static final int SDLCodeTypeID_GS1_DATABAR_14 = 48;
    private static final int SDLCodeTypeID_GS1_DATABAR_LIMITED = 49;
    private static final int SDLCodeTypeID_GS1_DATABAR_EXPANDED = 50;
    private static final int SDLCodeTypeID_USPS_4CB = 52;
    private static final int SDLCodeTypeID_UPU_4STATE = 53;
    private static final int SDLCodeTypeID_ISSN = 54;
    private static final int SDLCodeTypeID_SCANLET = 55;
    private static final int SDLCodeTypeID_CUECODE = 56;
    private static final int SDLCodeTypeID_MATRIX_2OF5 = 57;
    private static final int SDLCodeTypeID_UPC_A_PLUS_2_SUPPLEMENTAL = 72;
    private static final int SDLCodeTypeID_UPC_E0_PLUS_2_SUPPLEMENTAL = 73;
    private static final int SDLCodeTypeID_EAN_8_PLUS_2_SUPPLEMENTAL = 74;
    private static final int SDLCodeTypeID_EAN_13_PLUS_2_SUPPLEMENTAL = 75;
    private static final int SDLCodeTypeID_UPC_E1_PLUS_2_SUPPLEMENTAL = 80;
    private static final int SDLCodeTypeID_CCA_EAN_128 = 81;
    private static final int SDLCodeTypeID_CCA_EAN_13 = 82;
    private static final int SDLCodeTypeID_CCA_EAN_8 = 83;
    private static final int SDLCodeTypeID_CCA_GS1_DATABAR_EXPANDED = 84;
    private static final int SDLCodeTypeID_CCA_GA1_DATABAR_LIMITED = 85;
    private static final int SDLCodeTypeID_CCA_GS1_DATABAR_14 = 86;
    private static final int SDLCodeTypeID_CCA_UPC_A = 87;
    private static final int SDLCodeTypeID_CCA_UPC_E = 88;
    private static final int SDLCodeTypeID_CCC_EAN_128 = 89;
    private static final int SDLCodeTypeID_TLC_39 = 90;
    private static final int SDLCodeTypeID_CCB_EAN_128 = 97;
    private static final int SDLCodeTypeID_CCB_EAN_13 = 98;
    private static final int SDLCodeTypeID_CCB_EAN_8 = 99;
    private static final int SDLCodeTypeID_CCB_GS1_DATABAR_EXPANDED = 100;
    private static final int SDLCodeTypeID_CCB_GS1_DATABAR_LIMITED = 101;
    private static final int SDLCodeTypeID_CCB_GS1_DATABAR_14 = 102;
    private static final int SDLCodeTypeID_CCB_UPC_A = 103;
    private static final int SDLCodeTypeID_CCB_UPC_E = 104;
    private static final int SDLCodeTypeID_SIGNATURE_CAPTURE = 105;
    private static final int SDLCodeTypeID_CHINESE_2OF5 = 114;
    private static final int SDLCodeTypeID_KOREAN_3OF5 = 115;
    private static final int SDLCodeTypeID_UPC_A_PLUS_5_SUPPLEMENTAL = 136;
    private static final int SDLCodeTypeID_UPC_E0_PLUS_5_SUPPLEMENTAL = 137;
    private static final int SDLCodeTypeID_EAN_8_PLUS_5_SUPPLEMENTAL = 138;
    private static final int SDLCodeTypeID_EAN_13_PLUS_5_SUPPLEMENTAL = 139;
    private static final int SDLCodeTypeID_UPC_E1_PLUS_5_SUPPLEMENTAL = 144;
    private static final int SDLCodeTypeID_MACRO_MICRO_PDF = 154;
    private static final int SDLCodeTypeID_GS1_DATABAR_COUPON = 180;
    private static final int SDLCodeTypeID_HANXIN = 183;



    private HashMap<Integer, String> barcodeTypeMap;


    public String getBarcodeTypeName(int type){
        return barcodeTypeMap.get(type);
    }


    public ZebraUtils(){

        barcodeTypeMap = new HashMap<Integer, String>(){
            {
                put(SDLCodeTypeID_CODE_39, "Code 39");
                put(SDLCodeTypeID_CODABAR, "Codabar");
                put(SDLCodeTypeID_CODE_128, "Code 128");
                put(SDLCodeTypeID_DISCRETE_2OF5, "Discrete (Standard) 2 of 5");
                put(SDLCodeTypeID_IATA, "IATA");
                put(SDLCodeTypeID_INTERLEAVED_2OF5, "Interleaved 2 of 5");
                put(SDLCodeTypeID_CODE_93, "Code 93");
                put(SDLCodeTypeID_UPC_A, "UPC-A");
                put(SDLCodeTypeID_UPC_E0, "UPC-E0");
                put(SDLCodeTypeID_EAN_8, "EAN-8");
                put(SDLCodeTypeID_EAN_13, "EAN-13");
                put(SDLCodeTypeID_CODE_11, "Code 11");
                put(SDLCodeTypeID_CODE_49, "Code 49");
                put(SDLCodeTypeID_MSI, "MSI");
                put(SDLCodeTypeID_EAN_128, "EAN-128");
                put(SDLCodeTypeID_UPC_E1, "UPC-E1");
                put(SDLCodeTypeID_PDF_417, "PDF-417");
                put(SDLCodeTypeID_CODE_16K, "Code 16k");
                put(SDLCodeTypeID_CODE_39_FULL_ASCII, "Code 39 Full ASCII");
                put(SDLCodeTypeID_UPC_D, "UPC-D");
                put(SDLCodeTypeID_CODE_39_TRIOPTIC, "Code 39 Trioptic");
                put(SDLCodeTypeID_BOOKLAND, "Bookland");
                put(SDLCodeTypeID_COUPON_CODE, "Coupon Code");
                put(SDLCodeTypeID_NW_7, "NW-7");
                put(SDLCodeTypeID_ISBT_128, "ISBT-128");
                put(SDLCodeTypeID_MICRO_PDF, "Micro PDF");
                put(SDLCodeTypeID_DATAMATRIX, "DataMatrix");
                put(SDLCodeTypeID_QR_CODE, "QR Code");
                put(SDLCodeTypeID_MICRO_PDF_CCA, "Micro PDF CCA");
                put(SDLCodeTypeID_POSTNET_US, "PostNet US");
                put(SDLCodeTypeID_PLANET_CODE, "Planet Code");
                put(SDLCodeTypeID_CODE_32, "Code 32");
                put(SDLCodeTypeID_ISBT_128_CON, "ISBT-128 Con");
                put(SDLCodeTypeID_JAPAN_POSTAL, "Japan Postal");
                put(SDLCodeTypeID_AUSTRALIAN_POSTAL, "Australian Postal");
                put(SDLCodeTypeID_DUTCH_POSTAL, "Dutch Postal");
                put(SDLCodeTypeID_MAXICODE, "MaxiCode");
                put(SDLCodeTypeID_CANADIAN_POSTAL, "Canadian Postal");
                put(SDLCodeTypeID_UK_POSTAL, "UK Postal");
                put(SDLCodeTypeID_MACRO_PDF, "Macro PDF");
                put(SDLCodeTypeID_MACRO_QR, "Macro QR");
                put(SDLCodeTypeID_MICRO_QR, "Micro QR");
                put(SDLCodeTypeID_AZTEC, "Aztec");
                put(SDLCodeTypeID_AZTEC_RUNE, "Aztec Rune");
                put(SDLCodeTypeID_GS1_DATABAR_14, "GS1 DataBar-14");
                put(SDLCodeTypeID_GS1_DATABAR_LIMITED, "GS1 DataBar Limited");
                put(SDLCodeTypeID_GS1_DATABAR_EXPANDED, "GS1 DataBar Expanded");
                put(SDLCodeTypeID_USPS_4CB, "USPS 4CB");
                put(SDLCodeTypeID_UPU_4STATE, "USPS 4State");
                put(SDLCodeTypeID_ISSN, "ISSN");
                put(SDLCodeTypeID_SCANLET, "Scanlet");
                put(SDLCodeTypeID_CUECODE, "CueCode");
                put(SDLCodeTypeID_MATRIX_2OF5, "Matrix 2 of 5");
                put(SDLCodeTypeID_UPC_A_PLUS_2_SUPPLEMENTAL, "UPC-A + 2 Supplemental");
                put(SDLCodeTypeID_UPC_E0_PLUS_2_SUPPLEMENTAL, "UPC-E0 + 2 Supplemental");
                put(SDLCodeTypeID_EAN_8_PLUS_2_SUPPLEMENTAL, "EAN-8 + 2 Supplemental");
                put(SDLCodeTypeID_EAN_13_PLUS_2_SUPPLEMENTAL, "EAN-13 + 2 Supplemental");
                put(SDLCodeTypeID_UPC_E1_PLUS_2_SUPPLEMENTAL, "UPC-E1 + 2 Supplemental");
                put(SDLCodeTypeID_CCA_EAN_128, "CCA EAN-128");
                put(SDLCodeTypeID_CCA_EAN_13, "CCA EAN-13");
                put(SDLCodeTypeID_CCA_EAN_8, "CCA EAN-8");
                put(SDLCodeTypeID_CCA_GS1_DATABAR_EXPANDED, "CCA GS1 DataBar Expanded");
                put(SDLCodeTypeID_CCA_GA1_DATABAR_LIMITED, "CCA GS1 DataBar Limited");
                put(SDLCodeTypeID_CCA_GS1_DATABAR_14, "CCA GS1 DataBar-14");
                put(SDLCodeTypeID_CCA_UPC_A, "CCA UPC-A");
                put(SDLCodeTypeID_CCA_UPC_E, "CCA UPC-E");
                put(SDLCodeTypeID_CCC_EAN_128, "CCA EAN-128");
                put(SDLCodeTypeID_TLC_39, "TLC-39");
                put(SDLCodeTypeID_CCB_EAN_128, "CCB EAN-128");
                put(SDLCodeTypeID_CCB_EAN_13, "CCB EAN-13");
                put(SDLCodeTypeID_CCB_EAN_8, "CCB EAN-8");
                put(SDLCodeTypeID_CCB_GS1_DATABAR_EXPANDED, "CCB GS1 DataBar Expanded");
                put(SDLCodeTypeID_CCB_GS1_DATABAR_LIMITED, "CCB GS1 DataBar Limited");
                put(SDLCodeTypeID_CCB_GS1_DATABAR_14, "CCB GS1 DataBar-14");
                put(SDLCodeTypeID_CCB_UPC_A, "CCB UPC-A");
                put(SDLCodeTypeID_CCB_UPC_E, "CCB UPC-E");
                put(SDLCodeTypeID_SIGNATURE_CAPTURE, "Signature Capture");
                put(SDLCodeTypeID_CHINESE_2OF5, "Chinese 2 of 5");
                put(SDLCodeTypeID_KOREAN_3OF5, "Korean 3 of 5");
                put(SDLCodeTypeID_UPC_A_PLUS_5_SUPPLEMENTAL, "UPC-A + 5 supplemental");
                put(SDLCodeTypeID_UPC_E0_PLUS_5_SUPPLEMENTAL, "UPC-E0 + 5 supplemental");
                put(SDLCodeTypeID_EAN_8_PLUS_5_SUPPLEMENTAL, "EAN-8 + 5 supplemental");
                put(SDLCodeTypeID_EAN_13_PLUS_5_SUPPLEMENTAL, "EAN-13 + 5 supplemental");
                put(SDLCodeTypeID_UPC_E1_PLUS_5_SUPPLEMENTAL, "UPC-E1 + 5 supplemental");
                put(SDLCodeTypeID_MACRO_MICRO_PDF, "Macro Micro PDF");
                put(SDLCodeTypeID_GS1_DATABAR_COUPON, "GS1 Databar Coupon");
                put(SDLCodeTypeID_HANXIN, "Han Xin");
            }
        };
    }
}
