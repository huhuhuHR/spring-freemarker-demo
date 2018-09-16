
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Test {
    @org.junit.Test
    public void test() throws Exception {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("/Users/huorong/Downloads/txt.txt"), "gbk"));
        char[] chs = new char[1024];
        int length = -1;
        String str;
        StringBuilder sb = new StringBuilder();
        while ((length = br.read(chs)) != -1) {
            str = String.valueOf(chs, 0, length);
            sb.append(str);
        }
        String result = sb.toString();
        System.out.println(result);
        br.close();
    }
}
