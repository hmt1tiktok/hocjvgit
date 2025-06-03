import java.util.Scanner;

public class helloworld {

    public static String giaiPhuongTrinhBacNhat(double a, double b) {
        if (a == 0) {
            if (b == 0) {
                return "Phương trình vô số nghiệm";
            } else {
                return "Phương trình vô nghiệm";
            }
        } else {
            double x = -b / a;
            return "Phương trình có nghiệm x = " + x;
        }
    }
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        System.out.println("Tự học Java cơ bản");
        System.out.println("Học Java để làm gì?");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập hệ số a: ");
        double a = scanner.nextDouble();
        System.out.print("Nhập hệ số b: ");
        double b = scanner.nextDouble();
        String result = giaiPhuongTrinhBacNhat(a, b);  
        System.out.println(result);
        scanner.close();
    }
}