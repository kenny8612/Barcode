package org.k.barcode.decoder

object Code {
    enum class D1 {
        EAN8,
        EAN13,
        UPC_A,
        UPC_E,
        CodaBar,
        Code11,
        Code39,
        Code49,
        Code93,
        Code128,
        UCC_EAN128,
        INT25,
        Matrix25,
        ISBN,
        MSI,
        RSS,
        Telepen,
        Composite
    }

    enum class D2 {
        GridMatrix,
        DotCode,
        CodaBlock,
        QR,
        MaxiCode,
        MicroPDF,
        PDF417,
        Aztec,
        HanXin,
        DataMatrix
    }

    enum class Post{
        AustraliaPost,
        ChinaPost,
        JapanPostal,
        KoreanPost,
        CanadianPost,
        UKPostal,
        USPostnet
    }
}