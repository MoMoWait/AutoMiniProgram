package cn.edu.fjnu.autominiprogram.hkh;

import android.util.Log;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.Location;
import com.baidu.ocr.sdk.model.Word;
import com.baidu.ocr.sdk.model.WordSimple;

import java.io.File;

/**
 * Created by hkh on 18-3-10.
 */

public class Ocr {
    private static final String TAG = "Ocr";
    interface ServiceListener {
        public void onResult(String name, Location location);
    }

    public static void RecognizeText(final ServiceListener listener, String filePath){
        // 通用文字识别参数设置
        GeneralParams param = new GeneralParams();
        param.setDetectDirection(true);
        param.setImageFile(new File(filePath));

        // 调用通用文字识别服务（含位置信息版）
        OCR.getInstance().recognizeGeneral(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                StringBuilder sb = new StringBuilder();
                for (WordSimple wordSimple : result.getWordList()) {
                    // word包含位置
                    Word word = (Word) wordSimple;
                    sb.append(word.getWords());
                    sb.append("\n");
                    listener.onResult(word.getWords(), word.getLocation());
                }
                // 调用成功，返回GeneralResult对象，通过getJsonRes方法获取API返回字符串
                //listener.onResult(result.getJsonRes());
                Constant.mContinue = true;
            }
            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError对象
                Log.e(TAG, "onError");
            }
        });
    }
}
