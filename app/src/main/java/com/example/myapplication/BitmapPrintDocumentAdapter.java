package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import java.io.IOException;

public class BitmapPrintDocumentAdapter extends PrintDocumentAdapter {
    private final Context context;
    private final Bitmap qrCodeBitmap;
    private final String codeText;
    private PrintedPdfDocument pdfDocument;

    public BitmapPrintDocumentAdapter(Context context, Bitmap qrCodeBitmap, String codeText) {
        this.context = context;
        this.qrCodeBitmap = qrCodeBitmap;
        this.codeText = codeText;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         PrintDocumentAdapter.LayoutResultCallback callback,
                         Bundle metadata) {
        pdfDocument = new PrintedPdfDocument(context, newAttributes);
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }
        PrintDocumentInfo info = new PrintDocumentInfo.Builder("QRCode.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1)
                .build();
        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal,
                        PrintDocumentAdapter.WriteResultCallback callback) {
        PdfDocument.Page page = pdfDocument.startPage(0);
        Canvas canvas = page.getCanvas();

        int qrSize = 300;
        int left = (canvas.getWidth() - qrSize) / 2;
        canvas.drawBitmap(qrCodeBitmap, left, 100, null);

        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(24);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setTextAlign(Paint.Align.CENTER);

        float textY = 100 + qrSize + 20;
        canvas.drawText(codeText, canvas.getWidth()/2, textY, textPaint);

        pdfDocument.finishPage(page);

        try {
            pdfDocument.writeTo(new java.io.FileOutputStream(destination.getFileDescriptor()));
            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
        } catch (IOException e) {
            callback.onWriteFailed(e.getMessage());
        } finally {
            pdfDocument.close();
            pdfDocument = null;
        }
    }
}