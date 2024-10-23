package com.bookrental.bookrental.utils;

import com.bookrental.bookrental.model.Member;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QRCodeGenerator {

    public static void generateQRCode(Member member) throws WriterException, IOException {
        String qrCodePath = "C:\\Users\\Owner\\OneDrive\\Documents\\New folder";
        String qrCodeName = qrCodePath + member.getName()+member.getId()+"-QRCODE.png";
        var qrCodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix = qrCodeWriter.encode(
                "ID: "+member.getId()+"\n"+
                        "Name :"+member.getName(), BarcodeFormat.QR_CODE, 400,400);
        Path path = FileSystems.getDefault().getPath(qrCodePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

    }
}
