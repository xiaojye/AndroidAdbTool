import com.android.ddmlib.AndroidDebugBridge;
import com.android.tools.build.bundletool.device.DdmlibAdbServer;

import java.lang.reflect.Field;

/**
 * @auth 二宁
 * @date 2023/12/4
 */
public class BundleUtilJava {
    /**
     * BundleToolMain 用完ADB后会关闭
     * 想个法子给他设置成未初始化状态，下次用的时候它会重新初始化
     * 需要将DdmlibAdbServer的state改为DdmlibAdbServer.State.UNINITIALIZED
     * 需要将AndroidDebugBridge的sThis改为null
     */
    public static void resetAdbServer() {
        try {
            Class<?> clz = Class.forName("com.android.tools.build.bundletool.device.DdmlibAdbServer$State");
            Object[] objects = clz.getEnumConstants();
            Object UNINITIALIZED = null;
            for (Object obj : objects){
                if(obj.toString().equals("UNINITIALIZED")){
                    UNINITIALIZED = obj;
                    break;
                }
            }
            if(UNINITIALIZED != null){
                System.out.println("已找到com.android.tools.build.bundletool.device.DdmlibAdbServer.State.UNINITIALIZED");
                Class<? extends DdmlibAdbServer> ddmlibAdbServer = DdmlibAdbServer.getInstance().getClass();
                Field state = ddmlibAdbServer.getDeclaredField("state");
                state.setAccessible(true);
                state.set(DdmlibAdbServer.getInstance(), UNINITIALIZED);
                state.setAccessible(false);
                System.out.println("已将DdmlibAdbServer.state设置为UNINITIALIZED");
            }else{
                System.out.println("未找到com.android.tools.build.bundletool.device.DdmlibAdbServer.State.UNINITIALIZED");
            }

            Field sInitialized = AndroidDebugBridge.class.getDeclaredField("sThis");
            sInitialized.setAccessible(true);
            sInitialized.set(null,null);
            sInitialized.setAccessible(false);
            System.out.println("已将AndroidDebugBridge.sThis设置为null");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
