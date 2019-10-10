package  com.emamaker.voxelengine.utils.math;

import static com.emamaker.voxelengine.utils.VoxelSettings.chunkSize;

import java.math.BigDecimal;

import com.badlogic.gdx.math.Vector3;


public class MathHelper {

    public static double biggestNumberInList(double... n) {
        double d = 0;
        for (int i = 0; i < n.length; i++) {
            if (n[i] > d) {
                d = n[i];
            }
        }
        return d;
    }

    public static double lowestNumberInList(double... n) {
        double d = 0;
        for (int i = 0; i < n.length; i++) {
            if (n[i] < d) {
                d = n[i];
            }
        }
        return d;
    }

    public static Vector3 lowestVectorInList(Vector3... n) {
        Vector3 v1 = new Vector3(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        for (Vector3 v2 : n) {
            if (v2.x <= v1.x && v2.y <= v1.y && v2.z <= v1.z) {
                v1 = v2;
            }
        }
        return v1;
    }

    public static Vector3 castVectorToInt(Vector3 v) {
        return new Vector3((int) v.x, (int) v.y, (int) v.z);
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    /*public static int flatChunk3Dto1D(int x, int y, int z) {
        //return (z * MAXX * MAXY) + (y * MAXX) + x;
        return x + y * MAXX + z * MAXX * MAXZ;
    }*/

    public static int flatCell3Dto1D(int x, int y, int z) {
        if (x >= 0 && y >= 0 && z >= 0 && x < chunkSize && y < chunkSize && z < chunkSize) {
            return (z * chunkSize * chunkSize) + (y * chunkSize) + x;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public static int[] cell1Dto3D(int idx) {
        if (idx < chunkSize * chunkSize * chunkSize) {
            final int z = idx / (chunkSize * chunkSize);
            idx -= (z * chunkSize * chunkSize);
            final int y = idx / chunkSize;
            final int x = idx % chunkSize;
            return new int[]{x, y, z};
        } else {
            return new int[]{};
        }
    }

}
