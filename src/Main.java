import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {

    public static long[] newLongArray(int count) {

        long[] result = new long[count];
        for (int i = 0; i < result.length; i++) {
            result[i] = (int) (Math.random() * Integer.MAX_VALUE);
        }

        return result;
    }

    public static long[] newLongArrayMulti(int count) throws InterruptedException {

        long[] result = new long[count];
        List<Callable<Boolean>> callableArrayList = new ArrayList<>();
        int forCore = count / Pool.cores;

        for (int i = 0; i < Pool.cores; i++) {
            int start = (i == 0) ? 0 : forCore * i;
            int end = (i == 0) ? forCore : forCore * (i + 1);
            end = (i == Pool.cores - 1) ? count : end;
            final int finalEnd = end;

            callableArrayList.add(() -> {
                for (int n = start; n < finalEnd; n++) {
                    result[n] = (int) (Math.random() * Integer.MAX_VALUE);
                }
                return true;
            });
        }
        //List<Future<Boolean>> futureResult = Pool.pool.invokeAll(callableArrayList);
        Pool.pool.invokeAll(callableArrayList);
        return result;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MyTimer.start();
        long[] multiArray= newLongArray(499_999_999);
        System.out.println(Arrays.stream(multiArray).filter(x -> x == 0).count());
        MyTimer.end();

        multiArray = null;

        MyTimer.start();
        multiArray = newLongArrayMulti(499_999_999);
        System.out.println(Arrays.stream(multiArray).filter(x -> x == 0).count());
        MyTimer.end();

        Pool.pool.shutdown();
    }
}
