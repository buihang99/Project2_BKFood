package hangbt.hust.bkfoodserver.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import retrofit2.Retrofit;

public class Common {
    public static User currenUser;
    public static Order currenOrder;

    public static String USER = "User";
    public static String PWUSER = "Pass";

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static final String baseUrl = "http://maps.googleapis.com";

    private static final String BASE_URL = "http:/fcm.googleapis.com/";

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null){
                for(int i=0; i<info.length; i++){
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static String convertStatus(int code){
        if(code == 0){
            return "Đã xác nhận";
        }else if(code == 1){
            return "Đang giao";
        }else{
            return "Đã giao";
        }
    }

        public static Bitmap scaleBitmap(Bitmap bitmap,int newWidth, int newHeight){
        Bitmap scaleBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);

        float scaleX = newWidth/(float)bitmap.getWidth();
        float scaleY = newHeight/(float)bitmap.getHeight();
        float pivotX = 0, pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(bitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaleBitmap;
    }

}
