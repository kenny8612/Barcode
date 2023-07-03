package org.k.barcode.decoder

enum class Code(val aliasName: String) {
    EAN8("EAN-8"),
    EAN13("EAN-13"),
    UPC_A("UPC-A"),
    UPC_E("UPC-E"),
    CodaBar("CodaBar"),
    Code11("CODE 11"),
    Code39("CODE 39"),
    Code93("CODE 93"),
    Code128("CODE 128"),
    INT25("Interleaved 2 of 5"),
    Matrix25("Matrix 2 of 5"),
    MSI("MSI"),
    Telepen("Telepen"),
    GridMatrix("Grid Matrix"),
    DotCode("DotCode"),
    CodaBlock("CodaBlock"),
    QR("QR"),
    MaxiCode("MaxiCode"),
    MicroPDF("MicroPDF"),
    PDF417("PDF417"),
    Aztec("Aztec"),
    HanXin("Han Xin"),
    DataMatrix("Data Matrix")
}